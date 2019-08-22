package com.sap.cp.cf.demoapps;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;

@RepositoryRestResource // path = "products"
public interface ProductRepo extends JpaRepository<Product, String> {

	// TODO see https://github.com/SAP/cloud-cf-product-list-sample/blob/teched2019/exercises/09_secure/Spring.md
	public Collection<Product> findByName(String name);
}
