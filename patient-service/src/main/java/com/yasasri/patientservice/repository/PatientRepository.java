package com.yasasri.patientservice.repository;

import com.yasasri.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);

    List<Patient> findByLastNameContainingIgnoreCase(String lastName);

    List<Patient> findByInsuranceProvider(String insuranceProvider);

    boolean existsByEmail(String email);
}
