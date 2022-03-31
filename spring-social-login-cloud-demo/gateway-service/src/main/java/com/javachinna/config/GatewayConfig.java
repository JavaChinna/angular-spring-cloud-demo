package com.javachinna.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

	@Autowired
	AuthenticationFilter filter;

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes().route("user-auth-service", r -> r
				.path("/api/users/**", "/api/auth/**", "/oauth2/authorization/**", "/login/oauth2/code/**")
				.filters(f -> f.filter(filter).circuitBreaker(
						config -> config.setName("user-service-circuit-breaker").setFallbackUri("forward:/user-auth-fallback")))
				.uri("lb://user-auth-service"))
				.route("product-service",
						r -> r.path("/api/products/**").filters(f -> f.filter(filter)).uri("lb://product-service"))
				.route("order-service",
						r -> r.path("/api/order/**").filters(f -> f.filter(filter)).uri("lb://order-service"))
				.build();
	}
}
