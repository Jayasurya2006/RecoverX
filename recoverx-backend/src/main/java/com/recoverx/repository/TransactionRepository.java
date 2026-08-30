package com.recoverx.repository;

import com.recoverx.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByExternalId(String externalId);

    List<Transaction> findAllByOrderByIdAsc();
}
