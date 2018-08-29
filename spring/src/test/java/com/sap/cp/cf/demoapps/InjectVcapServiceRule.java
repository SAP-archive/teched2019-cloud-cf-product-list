package com.sap.cp.cf.demoapps;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.rules.ExternalResource;

/**
 *
 *
 */
public class InjectVcapServiceRule extends ExternalResource {

	private static final String VCAP_SERVICES = "VCAP_SERVICES";
	private String injectionValue;


	public InjectVcapServiceRule() {
		this.injectionValue = null;
	}

	public InjectVcapServiceRule(final String injectionValue) {
		this.injectionValue = injectionValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.rules.ExternalResource#before()
	 */
	@Override
	protected void before() throws Throwable {
		if (this.injectionValue == null || this.injectionValue.trim().length() == 0) {
			injectionValue = IOUtils.toString(InjectVcapServiceRule.class.getResourceAsStream("/vcap.json"), StandardCharsets.UTF_8);
		}
		EnvironmentInjectionUtil.injectEnvironmentVariable(VCAP_SERVICES, injectionValue);
		if (injectionValue!=null) {
			assertThat(System.getenv(VCAP_SERVICES)).isNotEmpty();
		}
	}


}