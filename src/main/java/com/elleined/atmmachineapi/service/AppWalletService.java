package com.elleined.atmmachineapi.service;

import com.elleined.atmmachineapi.model.AppWallet;
import com.elleined.atmmachineapi.repository.AppWalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AppWalletService {
    private final AppWalletRepository appWalletRepository;

    public <T> void addAndSaveBalance(T t) {
        AppWallet appWallet = appWalletRepository.findById(1).orElseThrow();
        BigDecimal oldAppWalletBalance = appWallet.getBalance();
        BigDecimal newAppWalletBalance = oldAppWalletBalance.add(new BigDecimal(String.valueOf(t)));
        appWallet.setBalance(newAppWalletBalance);

        appWalletRepository.save(appWallet);
        log.debug("App wallet has new balance of {} from {}", appWallet.getBalance(), oldAppWalletBalance);
    }
}
