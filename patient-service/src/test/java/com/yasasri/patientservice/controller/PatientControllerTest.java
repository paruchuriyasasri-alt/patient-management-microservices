package com.yasasri.patientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yasasri.patientservice.dto.PatientDTO;
import com.yasasri.patientservice.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllPatients_shouldReturn200() throws Exception {
        PatientDTO dto = new PatientDTO();
        dto.setId(1L);
        dto.setFirstName("John");
        dto.setLastName("Doe");

        when(patientService.getAllPatients()).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void createPatient_shouldReturn201() throws Exception {
        PatientDTO dto = new PatientDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane@example.com");

        PatientDTO saved = new PatientDTO();
        saved.setId(1L);
        saved.setFirstName("Jane");
        saved.setLastName("Smith");

        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }
}
