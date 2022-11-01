package com.example.demo.dto;

import com.example.demo.common.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDTO {
    private UUID id;
    private Long userId;
    private Currency currency;
    private Double balance;
}
