package com.neo.currencyconvertor.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.neo.currencyconvertor.model.BestCurrency;

@Component
public class ExcelGenerator {

	public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	static String[] HEADERs = { "Currency Code", "Country", "Amount of Currency left", "Best Conversion path" };
	static String SHEET = "BestCurrency";

	public ByteArrayInputStream bestCurrencyToExcel(BestCurrency bestCurrency) {

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(SHEET);

			// Header
			Row headerRow = sheet.createRow(0);

			for (int col = 0; col < HEADERs.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERs[col]);
			}

			int rowIdx = 1;
			Row row = sheet.createRow(rowIdx);

			row.createCell(0).setCellValue(bestCurrency.getCurrencyCode());
			row.createCell(1).setCellValue(bestCurrency.getCountry());
			row.createCell(2).setCellValue(bestCurrency.getCurrency());
			row.createCell(3).setCellValue(bestCurrency.getBestPath());

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
		}
	}
}
