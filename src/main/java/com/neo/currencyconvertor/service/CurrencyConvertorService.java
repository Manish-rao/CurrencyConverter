package com.neo.currencyconvertor.service;

import com.neo.currencyconvertor.model.BestCurrency;

public interface CurrencyConvertorService {

	BestCurrency convertSourceToTargetCurrency(String source, String target);
}
