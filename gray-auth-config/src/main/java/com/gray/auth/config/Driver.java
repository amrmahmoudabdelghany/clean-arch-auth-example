package com.gray.auth.config;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.gray.auth.infra.repo.jpa.repo" })
@EntityScan(basePackages = "com.gray.auth.infra.repo.jpa.repo.models")
public class Driver {


    public static void main(String args[]){

        ConfigurableApplicationContext run = SpringApplication.run(Driver.class);

    }
}
