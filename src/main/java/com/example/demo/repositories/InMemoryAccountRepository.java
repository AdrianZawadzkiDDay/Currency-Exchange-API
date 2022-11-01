package com.example.demo.repositories;

import com.example.demo.exception.AccountNoExistException;
import com.example.demo.model.Account;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAccountRepository implements AccountRepository {
    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account save(Account account) {
        if(account.getId() == null) {
            account.setId(UUID.randomUUID());
        }
        accounts.put(account.getId(), account);
        return account;
    }

    @Override
    public Account findById(UUID uuid) throws AccountNoExistException {
        Optional<Account> optionalAccount = Optional.ofNullable(accounts.get(uuid));

        if(optionalAccount.isPresent()) {
            return optionalAccount.get();
        } else {
            throw new AccountNoExistException();
        }
    }

    @Override
    public Optional<Account> checkIfExist(UUID uuid) {
        return Optional.ofNullable(accounts.get(uuid));
    }

    @Override
    public void update(Account account) throws AccountNoExistException {
        UUID uuid = account.getId();

        Optional<Account> optionalAccount = Optional.ofNullable(accounts.get(uuid));
        if(optionalAccount.isPresent()) {
            accounts.put(uuid, account);
        } else {
            throw new AccountNoExistException();
        }
    }

    public void deleteAll() {
        accounts.clear();;
    }
}
