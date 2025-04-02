package nz.com.patient_service.mapper;

import nz.com.patient_service.dto.PatientRequestDTO;
import nz.com.patient_service.dto.PatientResponseDTO;
import nz.com.patient_service.model.Patient;

import java.time.LocalDate;

public class PatientMapper {

    public static PatientResponseDTO toDTO(Patient patient) {
        PatientResponseDTO patientDTO = new PatientResponseDTO();
        patientDTO.setId(patient.getId().toString());
        patientDTO.setName(patient.getName());
        patientDTO.setEmail(patient.getEmail());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setDateOfBirth(patient.getDateOfBirth().toString());
        return patientDTO;
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()) );
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()) );
        patient.setEmail(patientRequestDTO.getEmail());
        return patient;
    }

}
