package com.example.newssummary.service.payment;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.newssummary.dao.LedgerType;
import com.example.newssummary.dto.LedgerEntryResponse;
import com.example.newssummary.dto.LedgerPageResponse;

public class CoinLedgerExcelExporter {
	public void export(List<LedgerEntryResponse> rows, OutputStream os) throws IOException {
		try (Workbook wb = new XSSFWorkbook()) {
			Sheet sheet = wb.createSheet("Coin Ledger");
			int rowIdx = 0;
			
			Row header = sheet.createRow(rowIdx++);
			header.createCell(0).setCellValue("일시");
			header.createCell(1).setCellValue("유형");
			header.createCell(2).setCellValue("코인 변화");
			header.createCell(3).setCellValue("잔액");
			header.createCell(4).setCellValue("설명");
			header.createCell(5).setCellValue("주문번호");
			
			for (LedgerEntryResponse r : rows) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(r.getCreatedAt().toString());
				row.createCell(1).setCellValue(r.getType().name());
				long signedAmount = 
						(r.getType() == LedgerType.USE || r.getType() == LedgerType.REFUND) 
						? -r.getAmount() 
						: r.getAmount();
				row.createCell(2).setCellValue(signedAmount);
				row.createCell(3).setCellValue(r.getBalanceAfter());
				row.createCell(4).setCellValue(r.getDescription() != null ? r.getDescription() : "");
				row.createCell(5).setCellValue(r.getOrderId() != null ? r.getOrderId() : "");
			}
			
			wb.write(os);
		}
	}
}
