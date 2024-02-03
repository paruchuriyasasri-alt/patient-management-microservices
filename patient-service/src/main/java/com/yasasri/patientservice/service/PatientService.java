package com.yasasri.patientservice.service;

import com.yasasri.patientservice.dto.PatientDTO;
import com.yasasri.patientservice.exception.PatientNotFoundException;
import com.yasasri.patientservice.exception.DuplicateEmailException;
import com.yasasri.patientservice.model.Patient;
import com.yasasri.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private static final String TOPIC_PATIENT_EVENTS = "patient-events";

    private final PatientRepository patientRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PatientService(PatientRepository patientRepository,
                          KafkaTemplate<String, String> kafkaTemplate) {
        this.patientRepository = patientRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        return toDTO(patient);
    }

    @Transactional
    public PatientDTO createPatient(PatientDTO dto) {
        if (dto.getEmail() != null && patientRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Patient with email " + dto.getEmail() + " already exists");
        }

        Patient patient = toEntity(dto);
        Patient saved = patientRepository.save(patient);

        log.info("Patient created with id: {}", saved.getId());
        kafkaTemplate.send(TOPIC_PATIENT_EVENTS, "PATIENT_CREATED:" + saved.getId());

        return toDTO(saved);
    }

    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setZipCode(dto.getZipCode());
        existing.setInsuranceProvider(dto.getInsuranceProvider());
        existing.setInsurancePolicyNumber(dto.getInsurancePolicyNumber());
        existing.setBloodGroup(dto.getBloodGroup());
        existing.setAllergies(dto.getAllergies());

        if (dto.getGender() != null) {
            existing.setGender(Patient.Gender.valueOf(dto.getGender()));
        }

        Patient updated = patientRepository.save(existing);
        log.info("Patient updated with id: {}", updated.getId());
        kafkaTemplate.send(TOPIC_PATIENT_EVENTS, "PATIENT_UPDATED:" + updated.getId());

        return toDTO(updated);
    }

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
        log.info("Patient deleted with id: {}", id);
        kafkaTemplate.send(TOPIC_PATIENT_EVENTS, "PATIENT_DELETED:" + id);
    }

    public List<PatientDTO> searchByLastName(String lastName) {
        return patientRepository.findByLastNameContainingIgnoreCase(lastName)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private PatientDTO toDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setEmail(patient.getEmail());
        dto.setPhone(patient.getPhone());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender() != null ? patient.getGender().name() : null);
        dto.setAddress(patient.getAddress());
        dto.setCity(patient.getCity());
        dto.setState(patient.getState());
        dto.setZipCode(patient.getZipCode());
        dto.setInsuranceProvider(patient.getInsuranceProvider());
        dto.setInsurancePolicyNumber(patient.getInsurancePolicyNumber());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setAllergies(patient.getAllergies());
        return dto;
    }

    private Patient toEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setAddress(dto.getAddress());
        patient.setCity(dto.getCity());
        patient.setState(dto.getState());
        patient.setZipCode(dto.getZipCode());
        patient.setInsuranceProvider(dto.getInsuranceProvider());
        patient.setInsurancePolicyNumber(dto.getInsurancePolicyNumber());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setAllergies(dto.getAllergies());

        if (dto.getGender() != null) {
            patient.setGender(Patient.Gender.valueOf(dto.getGender()));
        }
        return patient;
    }
}
