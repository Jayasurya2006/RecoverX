package com.recoverx.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String externalId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    /** Null for originally-successful payments. */
    private String failureReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecoveryStatus recoveryStatus;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false)
    private int batchNo;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
