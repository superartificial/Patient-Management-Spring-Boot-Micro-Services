package nz.com.patient_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import nz.com.patient_service.dto.PatientRequestDTO;
import nz.com.patient_service.dto.PatientResponseDTO;
import nz.com.patient_service.dto.validators.CreatePatientValidationGroup;
import nz.com.patient_service.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "API for managing Patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get Patients")
    ResponseEntity<List<PatientResponseDTO>> getPatients() {
        return ResponseEntity.ok().body(patientService.listPatients()) ;
    }

    @PostMapping
    @Operation(summary = "Create new Patient")
    ResponseEntity<PatientResponseDTO> savePatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequest) {
        return ResponseEntity.ok().body(this.patientService.createPatient(patientRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Patient")
    ResponseEntity<PatientResponseDTO> updatePatient(
            @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO,
            @PathVariable UUID id) {
        return ResponseEntity.ok().body(this.patientService.updatePatient(id,patientRequestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Patient")
    ResponseEntity<Boolean> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

}
