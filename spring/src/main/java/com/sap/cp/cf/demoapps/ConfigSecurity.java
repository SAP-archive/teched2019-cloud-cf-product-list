package com.sap.cp.cf.demoapps;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

import com.sap.xs2.security.commons.SAPOfflineTokenServicesCloud;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class ConfigSecurity extends ResourceServerConfigurerAdapter {

	@Value("${vcap.services.xsuaa.credentials.xsappname:product-list}")
	private String xsAppName;

	@Override
	public void configure(final HttpSecurity http) throws Exception {

		// @formatter:off
		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER).and()
				.authorizeRequests()
				.antMatchers(GET, "/health").permitAll()
				.antMatchers(GET, "/**").access(String.format("#oauth2.hasScope('%s.%s')", xsAppName, "read"))
				.anyRequest().denyAll(); // deny anything not configured above
		// @formatter:on
	}

	// offline token validation
	@Bean
	protected static SAPOfflineTokenServicesCloud offlineTokenServicesBean() {
		return new SAPOfflineTokenServicesCloud();
	}
}