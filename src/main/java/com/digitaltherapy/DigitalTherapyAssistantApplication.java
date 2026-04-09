package com.digitaltherapy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import com.digitaltherapy.cli.DigitalTherapyCLIClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(title = "Digital Therapy Assistant API", version = "1.0"),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class DigitalTherapyAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalTherapyAssistantApplication.class, args);
    }

    @Bean
    CommandLineRunner cliRunner(DigitalTherapyCLIClient cliClient,
                                @Value("${app.cli.enabled:false}") boolean cliEnabled) {
        return args -> {
            if (cliEnabled) {
                cliClient.start();
            }
        };
    }
}
