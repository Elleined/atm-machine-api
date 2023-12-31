package com.elleined.atmmachineapi.model.transaction;

import com.elleined.atmmachineapi.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_peer_to_peer_transaction")
@NoArgsConstructor
@Getter
@Setter
public class PeerToPeerTransaction extends Transaction {

    @ManyToOne(optional = false)
    @JoinColumn(
            updatable = false,
            nullable = false,
            name = "sender_id",
            referencedColumnName = "user_id"
    )
    private User sender;

    @ManyToOne(optional = false)
    @JoinColumn(
            updatable = false,
            nullable = false,
            name = "receiver_id",
            referencedColumnName = "user_id"
    )
    private User receiver;

    @Builder
    public PeerToPeerTransaction(int id, String trn, BigDecimal amount, LocalDateTime transactionDate, User sender, User receiver) {
        super(id, trn, amount, transactionDate);
        this.sender = sender;
        this.receiver = receiver;
    }
}
