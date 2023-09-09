package com.elleined.atmmachineapi.service.atm;

import com.elleined.atmmachineapi.exception.NotValidAmountException;
import com.elleined.atmmachineapi.model.User;
import com.elleined.atmmachineapi.model.transaction.DepositTransaction;
import com.elleined.atmmachineapi.repository.UserRepository;
import com.elleined.atmmachineapi.service.AppWalletService;
import com.elleined.atmmachineapi.service.atm.transaction.TransactionService;
import com.elleined.atmmachineapi.service.fee.FeeService;
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
    private final UserRepository userRepository;

    private final ATMValidator atmValidator;

    private final TransactionService transactionService;

    private final FeeService feeService;

    private final AppWalletService appWalletService;


    public DepositTransaction deposit(User currentUser, @NonNull BigDecimal depositedAmount)
            throws NotValidAmountException {

        if (atmValidator.isValidAmount(depositedAmount)) throw new NotValidAmountException("Amount should be positive and cannot be zero!");

        BigDecimal oldBalance = currentUser.getBalance();
        float depositFee = feeService.getDepositFee(depositedAmount);
        BigDecimal finalDepositedAmount = feeService.deductDepositFee(depositedAmount, depositFee);
        currentUser.setBalance(finalDepositedAmount);
        userRepository.save(currentUser);
        appWalletService.addAndSaveBalance(depositFee);

        DepositTransaction depositTransaction = saveDepositTransaction(currentUser, finalDepositedAmount);

        log.debug("User with id of {} deposited amounting {} from {} because of deposit fee of {} which is the {}% of the deposited amount and now has new balance of {} from {}", currentUser.getId(), finalDepositedAmount, depositedAmount, depositFee, FeeService.DEPOSIT_FEE_PERCENTAGE, currentUser.getBalance(), oldBalance);
        return depositTransaction;
    }

    private DepositTransaction saveDepositTransaction(User user, @NonNull BigDecimal depositedAmount) {
        String trn = UUID.randomUUID().toString();

        DepositTransaction depositTransaction = DepositTransaction.builder()
                .trn(trn)
                .amount(depositedAmount)
                .transactionDate(LocalDateTime.now())
                .user(user)
                .build();

        transactionService.save(depositTransaction);
        log.debug("Deposit transaction saved with trn of {}", trn);
        return depositTransaction;
    }
}
