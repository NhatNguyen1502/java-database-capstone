package com.project.back_end.DTO;

import com.project.back_end.models.DoctorSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    private List<DoctorSchedule> schedules;

    public static ScheduleResponse of(List<DoctorSchedule> schedules) {
        return new ScheduleResponse(schedules);
    }
}
