package com.project.back_end.DTO;

import java.util.List;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersResponse {
    private List<Patient> patients;
    private List<Doctor> doctors;
    private List<Admin> admins;
}
