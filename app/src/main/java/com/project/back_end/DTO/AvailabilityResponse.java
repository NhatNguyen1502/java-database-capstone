package com.project.back_end.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponse {
    private boolean available;

    public static AvailabilityResponse of(boolean available) {
        return new AvailabilityResponse(available);
    }
}
