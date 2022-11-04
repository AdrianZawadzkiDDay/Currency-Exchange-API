package com.example.demo.controllers;


import com.example.demo.common.Currency;
import com.example.demo.model.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.flywaydb.core.Flyway;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    Flyway flyway;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldReturnInitialAccount() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/accounts/all"))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Account[] accounts = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Account[].class);
        Assertions.assertEquals(Currency.PLN, accounts[0].getCurrency());

    }

    // 0f5ab74a-033f-4ad9-9b2e-e457741092af
    @Test
    public void shouldReturn404WhenGetToWrongEndpoint() throws Exception {
       mockMvc.perform(MockMvcRequestBuilders.get("/accounts/wrongEndpoint"))
                .andExpect(MockMvcResultMatchers.status().is(404)).andReturn();
    }


//    @AfterEach
//    public void get() {
//        Flyway.configure()
//                .cleanDisabled(false)
//                .load();
//        flyway.clean();
//        flyway.migrate();
//    }
}
