package com.example.demo.service;

import com.example.demo.common.Currency;
import com.example.demo.dto.AccountDTO;
import com.example.demo.exception.AccountAlreadyExistException;
import com.example.demo.exception.AccountNoExistException;
import com.example.demo.exception.InsufficientFundsException;
import com.example.demo.model.Account;
import com.example.demo.repositories.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AccountService {

    private static Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public AccountService(AccountRepository accountRepository, ExchangeRateService exchangeRateService) {
        this.accountRepository = accountRepository;
        this.exchangeRateService = exchangeRateService;
    }

    public synchronized Account addAccount(AccountDTO accountDTO) throws AccountAlreadyExistException {
        Optional<Account> optionalAccount = accountRepository.checkIfExist(accountDTO.getId());
        if(optionalAccount.isPresent()) {
            throw new AccountAlreadyExistException();
        }

        Account account = mapDTOToAccount(accountDTO);
        return accountRepository.save(account);
    }

    private Account mapDTOToAccount(AccountDTO accountDTO) {
        return new Account(accountDTO.getId(), accountDTO.getUserId(), accountDTO.getCurrency(), BigDecimal.valueOf(accountDTO.getBalance()));
    }

    public synchronized void transferMoney(UUID accountFromId, UUID accountToId, BigDecimal amount) throws AccountNoExistException, IOException, InsufficientFundsException {
        Account accountFrom = accountRepository.findById(accountFromId);
        Account accountTo = accountRepository.findById(accountToId);

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

        Account updatedAccountFrom = getNewAccount(accountFrom, newBalanceForFromAccount);
        Account updatedAccountTo = getNewAccount(accountTo, newBalanceForToAccount);

        accountRepository.update(updatedAccountFrom);
        accountRepository.update(updatedAccountTo);
    }

    private Account getNewAccount(Account oldAccount, BigDecimal newBalance) {
        return new Account(oldAccount.getId(), oldAccount.getUserId(), oldAccount.getCurrency(), newBalance);
    }

    public BigDecimal getCurrenciesRate(String from, String to) throws IOException {
        return exchangeRateService.getCurrenciesRate(from, to);
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }


}
