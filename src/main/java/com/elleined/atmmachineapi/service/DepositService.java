package com.elleined.atmmachineapi.service;

import com.elleined.atmmachineapi.exception.ResourceNotFoundException;
import com.elleined.atmmachineapi.model.User;
import com.elleined.atmmachineapi.model.transaction.ATMTransaction;
import com.elleined.atmmachineapi.model.transaction.DepositATMTransaction;
import com.elleined.atmmachineapi.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepositService {
    private final UserService userService;
    private final ATMValidator atmValidator;
    private final TransactionService transactionService;

    public BigDecimal deposit(int currentUserId, @NonNull BigDecimal depositAmount)
            throws ResourceNotFoundException, IllegalArgumentException {

        if (atmValidator.isValidAmount(depositAmount)) {
            log.trace("Amount trying to deposit is {} which is less than 0 or a negative number", depositAmount);
            throw new IllegalArgumentException("Amount should be positive and cannot be zero!");
        }

        User currentUser = userService.getById(currentUserId);
        BigDecimal oldBalance = currentUser.getBalance();
        BigDecimal newBalance = currentUser.getBalance().add(depositAmount);
        currentUser.setBalance(newBalance);

        userService.save(currentUser);
        saveDepositTransaction(currentUser, depositAmount);

        log.debug("User with id of {} deposited amounting {}.\nOld balance: {}\nNew Balance: {}", currentUserId, depositAmount, oldBalance, newBalance);
        return currentUser.getBalance();
    }

    private void saveDepositTransaction(User user, @NonNull BigDecimal depositedAmount) {
        String trn = UUID.randomUUID().toString();

        ATMTransaction depositTransaction = DepositATMTransaction.builder()
                .trn(trn)
                .amount(depositedAmount)
                .transactionDate(LocalDateTime.now())
                .user(user)
                .accountBalance(user.getBalance())
                .build();

        transactionService.save(depositTransaction);
        log.debug("Deposit transaction saved with trn of {}", trn);
    }
}
