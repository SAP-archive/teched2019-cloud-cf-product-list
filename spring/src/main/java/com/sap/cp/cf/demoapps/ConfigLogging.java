package com.sap.cp.cf.demoapps;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sap.hcp.cf.logging.servlet.filter.RequestLoggingFilter;

@Configuration
public class ConfigLogging {

	@SuppressWarnings("static-method")
	@Bean
	public Filter requestLoggingFilter() {
		return new RequestLoggingFilter();
	}
}