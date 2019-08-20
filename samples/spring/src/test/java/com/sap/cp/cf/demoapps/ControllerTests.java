package com.sap.cp.cf.demoapps;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.test.JwtGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"xsuaa.xsappname=product-list!t22",
		"xsuaa.clientid=sb-product-list!t22"}, classes = { SecurityConfiguration.class, Controller.class, Application.class})
@ActiveProfiles("uaamock")
@AutoConfigureMockMvc
public class ControllerTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private CommandLineRunner runner;

	@Autowired
	private XsuaaServiceConfiguration xsuaaServiceConfiguration;

	@MockBean
	private ProductRepo productRepo;

	private static final String PRODUCT_NAME = "TestProduct";

	@Before
	public void setup() {
		given(productRepo.findByName(PRODUCT_NAME)).willReturn(Arrays.asList(new Product(PRODUCT_NAME)));
	}

	@Test
	public void test() throws Exception {
		String jwtRead = new JwtGenerator(xsuaaServiceConfiguration.getClientId())
				.addScopes(new String[] { "openid", getGlobalScope("read")})
				.getTokenForAuthorizationHeader();

		mvc.perform(get("/productsByParam?name=" + PRODUCT_NAME).with(bearerToken(jwtRead))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", is(PRODUCT_NAME)));
	}

	@Test
	public void test_unauthorized_403() throws Exception {
		String jwtWithoutScopes = new JwtGenerator(xsuaaServiceConfiguration.getClientId())
				.getTokenForAuthorizationHeader();
		mvc.perform(get("/productsByParam?name=" + PRODUCT_NAME).with(bearerToken(jwtWithoutScopes))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	public void test_healthcheck_without_authentication() throws Exception {
		mvc.perform(get("/actuator/health")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	private String getGlobalScope(String localScope) {
		Assert.hasText(xsuaaServiceConfiguration.getAppId(), "make sure that xsuaa.xsappname is configured properly.");
		return xsuaaServiceConfiguration.getAppId() + "." + localScope;
	}

	private static class BearerTokenRequestPostProcessor implements RequestPostProcessor {
		private String token;

		public BearerTokenRequestPostProcessor(String token) {
			this.token = token;
		}

		@Override
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			request.addHeader("Authorization", this.token);
			return request;
		}
	}

	private static BearerTokenRequestPostProcessor bearerToken(String token) {
		return new BearerTokenRequestPostProcessor(token);
	}
}
