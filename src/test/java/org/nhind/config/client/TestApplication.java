package org.nhind.config.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;


@SpringBootApplication
@ComponentScan({"org.nhindirect.config", "org.nhind.config"})
@EnableFeignClients({"org.nhind.config.rest.feign"})
@EnableR2dbcRepositories("org.nhindirect.config.repository")
@Import(HttpMessageConvertersAutoConfiguration.class)
public class TestApplication
{
    public static void main(String[] args) 
    {
        SpringApplication.run(TestApplication.class, args);
    }  
}
