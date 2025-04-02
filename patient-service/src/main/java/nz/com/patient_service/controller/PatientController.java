package nz.com.patient_service.controller;

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
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    ResponseEntity<List<PatientResponseDTO>> getPatients() {
        return ResponseEntity.ok().body(patientService.listPatients()) ;
    }

    @PostMapping
    ResponseEntity<PatientResponseDTO> savePatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequest) {
        return ResponseEntity.ok().body(this.patientService.createPatient(patientRequest));
    }

    @PutMapping("/{id}")
    ResponseEntity<PatientResponseDTO> updatePatient(
            @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO,
            @PathVariable UUID id) {
        return ResponseEntity.ok().body(this.patientService.updatePatient(id,patientRequestDTO));
    }


}
