package com.project.back_end.DTO;

import com.project.back_end.models.Prescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionListResponse {
    private List<Prescription> prescriptions;
    private long count;

    public static PrescriptionListResponse of(List<Prescription> prescriptions) {
        return new PrescriptionListResponse(prescriptions, prescriptions.size());
    }
}
