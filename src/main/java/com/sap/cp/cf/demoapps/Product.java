package com.sap.cp.cf.demoapps;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private String currencyCode;
	private String pictureUrl;

	public Product() {
	}

	public Product(final String name) {
		this.name = name;
	}

	public Product(final String name, final String description, final String price, final String currencyCode,
			final String pictureUrl) {
		super();
		this.name = name;
		this.description = description;
		this.price = new BigDecimal(price);
		this.currencyCode = currencyCode;
		this.pictureUrl = pictureUrl;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(final BigDecimal param) {
		price = param;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(final String param) {
		currencyCode = param;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(final String param) {
		pictureUrl = param;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price
				+ ", currencyCode=" + currencyCode + ", pictureUrl=" + pictureUrl + "]";
	}
}