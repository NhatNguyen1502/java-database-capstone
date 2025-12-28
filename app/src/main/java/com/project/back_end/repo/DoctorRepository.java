package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    Doctor findByEmail(String email);
    
    Doctor findByUsername(String username);
    
    Doctor findByEmailOrUsername(String email, String username);
    
    List<Doctor> findByUsernameContainingIgnoreCase(String username);
    
    List<Doctor> findByUsernameContainingIgnoreCaseAndSpecialization(String username, String specialization);
    
    List<Doctor> findBySpecialization(String specialization);
    
    List<Doctor> findByIsActiveTrue();
    
    List<Doctor> findByIsActiveFalse();
}
