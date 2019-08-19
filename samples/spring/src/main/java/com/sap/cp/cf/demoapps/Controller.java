package com.sap.cp.cf.demoapps;

	import java.io.IOException;
	import java.util.Collection;

	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.http.MediaType;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.ResponseBody;
	import org.springframework.web.bind.annotation.RestController;

	@RestController
	public class Controller {

		private static final Logger logger = LoggerFactory.getLogger(Controller.class);

		@Autowired
		private ProductRepo productRepo;

		@GetMapping("/all")
		public Collection<Product> getAllProducts() {
			logger.info("***First - Retrieving all products");
			return productRepo.findAll();
		}

		@GetMapping("/productsByParam")
		public Collection<Product> getProductByName(@RequestParam(value = "name") String name) {
			logger.info("***First - Retrieving details for '{}'.", name);
			return productRepo.findByName(name);
		}

		@ResponseBody
		@GetMapping(value = "/images/{imageFile:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
		public byte[] getIcon(@PathVariable String imageFile) throws IOException {
//			ConnectivityConsumer connConsumer = new ConnectivityConsumer();
//			return connConsumer.getImageFromBackend(imageFile);
			return null;
		}
	}
