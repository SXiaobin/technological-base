package org.hibernate.tutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HibernateTutorialHbmApplication {

    public static void main(String[] args) {
        SpringApplication.run(HibernateTutorialHbmApplication.class, args);
    }

}
