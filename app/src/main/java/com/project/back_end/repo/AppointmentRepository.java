package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
    
    List<Appointment> findByPatientIdAndStatus(Long patientId, Appointment.AppointmentStatus status);
    
    List<Appointment> findByPatientIdAndAppointmentDateBetween(Long patientId, LocalDate dateFrom, LocalDate dateTo);
    
    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId);
    
    List<Appointment> findByDoctorIdAndPatientId(Long doctorId, Long patientId);
    
    List<Appointment> findByDoctorIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(Long doctorId, LocalDate date);
    
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime);
    
    long countByDoctorId(Long doctorId);
    
    long countByDoctorIdAndStatus(Long doctorId, Appointment.AppointmentStatus status);
    
    long countByAppointmentDate(LocalDate date);
    
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);
}
