package com.nex.ticket.config;

import co.omise.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OmiseConfig {

    @Value("${omise.public-key}")
    private String publicKey;

    @Value("${omise.secret-key}")
    private String secretKey;

    @Bean
    public Client omiseClient() {
        try {
            return new Client.Builder()
                    .publicKey(publicKey)
                    .secretKey(secretKey)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Omise client", e);
        }
    }
}