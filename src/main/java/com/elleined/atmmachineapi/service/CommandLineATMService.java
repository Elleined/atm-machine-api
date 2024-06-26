package com.elleined.atmmachineapi.service;

import com.elleined.atmmachineapi.exception.InsufficientFundException;
import com.elleined.atmmachineapi.exception.limit.LimitException;
import com.elleined.atmmachineapi.exception.resource.ResourceNotFoundException;
import com.elleined.atmmachineapi.model.User;
import com.elleined.atmmachineapi.model.transaction.DepositTransaction;
import com.elleined.atmmachineapi.model.transaction.PeerToPeerTransaction;
import com.elleined.atmmachineapi.model.transaction.WithdrawTransaction;
import com.elleined.atmmachineapi.service.machine.deposit.DepositService;
import com.elleined.atmmachineapi.service.machine.p2p.PeerToPeerService;
import com.elleined.atmmachineapi.service.machine.withdraw.WithdrawService;
import com.elleined.atmmachineapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Scanner;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("commandLineAtm")
public class CommandLineATMService implements ATMService, Runnable {

    private final DepositService depositService;
    private final WithdrawService withdrawService;
    private final PeerToPeerService peerToPeerService;
    private final UserService userService;

    @Override
    public DepositTransaction deposit(User currentUser, BigDecimal depositedAmount) {
        try {
            depositService.deposit(currentUser, depositedAmount);
            System.out.println("You successfully deposited amounting " + depositedAmount);
            System.out.println("=== Your new Account balance is " + currentUser.getBalance() + " ===");
        } catch (ResourceNotFoundException | LimitException | IllegalArgumentException e) {
            log.error("Error Occurred! {}", e.getMessage());
        }
        return null;
    }

    @Override
    public WithdrawTransaction withdraw(User currentUser, BigDecimal withdrawnAmount) {
        try {
            withdrawService.withdraw(currentUser, withdrawnAmount);
            System.out.println("You successfully withdrawn amounting " + withdrawnAmount);
            System.out.println("=== Your new Account balance is " + currentUser.getBalance() + " ===");
        } catch (IllegalArgumentException | ResourceNotFoundException | LimitException | InsufficientFundException e) {
            log.error("Error Occurred! {}", e.getMessage());
        }
        return null;
    }

    @Override
    public PeerToPeerTransaction peerToPeer(User sender, User receiver, BigDecimal sentAmount) {
        try {
            peerToPeerService.peerToPeer(sender, receiver, sentAmount);
            System.out.println("You successfully send money to the recipient with id of " + receiver.getId() + " amounting " + sentAmount);
            System.out.println("=== Your new Account balance is " + sender.getBalance() + " ===");
        } catch (IllegalArgumentException | ResourceNotFoundException | LimitException | InsufficientFundException e) {
            log.error("Error Occurred! {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void run() throws ResourceNotFoundException {
        final Scanner in = new Scanner(System.in);
        System.out.print("Enter your user id: ");
        int userId = in.nextInt();
        if (!userService.isExists(userId)) {
            log.error("User with id of {} does not exists!", userId);
            return;
        }

        String name = userService.getById(userId).getName();
        System.out.println("=== Welcome " + name + " ====");

        final Scanner yerOrNo = new Scanner(System.in);
        char response = 'Y';
        do {
            User user = userService.getById(userId);

            System.out.print("""
				1. Withdraw
				2. Deposit
				3. Send Money
				Choose your action:\s""");
            int action = in.nextInt();
            switch (action) {
                case 1 -> {
                    System.out.println("=== Account Balance: " + user.getBalance() + " ===");
                    System.out.print("Enter amount you want to withdraw: ");
                    User currentUser = userService.getById(userId);
                    this.withdraw(currentUser, in.nextBigDecimal());

                    System.out.print("Do you want to make another transaction? (Y/N): ");
                    response = yerOrNo.nextLine().charAt(0);
                    if (response == 'N' || response == 'n') response = 'N';
                }
                case 2 -> {
                    System.out.println("=== Account Balance: " + user.getBalance() + " ===");
                    System.out.print("Enter amount you want to deposit: ");
                    User currentUser = userService.getById(userId);
                    this.deposit(currentUser, in.nextBigDecimal());

                    System.out.print("Do you want to make another transaction? (Y/N): ");
                    response = yerOrNo.nextLine().charAt(0);
                    if (response == 'N' || response == 'n') response = 'N';
                }
                case 3 -> {
                    System.out.println("=== Account Balance: " + user.getBalance() + " ===");
                    System.out.print("Enter the recipient's id: " );
                    int receiverId = in.nextInt();
                    if (!userService.isExists(receiverId)) {
                        log.error("User with id of {} does not exists!", receiverId);
                        return;
                    }

                    System.out.print("Enter amount to be sent: ");
                    BigDecimal sentAmount = in.nextBigDecimal();
                    log.trace("Sender id: {}. Recipient id: {}. Amount to be sent: {}", userId, sentAmount, receiverId);
                    User sender = userService.getById(userId);
                    User receiver = userService.getById(receiverId);
                    this.peerToPeer(sender, receiver, sentAmount);

                    System.out.print("Do you want to make another transaction? (Y/N): ");
                    response = yerOrNo.nextLine().charAt(0);
                    if (response == 'N' || response == 'n') response = 'N';
                }
                default -> log.error("Please choose only within the specified choices!");
            }
        } while (response != 'N');

        System.out.println("=== Thank you for using my atm application :) ===");
    }
}
