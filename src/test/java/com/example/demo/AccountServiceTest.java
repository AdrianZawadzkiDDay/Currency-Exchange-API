package com.example.demo;

import com.example.demo.common.Currency;
import com.example.demo.dto.AccountDTO;
import com.example.demo.exception.AccountAlreadyExistException;
import com.example.demo.exception.AccountNoExistException;
import com.example.demo.exception.InsufficientFundsException;
import com.example.demo.model.Account;
import com.example.demo.repositories.InMemoryAccountRepository;
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
import java.util.UUID;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private static final UUID UUID_1 = UUID.fromString("4181661a-bfc7-485c-8582-8ec74eb17ee8");
    private static final UUID UUID_2 = UUID.fromString("ceb396e2-a5e5-4244-ad61-3699a9035570");
    private InMemoryAccountRepository repository;
    private AccountService accountService;
    @Mock
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void initialize() {
        repository = new InMemoryAccountRepository();
        accountService = new AccountService(repository, exchangeRateService);

        Account account = new Account(UUID_1, 1L, Currency.PLN, BigDecimal.valueOf(10000.00));
        repository.save(account);
        Account account2 = new Account(UUID_2, 2L, Currency.EUR, BigDecimal.valueOf(10000.00));
        repository.save(account2);
    }

    @Test
    public void shouldThrowInsufficientFundsException() {
        Assertions.assertThrows(InsufficientFundsException.class,
                () -> accountService.transferMoney(UUID_1, UUID_2, BigDecimal.valueOf(20000.00)));
    }

    @Test
    public void shouldThrowAccountAlreadyExistException() {
        AccountDTO accountDTO = new AccountDTO(UUID_1, 1L, Currency.PLN, 20000.00);
        Assertions.assertThrows(AccountAlreadyExistException.class,
                () -> accountService.addAccount(accountDTO));
    }

    @Test
    public void shouldTransferMoneyProperly() throws IOException, AccountNoExistException, InsufficientFundsException {
        Mockito.when(exchangeRateService.getCurrenciesRate(Currency.PLN.name(), Currency.EUR.name()))
                .thenReturn(BigDecimal.valueOf(0.20));

        accountService.transferMoney(UUID_1, UUID_2, BigDecimal.valueOf(5000.00));

        Account updatedAccountFrom = repository.findById(UUID_1);
        Assertions.assertEquals(updatedAccountFrom.getBalance(), BigDecimal.valueOf(5000.00));

        Account updatedAccountTo = repository.findById(UUID_2);
        Assertions.assertEquals(updatedAccountTo.getBalance(), new BigDecimal("11000.00"));
    }

}
