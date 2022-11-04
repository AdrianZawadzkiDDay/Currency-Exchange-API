package com.example.demo.repositories;

import com.example.demo.common.Currency;
import com.example.demo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountJpaRepository extends JpaRepository<Account, UUID> {

    @Override
    List<Account> findAll();

    @Override
    Optional<Account> findById(UUID uuid);

    @Override
    Account save(Account account);

    @Modifying
    @Query("Update Account a set a.balance = :balance where a.id = :id")
    void updateBalance(@Param(value = "id") UUID id, @Param(value = "balance") BigDecimal balance);

    Optional<List<Account>> findAllByCurrency(Currency currency);


}
