package nz.com.patient_service.service;

import nz.com.patient_service.dto.PatientRequestDTO;
import nz.com.patient_service.dto.PatientResponseDTO;
import nz.com.patient_service.mapper.PatientMapper;
import nz.com.patient_service.model.Patient;
import nz.com.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> listPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(PatientMapper::toDTO)
                .toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        return PatientMapper.toDTO(patientRepository.save(PatientMapper.toModel(patientRequestDTO)));
    }

}
