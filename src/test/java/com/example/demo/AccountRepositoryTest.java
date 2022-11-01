package com.example.demo;

import com.example.demo.common.Currency;
import com.example.demo.exception.AccountNoExistException;
import com.example.demo.model.Account;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.InMemoryAccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountRepositoryTest {

    private static final UUID UUID_1 = UUID.fromString("4181661a-bfc7-485c-8582-8ec74eb17ee8");
    private static final UUID UUID_2 = UUID.fromString("ceb396e2-a5e5-4244-ad61-3699a9035570");
    private AccountRepository repository;

    @BeforeEach
    public void initialize() {
        repository = new InMemoryAccountRepository();
    }

    @Test
    public void addAccounts() {
        Account account = new Account(UUID.randomUUID(), 1L, Currency.EUR, new BigDecimal("10000"));
        repository.save(account);

        Assertions.assertEquals(repository.findAll().size(), 1);

        Account account2 = new Account(UUID.randomUUID(), 2L, Currency.PLN, new BigDecimal("12000"));
        repository.save(account2);

        Assertions.assertEquals(repository.findAll().size(), 2);
    }

    @Test
    public void findAccountById() throws AccountNoExistException {
        Account account = new Account(UUID_1, 1L, Currency.EUR, new BigDecimal("10000"));
        Account account2 = new Account(UUID_2, 2L, Currency.PLN, new BigDecimal("12000"));
        repository.save(account);
        repository.save(account2);

       Account accountFromDb = repository.findById(UUID_1);

        Assertions.assertEquals(accountFromDb.getUserId(), 1L);
        Assertions.assertEquals(accountFromDb.getCurrency(), Currency.EUR);

    }

    @Test
    public void shouldThrowNoExistException() {
        Account account = new Account(UUID_1, 1L, Currency.EUR, new BigDecimal("10000"));
        repository.save(account);

        UUID noExistAccountId = UUID.fromString("3a46d5c6-d1a7-4090-9270-1a6d8acc793a");
        Account noExistedAccount = new Account(noExistAccountId, 1L, Currency.PLN, new BigDecimal("40000"));

        Assertions.assertThrows(AccountNoExistException.class, () -> repository.update(noExistedAccount));
    }

}
