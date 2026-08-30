package com.recoverx.repository;

import com.recoverx.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByTransactionExternalIdOrderByTimestampAsc(String externalId);
}
