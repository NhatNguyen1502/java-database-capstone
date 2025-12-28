package com.project.back_end.DTO;

import com.project.back_end.models.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorListResponse {
    private List<Doctor> doctors;
    private long count;

    public static DoctorListResponse of(List<Doctor> doctors) {
        return new DoctorListResponse(doctors, doctors.size());
    }
}
