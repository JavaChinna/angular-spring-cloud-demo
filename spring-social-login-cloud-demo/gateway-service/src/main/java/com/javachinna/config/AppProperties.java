package com.javachinna.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
	private final Auth auth = new Auth();

	@Getter
	@Setter
	public static class Auth {
		private String tokenSecret;
		private long tokenExpirationMsec;
	}
}