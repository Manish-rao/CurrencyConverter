package com.neo.currencyconvertor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.neo.currencyconvertor.model.BestCurrency;
import com.neo.currencyconvertor.service.CurrencyConvertorService;
import com.neo.currencyconvertor.util.ExcelGenerator;

@RestController
public class CurrencyConvertorController {

	@Autowired
	private CurrencyConvertorService currencyConvertorService;

	@Autowired
	ExcelGenerator excelGenerator;

	@GetMapping("/convertCurrency/{sourceCurrency}/{targetCurrency}")
	public ResponseEntity<Resource> convertSourcToTarget(@PathVariable String sourceCurrency,
			@PathVariable String targetCurrency) {
		String filename = "bestCurrency.xlsx";
		long timeNow = System.currentTimeMillis();
		BestCurrency bestCurrency = currencyConvertorService.convertSourceToTargetCurrency(sourceCurrency,
				targetCurrency);
		InputStreamResource file = new InputStreamResource(excelGenerator.bestCurrencyToExcel(bestCurrency));

		long timeExit = System.currentTimeMillis();
		System.out.println("Total time is :" + (timeExit - timeNow));
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
	}

}
