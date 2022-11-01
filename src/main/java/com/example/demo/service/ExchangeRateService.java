package com.example.demo.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class ExchangeRateService {
    private static Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    public BigDecimal getCurrenciesRate(String from, String to) throws IOException {
        String url_str = "https://api.exchangerate.host/convert?from=" + from + "&to=" + to;
        URL url = new URL(url_str);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

        String req_result = jsonobj.get("result").getAsString();

        BigDecimal bigDecimalValue = new BigDecimal(req_result);

        logger.info("Rate value from {} to {} : {}", from, to, bigDecimalValue);

        return bigDecimalValue;
    }
}
