package com.pm.stack;

import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalStack extends Stack  {

    private final Vpc vpc;
    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);
        this.vpc = createVpc();
        DatabaseInstance authServiceDB = createDatabase("AuthServiceDB", "auth-service-db");
        DatabaseInstance patientServiceDB = createDatabase("PatientServiceDB", "patient-service-db");
        CfnHealthCheck authServiceDBHealthCheck = createDbHealthCheck(authServiceDB, "AuthServiceDBHealthCheck");
        CfnHealthCheck patientServiceDBHealthCheck = createDbHealthCheck(patientServiceDB, "PatientServiceDBHealthCheck");
        CfnCluster mskCluster = createMskCluser();
        this.ecsCluster = createEcsCluser();

        FargateService authService = createFargateService(
                "AuthService",
                "auth-service",
                List.of(4005),
                authServiceDB,
                Map.of("JWT_SECRET","JWT_SECRET=d939017c19a4227d0e6fe26e3c7fa237713dbe400e57d3b6aabcffc14509fedab6a3cf3bd98bb9a6300ee11ad68286af75079f80d83aa95ec0e093469472db5a74414fe23834f4d6ef3e54de2068959eca22eef9ea39be68d07df6fecb25ae0d5305e1f9e80109c5cb84c7d2ca95d7ba8bcf8b897325676b6d1672ef24f7364b419c2a020525c9d60db3bfc33663c8d2255f2cc2c98d53910a6b108f3bbc268374a1fda410ce986a5dc5f5b600c5cdcc89dd6acd4e6f49a359cc73436b0ecd0d53c814788dea5bac7dffc990b3308f49cb82df1ee4ed156d494202acb3ee999816a010c4869b4c9b15822cb06e3219e46ad3537680472702ca5dcd2bcc31f6dd" )
        );

        authService.getNode().addDependency(authServiceDBHealthCheck);
        authService.getNode().addDependency(authServiceDB);

        FargateService billingService = createFargateService(
                "BillingService",
                "billing-service",
                List.of(4001,9001),
                null,
                null
        );

        FargateService analyticsService = createFargateService(
                "AnalyticsService",
                "analytics_service",
                List.of(4002),
                null,
                null
        );

        analyticsService.getNode().addDependency(mskCluster);

        FargateService patientService = createFargateService(
                "PatientService",
                "patient-service",
                List.of(4000),
                patientServiceDB,
                Map.of(
                "BILLING_UNDER_SERVICE_ADDRESS","host.docker.internal",
                "BILLING_SERVICE_GRPC_PORT","9001"
                )
        );
        patientService.getNode().addDependency(patientServiceDBHealthCheck);
        patientService.getNode().addDependency(patientServiceDB);
        patientService.getNode().addDependency(mskCluster);
        patientService.getNode().addDependency(billingService);

        createApiGatewayService();

    }

    private Vpc createVpc() {
        return Vpc.Builder
            .create(this,"PatientManagementVPC")
            .vpcName("PatientManagementVPC")
            .maxAzs(2)
            .build();
    }

    private DatabaseInstance createDatabase(String id, String dbName) {
        return DatabaseInstance.Builder
                .create(this,id)
                .engine(DatabaseInstanceEngine.postgres(
                        PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_17_2)
                                .build()
                ))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)) // not needed for localstack
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY) ///  good for dev only, gives fresh data each time stack is created
                .build();
    }

    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id ) {
        return CfnHealthCheck.Builder.create(this,id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                        .ipAddress(db.getDbInstanceEndpointAddress())
                        .requestInterval(30)
                        .failureThreshold(3)
                        .build())
                .build();
    }

    private CfnCluster createMskCluser() {
        return CfnCluster.Builder.create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.0")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty
                        .builder()
                        .instanceType("kafka.m5.xlarge")
                        .clientSubnets(vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId).collect(Collectors.toList()))
                        .brokerAzDistribution("DEFAULT")
                        .build()
                )
                .build();
    }

    private Cluster createEcsCluser() {
        return Cluster.Builder.create(this,"PatientManagementCluster")
                .clusterName("PatientManagementCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder().name("patient-management.local").build())
                .build();

    }

    private FargateService createFargateService(
            String id,
            String imageName,
            List<Integer> ports,
            DatabaseInstance db,
            Map<String, String> additionalEnvVars)
    {
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, id + "Task")
                .memoryLimitMiB(512)
                .cpu(256)
                .build();

        ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(imageName))
                .portMappings(ports.stream().map(port -> PortMapping.builder()
                        .containerPort(port)
                        .hostPort(port)
                        .protocol(Protocol.TCP)
                        .build()).toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
        .logGroup(LogGroup.Builder.create(this,id + "LogGroup")
                .logGroupName("/ecs/" + imageName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .retention(RetentionDays.ONE_DAY)
                .build())
        .streamPrefix(imageName)
        .build()));

        Map<String, String> envVars = new HashMap<>();
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost:localstack.cloud:4510, localhost:localstack.cloud:4511, localhost:localstack.cloud:4512");
        if(additionalEnvVars != null) {
            envVars.putAll(additionalEnvVars);
        }
        if(db != null) {
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(), imageName));
            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "600000");
        }
        containerOptions.environment(envVars);
        taskDefinition.addContainer(imageName + "Container", containerOptions.build());
        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
                .serviceName(imageName)
                .build();
    }

    private void createApiGatewayService() {
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this,  "APIGatewayTaskDefinition")
                .memoryLimitMiB(512)
                .cpu(256)
                .build();

        ContainerDefinitionOptions containerOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry("api-gateway"))
                .environment(Map.of(
                    "SPRING_PROFILES_ACTIVE","prod",
                    "AUTH_SERVICE_URL","http://host.docker.internal:4005"
                ))
                .portMappings(List.of(4004).stream().map(port -> PortMapping.builder()
                        .containerPort(port)
                        .hostPort(port)
                        .protocol(Protocol.TCP)
                        .build()).toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder.create(this, "ApiGatewayLogGroup")
                                .logGroupName("/ecs/api-gateway")
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_DAY)
                                .build())
                        .streamPrefix("api-gateway")
                        .build()))
                .build();

        taskDefinition.addContainer("api-gateway-container", containerOptions);

        ApplicationLoadBalancedFargateService apiGateway
                = ApplicationLoadBalancedFargateService.Builder.create(this, "APIGatewayService")
                .cluster(ecsCluster)
                .serviceName("api-gateway")
                .taskDefinition(taskDefinition)
                .desiredCount(1)
                .healthCheckGracePeriod(Duration.seconds(60))
                .build();

    }

    public static void main(String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();

        new LocalStack(app, "localstack", props);
        app.synth();
        System.out.println("cdk synth done");
    }

}