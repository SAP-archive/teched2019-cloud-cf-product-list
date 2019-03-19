package com.sap.cp.cf.demoapps;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import com.sap.cloud.security.xsuaa.test.JwtGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"xsuaa.xsappname=java-hello-world",
		"xsuaa.clientid=sb-java-hello-world",
		"xsuaa.url=${mockxsuaaserver.url}" }, classes = { ConfigSecurity.class, Controller.class, Application.class})
@ActiveProfiles("uaamock")
@AutoConfigureMockMvc(secure = false)
public class ControllerTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private CommandLineRunner runner;

	@MockBean
	private ProductRepo productRepo;

	JwtGenerator jwtGenerator = new JwtGenerator("sb-java-hello-world");
	@Test
	public void test() throws Exception {
		org.apache.commons.io.IOUtils a;
		final String productName = "TestProduct";
		given(productRepo.findByName(productName)).willReturn(Arrays.asList(new Product(productName)));
		mvc.perform(get("/productsByParam?name=" + productName).with(bearerToken(jwtGenerator.addScopes(new String[] { "openid","java-hello-world.read"}).getToken().getTokenValue())).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].name", is(productName)));

	}
	private static class BearerTokenRequestPostProcessor implements RequestPostProcessor {
		private String token;

		public BearerTokenRequestPostProcessor(String token) {
			this.token = token;
		}

		@Override
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			request.addHeader("Authorization", "Bearer " + this.token);
			return request;
		}
	}

	private static BearerTokenRequestPostProcessor bearerToken(String token) {
		return new BearerTokenRequestPostProcessor(token);
	}
}
