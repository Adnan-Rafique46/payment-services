package com.bundlen.paymentservices;

import com.stripe.Stripe;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages={"com.bundlen"})
public class PaymentServicesApiWebApplication extends SpringBootServletInitializer {


	//*
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(PaymentServicesApiWebApplication.class, args);
		Environment environment = context.getEnvironment();
		Stripe.apiKey = environment.getRequiredProperty("stripe.secret-key");
	}

	@Bean
	public OpenAPI getOpenAPI() {
		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes("OAuth", new SecurityScheme()
								.type(SecurityScheme.Type.APIKEY)
								.scheme("bearer")
								.bearerFormat("jwt")
								.in(SecurityScheme.In.HEADER)
								.name("Authorization")
						)).addSecurityItem(new SecurityRequirement().addList("OAuth"));
	}

}
