package com.sap.cp.cf.demoapps;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	@Bean
	CommandLineRunner runner(final ProductRepo productRepo) {
		return strings -> {

			final List<Product> products = Arrays.asList(
					new Product("Notebook Basic 15",
							"Notebook Basic 15 with 1,7GHz - 15\" XGA - 1024MB DDR2 SDRAM - 40GB Hard Disc", "956.00",
							"EUR", "images/HT-1000.jpg"),
					new Product("Notebook Professional 15",
							"Notebook Professional 15 with 2,3GHz - 15\" XGA - 2048MB DDR2 SDRAM - 40GB Hard Disc - DVD-Writer (DVD-R/+R/-RW/-RAM)",
							"1999.00", "EUR", "images/HT-1010.jpg"),
					new Product("Ergo Screen",
							"17\" Optimum Resolution 1024 x 768 @ 85Hz, Max resolution 1280 x 960 @ 75Hz, Dot Pitch: 0.27mm",
							"230.00", "EUR", "images/HT-1030.jpg"));

			productRepo.saveAll(products);

			productRepo.findAll().forEach(System.out::println);
		};
	}

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
