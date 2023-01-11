package com.neo.currencyconvertor.model;

public class BestCurrency {

	private String currencyCode;
	private String country;
	private Double currency;
	private String bestPath;

	public BestCurrency(String currencyCode, String country, Double currency, String bestPath) {
		super();
		this.currencyCode = currencyCode;
		this.country = country;
		this.currency = currency;
		this.bestPath = bestPath;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double getCurrency() {
		return currency;
	}

	public void setCurrency(Double currency) {
		this.currency = currency;
	}

	public String getBestPath() {
		return bestPath;
	}

	public void setBestPath(String bestPath) {
		this.bestPath = bestPath;
	}

}
