package com.revconnect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RevConnectApplication {

    private static final Logger logger = LogManager.getLogger(RevConnectApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RevConnectApplication.class, args);
        logger.info("==============================================");
        logger.info("   RevConnect Application Started!");
        logger.info("==============================================");
    }
}
