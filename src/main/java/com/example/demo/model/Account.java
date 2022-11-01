package com.example.demo.model;

import com.example.demo.common.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private UUID id;
    private Long userId;
    private Currency currency;
    private BigDecimal balance;
}
