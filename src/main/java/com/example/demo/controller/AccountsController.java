package com.example.demo.controller;

import com.example.demo.common.Currency;
import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.TransferMoneyDTO;
import com.example.demo.exception.AccountAlreadyExistException;
import com.example.demo.exception.AccountNoExistException;
import com.example.demo.exception.InsufficientFundsException;
import com.example.demo.model.Account;
import com.example.demo.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private static Logger logger = LoggerFactory.getLogger(AccountsController.class);

    private final AccountService accountService;

    @Autowired
    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Account>> all() {
        return new ResponseEntity<List<Account>>(accountService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity transferMoney(@RequestBody TransferMoneyDTO transferMoneyDTO) {
        UUID accountFromId = transferMoneyDTO.getAccountFromId();
        UUID accountToId = transferMoneyDTO.getAccountToId();
        BigDecimal amount = BigDecimal.valueOf(transferMoneyDTO.getAmount());

        try {
            accountService.transferMoney(accountFromId, accountToId, amount);
            logger.info("Money successfully transferred");

            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        catch (AccountNoExistException e) {
            logger.error("One of accounts doesn't exist. Account ids: from {} to {}", accountFromId, accountToId);

            return new ResponseEntity(HttpStatus.NOT_FOUND);

        } catch (InsufficientFundsException e) {
            logger.error("Insufficient funds on account: {}", accountFromId);
            return new ResponseEntity(HttpStatus.CONFLICT);
        } catch (IOException e) {
            logger.error("Can't get exchange rate.");
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody AccountDTO accountDTO) {
        try {
            accountService.addAccount(accountDTO);
            return new ResponseEntity("Account created" ,HttpStatus.CREATED);

        } catch (AccountAlreadyExistException e) {
            return new ResponseEntity("Account with this id already exist", HttpStatus.CONFLICT);

        }
    }

}
