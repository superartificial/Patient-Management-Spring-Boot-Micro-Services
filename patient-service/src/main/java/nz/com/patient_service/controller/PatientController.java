package nz.com.patient_service.controller;

import jakarta.validation.Valid;
import nz.com.patient_service.dto.PatientRequestDTO;
import nz.com.patient_service.dto.PatientResponseDTO;
import nz.com.patient_service.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    ResponseEntity<PatientResponseDTO> savePatient(@Valid @RequestBody PatientRequestDTO patientRequest) {
        return ResponseEntity.ok().body(this.patientService.createPatient(patientRequest));
    }

}
