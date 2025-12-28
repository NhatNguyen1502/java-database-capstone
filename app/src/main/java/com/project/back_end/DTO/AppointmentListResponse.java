package com.project.back_end.DTO;

import com.project.back_end.models.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentListResponse {
    private List<Appointment> appointments;
    private long count;

    public static AppointmentListResponse of(List<Appointment> appointments) {
        return new AppointmentListResponse(appointments, appointments.size());
    }
}
