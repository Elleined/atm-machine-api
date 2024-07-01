package com.elleined.atmmachineapi.repository.transaction;

import com.elleined.atmmachineapi.model.User;
import com.elleined.atmmachineapi.model.transaction.DepositTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepositTransactionRepository extends JpaRepository<DepositTransaction, Integer> {

    @Query("SELECT dt FROM DepositTransaction dt WHERE dt.trn = :trn")
    DepositTransaction fetchByTRN(@Param("trn") String trn);

    @Query("SELECT dt FROM DepositTransaction dt WHERE dt.user = :currentUser")
    Page<DepositTransaction> findAll(@Param("currentUser") User currentUser, Pageable pageable);
}