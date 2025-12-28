package com.project.back_end.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUserType(AuditLog.UserType userType);
    
    List<AuditLog> findByAction(String action);
    
    List<AuditLog> findByUserTypeAndAction(AuditLog.UserType userType, String action);
}
