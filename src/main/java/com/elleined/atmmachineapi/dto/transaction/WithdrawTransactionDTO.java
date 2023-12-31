package com.elleined.atmmachineapi.dto.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class WithdrawTransactionDTO extends TransactionDTO {
    private int userId;

    @Builder(builderMethodName = "withdrawTransactionDTOBuilder")

    public WithdrawTransactionDTO(int id, String trn, BigDecimal amount, LocalDateTime transactionDate, int userId) {
        super(id, trn, amount, transactionDate);
        this.userId = userId;
    }
}
