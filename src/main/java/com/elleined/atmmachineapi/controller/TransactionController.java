package com.elleined.atmmachineapi.controller;

import com.elleined.atmmachineapi.dto.transaction.DepositTransactionDTO;
import com.elleined.atmmachineapi.dto.transaction.PeerToPeerTransactionDTO;
import com.elleined.atmmachineapi.dto.transaction.WithdrawTransactionDTO;
import com.elleined.atmmachineapi.mapper.TransactionMapper;
import com.elleined.atmmachineapi.model.User;
import com.elleined.atmmachineapi.service.atm.deposit.DepositTransactionService;
import com.elleined.atmmachineapi.service.atm.p2p.P2PTransactionService;
import com.elleined.atmmachineapi.service.atm.withdraw.WithdrawTransactionService;
import com.elleined.atmmachineapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/{currentUserId}/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final UserService userService;

    private final WithdrawTransactionService withdrawTransactionService;
    private final DepositTransactionService depositTransactionService;
    private final P2PTransactionService p2PTransactionService;

    private final TransactionMapper transactionMapper;

    @GetMapping("/withdraw")
    public List<WithdrawTransactionDTO> getAllWithdrawalTransactions(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        return withdrawTransactionService.getAll(currentUser).stream()
                .map(transactionMapper::toWithdrawTransactionDTO)
                .toList();
    }

    @GetMapping("/deposit")
    public List<DepositTransactionDTO> getAllDepositTransactions(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        return depositTransactionService.getAll(currentUser).stream()
                .map(transactionMapper::toDepositTransactionDTO)
                .toList();
    }

    @GetMapping("/receive-money")
    public List<PeerToPeerTransactionDTO> getAllReceiveMoneyTransactions(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        return p2PTransactionService.getAllReceiveMoneyTransactions(currentUser).stream()
                .map(transactionMapper::toPeer2PeerTransactionDTO)
                .toList();
    }

    @GetMapping("/sent-money")
    public List<PeerToPeerTransactionDTO> getAllSentMoneyTransactions(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        return p2PTransactionService.getAllSentMoneyTransactions(currentUser).stream()
                .map(transactionMapper::toPeer2PeerTransactionDTO)
                .toList();
    }
}
