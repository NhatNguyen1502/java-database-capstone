package com.project.back_end.DTO;

import com.project.back_end.models.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogsResponse {
    private List<AuditLog> logs;

    public static AuditLogsResponse of(List<AuditLog> logs) {
        return new AuditLogsResponse(logs);
    }
}
