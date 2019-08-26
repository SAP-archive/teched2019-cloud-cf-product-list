package com.sap.cp.cf.demoapps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class Controller {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	private final ProductRepo productRepo;

	@Autowired
	public Controller(ProductRepo productRepo) {
		this.productRepo = productRepo;
	}

	@GetMapping("/productsByParam")
	public Collection<Product> getProductByName(@RequestParam(value = "name") String name) {
		logger.info("***First - Retrieving details for '{}'.", name);
		return productRepo.findByName(name);
	}
}
