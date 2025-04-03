package nz.com.patient_service.service;

import nz.com.patient_service.dto.PatientRequestDTO;
import nz.com.patient_service.dto.PatientResponseDTO;
import nz.com.patient_service.exception.EmailAlreadyExistsException;
import nz.com.patient_service.exception.PatientNotFoundException;
import nz.com.patient_service.mapper.PatientMapper;
import nz.com.patient_service.model.Patient;
import nz.com.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists: " + patientRequestDTO.getEmail());
        }
        return PatientMapper.toDTO(patientRepository.save(PatientMapper.toModel(patientRequestDTO)));
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(() ->
                new PatientNotFoundException("Patient not found with id: " + id)
        );
        if(patientRepository.existsByEmailAndIdNot(patient.getEmail(),id))
        {
            throw new EmailAlreadyExistsException("A patient with this email already exists: " + patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse( patientRequestDTO.getDateOfBirth() ));

        return PatientMapper.toDTO(patientRepository.save(patient));
    }

    public void deletePatient(UUID id) {
//        Patient patient = patientRepository.findById(id).orElseThrow(() ->
//                new PatientNotFoundException("Patient not found with id: " + id)
//        );
//        this.patientRepository.delete(patient);
        this.patientRepository.deleteById(id);
    }


}
