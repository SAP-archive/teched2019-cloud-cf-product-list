package com.sap.cp.cf.demoapps;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfigurationDefault;
import com.sap.cloud.security.xsuaa.XsuaaServicePropertySourceFactory;
import com.sap.cloud.security.xsuaa.token.TokenAuthenticationConverter;
import com.sap.cloud.security.xsuaa.token.authentication.XsuaaJwtDecoderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
@PropertySource(factory = XsuaaServicePropertySourceFactory.class, value = { "" })
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

	@Value("${vcap.services.xsuaa.credentials.xsappname:product-list}")
	private String xsAppName;

	@Autowired
	XsuaaServiceConfigurationDefault xsuaaServiceConfiguration;

	@Override
	public void configure(final HttpSecurity http) throws Exception {

		// @formatter:off
		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER)
				.and()
					.authorizeRequests()
					.antMatchers(GET, "/health").permitAll()
					.antMatchers(GET, "/**").hasAuthority("read")
					.anyRequest().denyAll() // deny anything not configured above
				.and()
					.oauth2ResourceServer()
					.jwt()
					.jwtAuthenticationConverter(getJwtAuthenticationConverter());

		// @formatter:on
	}

	/**
	 * Customizes how GrantedAuthority are derived from a Jwt
	 */
	Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
		TokenAuthenticationConverter converter = new TokenAuthenticationConverter(xsuaaServiceConfiguration);
		converter.setLocalScopeAsAuthorities(true);
		return converter;
	}

	@Bean
	JwtDecoder jwtDecoder() {
		return new XsuaaJwtDecoderBuilder(xsuaaServiceConfiguration).build();
	}

	@Bean
	XsuaaServiceConfigurationDefault config() {
		return new XsuaaServiceConfigurationDefault();
	}

}