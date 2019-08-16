package com.sap.cp.cf.demoapps;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.sap.cp.cf.demoapps.Product;
import com.sap.cp.cf.demoapps.ProductRepo;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductRepoTests {

	@MockBean
	private CommandLineRunner runner;

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ProductRepo productRepo;

	@Test
	public void test() throws Exception {
		final String productName = "TestProduct";
		entityManager.persist(new Product(productName));
		final Collection<Product> products = productRepo.findByName(productName);
		final Optional<Product> result = products.stream().findAny();
		assertThat(result.isPresent());
		assertThat(result.get().getName()).isEqualTo(productName);
	}

}
