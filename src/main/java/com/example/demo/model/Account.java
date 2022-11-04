package com.example.demo.model;

import com.example.demo.common.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    private UUID id;
    @Column(name = "user_id")
    private Long userId;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private BigDecimal balance;
}
