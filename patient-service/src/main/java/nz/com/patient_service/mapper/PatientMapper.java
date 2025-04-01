package nz.com.patient_service.mapper;

import nz.com.patient_service.dto.PatientResponseDTO;
import nz.com.patient_service.model.Patient;

public class PatientMapper {

    public PatientResponseDTO toDTO(Patient patient) {
        PatientResponseDTO patientDTO = new PatientResponseDTO();
        patientDTO.setId(patient.getId().toString());
        patientDTO.setName(patient.getName());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setDateOfBirth(patient.getDateOfBirth().toString());
        return patientDTO;
    }

}
