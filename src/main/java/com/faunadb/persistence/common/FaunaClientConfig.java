package com.faunadb.persistence.common;

import com.faunadb.client.FaunaClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class FaunaClientConfig {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FaunaClient faunaClient() {
        FaunaClient client =
            FaunaClient.builder()
                .withSecret("fnADF7kfJvACBNbQYctBjNyhlstb5gy449LKA8sb")
                .build();

        return client;
    }

}
