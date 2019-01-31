package com.faunadb.persistence.common;

import com.faunadb.client.FaunaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.MalformedURLException;

@Configuration
public class FaunaClientConfig {

    @Autowired
    private FaunaClientProperties faunaProperties;

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FaunaClient faunaClient() throws MalformedURLException {
        FaunaClient client =
            FaunaClient.builder()
                .withEndpoint(faunaProperties.getEndpoint())
                .withSecret(faunaProperties.getSecret())
                .build();

        return client;
    }

}
