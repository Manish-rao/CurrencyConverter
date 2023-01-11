package com.neo.currencyconvertor.model;

public class Currency {

	Double exchangeRate;

	String fromCurrencyCode;

	String fromCurrencyName;

	String toCurrencyCode;

	String toCurrencyName;

	public Currency() {

	}

	public Currency(Double exchangeRate, String fromCurrencyCode, String fromCurrencyName, String toCurrencyCode,
			String toCurrencyName) {
		super();
		this.exchangeRate = exchangeRate;
		this.fromCurrencyCode = fromCurrencyCode;
		this.fromCurrencyName = fromCurrencyName;
		this.toCurrencyCode = toCurrencyCode;
		this.toCurrencyName = toCurrencyName;
	}

	public Double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getFromCurrencyCode() {
		return fromCurrencyCode;
	}

	public void setFromCurrencyCode(String fromCurrencyCode) {
		this.fromCurrencyCode = fromCurrencyCode;
	}

	public String getFromCurrencyName() {
		return fromCurrencyName;
	}

	public void setFromCurrencyName(String fromCurrencyName) {
		this.fromCurrencyName = fromCurrencyName;
	}

	public String getToCurrencyCode() {
		return toCurrencyCode;
	}

	public void setToCurrencyCode(String toCurrencyCode) {
		this.toCurrencyCode = toCurrencyCode;
	}

	public String getToCurrencyName() {
		return toCurrencyName;
	}

	public void setToCurrencyName(String toCurrencyName) {
		this.toCurrencyName = toCurrencyName;
	}

	@Override
	public String toString() {
		return "Currency [exchangeRate=" + exchangeRate + ", fromCurrencyCode=" + fromCurrencyCode
				+ ", fromCurrencyName=" + fromCurrencyName + ", toCurrencyCode=" + toCurrencyCode + ", toCurrencyName="
				+ toCurrencyName + "]";
	}

}
