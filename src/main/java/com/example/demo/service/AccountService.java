package com.example.demo.service;

import com.example.demo.common.Currency;
import com.example.demo.dto.AccountDTO;
import com.example.demo.exception.AccountAlreadyExistException;
import com.example.demo.exception.AccountNoExistException;
import com.example.demo.exception.InsufficientFundsException;
import com.example.demo.model.Account;
import com.example.demo.repositories.AccountJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AccountService {

    private static Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountJpaRepository accountRepository;
    private final ExchangeRateService exchangeRateService;

    public AccountService(AccountJpaRepository accountRepository, ExchangeRateService exchangeRateService) {
        this.accountRepository = accountRepository;
        this.exchangeRateService = exchangeRateService;
    }

    public synchronized Account addAccount(AccountDTO accountDTO) throws AccountAlreadyExistException {
        Optional<Account> optionalAccount = accountRepository.findById(accountDTO.getId());
        if(optionalAccount.isPresent()) {
            throw new AccountAlreadyExistException();
        }

        Account account = mapDTOToAccount(accountDTO);
        return accountRepository.save(account);
    }

    private Account mapDTOToAccount(AccountDTO accountDTO) {
        return new Account(accountDTO.getId(), accountDTO.getUserId(), accountDTO.getCurrency(), BigDecimal.valueOf(accountDTO.getBalance()));
    }

    @Transactional
    public synchronized void transferMoney(UUID accountFromId, UUID accountToId, BigDecimal amount) throws IOException, InsufficientFundsException, AccountNoExistException {
        Optional<Account> accountFromOptional = accountRepository.findById(accountFromId);
        Optional<Account> accountToOptional = accountRepository.findById(accountToId);

        if(accountFromOptional.isEmpty() || accountToOptional.isEmpty()) {
            throw new AccountNoExistException();
        }

        Account accountFrom = accountFromOptional.get();
        Account accountTo= accountToOptional.get();

        BigDecimal accountFromBalance = accountFrom.getBalance();
        if(accountFromBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        BigDecimal accountToBalance = accountTo.getBalance();
        Currency currencyFrom = accountFrom.getCurrency();
        Currency currencyTo = accountTo.getCurrency();
        BigDecimal rate = exchangeRateService.getCurrenciesRate(currencyFrom.name(), currencyTo.name());

        BigDecimal newBalanceForFromAccount = accountFromBalance.subtract(amount);
        BigDecimal newBalanceForToAccount = accountToBalance.add(amount.multiply(rate));

        accountRepository.updateBalance(accountFrom.getId(), newBalanceForFromAccount);
        accountRepository.updateBalance(accountTo.getId(), newBalanceForToAccount);
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public List<Account> findByCurrency(Currency currency) {
        return accountRepository.findAllByCurrency(currency).orElse(new ArrayList<>());
    }


}
