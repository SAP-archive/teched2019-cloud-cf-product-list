package com.sap.cp.cf.demoapps;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(Controller.class)
public class ControllerTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private CommandLineRunner runner;

	@MockBean
	private ProductRepo productRepo;

	@Test
	public void test() throws Exception {
		final String productName = "TestProduct";
		given(productRepo.findByName(productName)).willReturn(Arrays.asList(new Product(productName)));
		mvc.perform(get("/productsByParam?name=" + productName).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].name", is(productName)));
	}

}
