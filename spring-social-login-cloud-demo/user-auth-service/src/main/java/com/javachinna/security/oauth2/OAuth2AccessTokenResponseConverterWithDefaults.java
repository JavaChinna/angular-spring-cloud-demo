package com.javachinna.security.oauth2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.StringUtils;

/**
 * @author Chinna
 */
public class OAuth2AccessTokenResponseConverterWithDefaults implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {
	private static final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = new HashSet<>(
			Arrays.asList(OAuth2ParameterNames.ACCESS_TOKEN, OAuth2ParameterNames.EXPIRES_IN,
					OAuth2ParameterNames.REFRESH_TOKEN, OAuth2ParameterNames.SCOPE, OAuth2ParameterNames.TOKEN_TYPE));

	@Override
	public OAuth2AccessTokenResponse convert(Map<String, Object> source) {
		String accessToken = getParameterValue(source, OAuth2ParameterNames.ACCESS_TOKEN);
		OAuth2AccessToken.TokenType accessTokenType = getAccessTokenType(source);
		long expiresIn = getExpiresIn(source);
		Set<String> scopes = getScopes(source);
		String refreshToken = getParameterValue(source, OAuth2ParameterNames.REFRESH_TOKEN);
		Map<String, Object> additionalParameters = new LinkedHashMap<>();
		for (Map.Entry<String, Object> entry : source.entrySet()) {
			if (!TOKEN_RESPONSE_PARAMETER_NAMES.contains(entry.getKey())) {
				additionalParameters.put(entry.getKey(), entry.getValue());
			}
		}
		// @formatter:off
		return OAuth2AccessTokenResponse.withToken(accessToken)
				.tokenType(accessTokenType)
				.expiresIn(expiresIn)
				.scopes(scopes)
				.refreshToken(refreshToken)
				.additionalParameters(additionalParameters)
				.build();
		// @formatter:on
	}

	private static OAuth2AccessToken.TokenType getAccessTokenType(Map<String, Object> tokenResponseParameters) {
		if (OAuth2AccessToken.TokenType.BEARER.getValue()
				.equalsIgnoreCase(getParameterValue(tokenResponseParameters, OAuth2ParameterNames.TOKEN_TYPE))) {
			return OAuth2AccessToken.TokenType.BEARER;
		}
		return OAuth2AccessToken.TokenType.BEARER;
	}

	private static long getExpiresIn(Map<String, Object> tokenResponseParameters) {
		return getParameterValue(tokenResponseParameters, OAuth2ParameterNames.EXPIRES_IN, 0L);
	}

	private static Set<String> getScopes(Map<String, Object> tokenResponseParameters) {
		if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
			String scope = getParameterValue(tokenResponseParameters, OAuth2ParameterNames.SCOPE);
			return new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
		}
		return Collections.emptySet();
	}

	private static String getParameterValue(Map<String, Object> tokenResponseParameters, String parameterName) {
		Object obj = tokenResponseParameters.get(parameterName);
		return (obj != null) ? obj.toString() : null;
	}

	private static long getParameterValue(Map<String, Object> tokenResponseParameters, String parameterName,
			long defaultValue) {
		long parameterValue = defaultValue;

		Object obj = tokenResponseParameters.get(parameterName);
		if (obj != null) {
			// Final classes Long and Integer do not need to be coerced
			if (obj.getClass() == Long.class) {
				parameterValue = (Long) obj;
			}
			else if (obj.getClass() == Integer.class) {
				parameterValue = (Integer) obj;
			}
			else {
				// Attempt to coerce to a long (typically from a String)
				try {
					parameterValue = Long.parseLong(obj.toString());
				}
				catch (NumberFormatException ignored) {
				}
			}
		}

		return parameterValue;
	}
}