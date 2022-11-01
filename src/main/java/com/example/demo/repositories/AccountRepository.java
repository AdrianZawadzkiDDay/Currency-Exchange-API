package com.example.demo.repositories;


import com.example.demo.exception.AccountNoExistException;
import com.example.demo.model.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    List<Account> findAll();
    Account save(Account account);

    Account findById(UUID uuid) throws AccountNoExistException;

    Optional<Account> checkIfExist(UUID uuid);


    void update(Account account) throws AccountNoExistException;
}
