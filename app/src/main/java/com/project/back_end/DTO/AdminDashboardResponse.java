package com.project.back_end.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long todayAppointments;

    public static AdminDashboardResponse of(long patients, long doctors, long appointments, long today) {
        return new AdminDashboardResponse(patients, doctors, appointments, today);
    }
}
