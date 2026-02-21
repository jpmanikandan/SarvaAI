
package com.sarva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableJpaRepositories("com.sarva.repository")
@EntityScan("com.sarva.entity")
public class SarvaBhashaAiApplication {

    public static void main(String[] args) {
        System.setProperty("com.sun.net.ssl.checkRevocation", "false");
        SpringApplication.run(SarvaBhashaAiApplication.class, args);
    }
}
