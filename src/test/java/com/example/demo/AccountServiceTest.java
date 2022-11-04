package com.example.demo;

import com.example.demo.common.Currency;
import com.example.demo.dto.AccountDTO;
import com.example.demo.exception.AccountAlreadyExistException;
import com.example.demo.exception.AccountNoExistException;
import com.example.demo.exception.InsufficientFundsException;
import com.example.demo.model.Account;
import com.example.demo.repositories.AccountJpaRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExchangeRateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private static final UUID UUID_1 = UUID.fromString("4181661a-bfc7-485c-8582-8ec74eb17ee8");
    private static final UUID UUID_2 = UUID.fromString("ceb396e2-a5e5-4244-ad61-3699a9035570");
    @Mock
    private AccountJpaRepository repository;
    @Mock
    private ExchangeRateService exchangeRateService;
    private AccountService accountService;

    @BeforeEach
    public void initialize() {
        accountService = new AccountService(repository, exchangeRateService);
    }

    @Test
    public void shouldThrowInsufficientFundsException() {
        when(repository.findById(UUID_1)).thenReturn(Optional.of(new Account(UUID_1, 1L, Currency.PLN, BigDecimal.valueOf(10000.00))));
        when(repository.findById(UUID_2)).thenReturn(Optional.of(new Account(UUID_2, 2L, Currency.EUR, BigDecimal.valueOf(10000.00))));

        Assertions.assertThrows(InsufficientFundsException.class,
                () -> accountService.transferMoney(UUID_1, UUID_2, BigDecimal.valueOf(20000.00)));
    }

    @Test
    public void shouldThrowAccountAlreadyExistException() {
        when(repository.findById(UUID_1)).thenReturn(Optional.of(new Account(UUID_1, 1L, Currency.PLN, BigDecimal.valueOf(10000.00))));

        AccountDTO accountDTO = new AccountDTO(UUID_1, 1L, Currency.PLN, 20000.00);
        Assertions.assertThrows(AccountAlreadyExistException.class,
                () -> accountService.addAccount(accountDTO));
    }

    @Test
    public void shouldInvokeMethodUpdateBalanceTwiceDuringTransferMoneyProperly() throws IOException, AccountNoExistException, InsufficientFundsException {
        when(repository.findById(UUID_1)).thenReturn(Optional.of(new Account(UUID_1, 1L, Currency.PLN, BigDecimal.valueOf(10000.00))));
        when(repository.findById(UUID_2)).thenReturn(Optional.of(new Account(UUID_2, 2L, Currency.EUR, BigDecimal.valueOf(10000.00))));

        Mockito.when(exchangeRateService.getCurrenciesRate(Currency.PLN.name(), Currency.EUR.name()))
                .thenReturn(BigDecimal.valueOf(0.20));

        accountService.transferMoney(UUID_1, UUID_2, BigDecimal.valueOf(5000.00));

        verify(repository, times(2)).updateBalance(any(), any());
    }

    @Test
    public void shouldInvokeMethodUpdateBalanceWithProperValues() throws IOException, AccountNoExistException, InsufficientFundsException {
        when(repository.findById(UUID_1)).thenReturn(Optional.of(new Account(UUID_1, 1L, Currency.PLN, BigDecimal.valueOf(10000.00))));
        when(repository.findById(UUID_2)).thenReturn(Optional.of(new Account(UUID_2, 2L, Currency.EUR, BigDecimal.valueOf(10000.00))));

        Mockito.when(exchangeRateService.getCurrenciesRate(Currency.PLN.name(), Currency.EUR.name()))
                .thenReturn(BigDecimal.valueOf(0.20));

        accountService.transferMoney(UUID_1, UUID_2, BigDecimal.valueOf(5000.00));

        verify(repository, times(1)).updateBalance(UUID_1, new BigDecimal("5000.0"));
        verify(repository, times(1)).updateBalance(UUID_2, new BigDecimal("11000.00"));
    }

}
