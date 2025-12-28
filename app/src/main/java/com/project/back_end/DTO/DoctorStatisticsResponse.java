package com.project.back_end.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStatisticsResponse {
    private long totalAppointments;
    private long completedAppointments;
    private long upcomingAppointments;

    public static DoctorStatisticsResponse of(long total, long completed, long upcoming) {
        return new DoctorStatisticsResponse(total, completed, upcoming);
    }
}
