package com.example.demo.controllers;

import com.example.demo.common.Currency;
import com.example.demo.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountsControllerE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    public void shouldReturnAccountsByCurrency() {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorisation", "myHardKey");
        HttpEntity httpEntity = new HttpEntity(headers);

        ResponseEntity<Account[]> actual = testRestTemplate.exchange("http://localhost:" + port + "/accounts/filter?currency=PLN",
                HttpMethod.GET,
                httpEntity,
                Account[].class);
        Assertions.assertEquals(Currency.PLN, Objects.requireNonNull(actual.getBody())[0].getCurrency());
    }
}
