package com.sap.cp.cf.demoapps;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;

@RepositoryRestResource // path = "products"
public interface ProductRepo extends JpaRepository<Product, String> {

	// TODO see https://github.com/SAP-samples/cloud-cf-product-list/blob/teched2019/docs/09_secure/Spring.md
	public Collection<Product> findByName(String name);
}
