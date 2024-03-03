package com.yasasri.patientservice.service;

import com.yasasri.patientservice.dto.PatientDTO;
import com.yasasri.patientservice.exception.PatientNotFoundException;
import com.yasasri.patientservice.model.Patient;
import com.yasasri.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private PatientService patientService;

    private Patient samplePatient;

    @BeforeEach
    void setUp() {
        samplePatient = new Patient();
        samplePatient.setId(1L);
        samplePatient.setFirstName("John");
        samplePatient.setLastName("Doe");
        samplePatient.setEmail("john.doe@example.com");
        samplePatient.setPhone("555-1234");
        samplePatient.setDateOfBirth(LocalDate.of(1990, 5, 15));
        samplePatient.setGender(Patient.Gender.MALE);
        samplePatient.setBloodGroup("O+");
    }

    @Test
    void getAllPatients_shouldReturnList() {
        when(patientRepository.findAll()).thenReturn(Arrays.asList(samplePatient));

        List<PatientDTO> result = patientService.getAllPatients();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getPatientById_shouldReturnPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient));

        PatientDTO result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void getPatientById_shouldThrowWhenNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(99L));
    }

    @Test
    void createPatient_shouldSaveAndPublishEvent() {
        PatientDTO dto = new PatientDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane@example.com");

        when(patientRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(samplePatient);

        PatientDTO result = patientService.createPatient(dto);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void deletePatient_shouldDeleteAndPublishEvent() {
        when(patientRepository.existsById(1L)).thenReturn(true);

        patientService.deletePatient(1L);

        verify(patientRepository).deleteById(1L);
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void deletePatient_shouldThrowWhenNotFound() {
        when(patientRepository.existsById(99L)).thenReturn(false);

        assertThrows(PatientNotFoundException.class, () -> patientService.deletePatient(99L));
    }

    @Test
    void searchByLastName_shouldReturnMatches() {
        when(patientRepository.findByLastNameContainingIgnoreCase("Doe"))
                .thenReturn(Arrays.asList(samplePatient));

        List<PatientDTO> result = patientService.searchByLastName("Doe");

        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).getLastName());
    }
}
