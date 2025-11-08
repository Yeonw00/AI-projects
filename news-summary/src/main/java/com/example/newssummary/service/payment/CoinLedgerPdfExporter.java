package com.example.newssummary.service.payment;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.newssummary.dao.LedgerType;
import com.example.newssummary.dto.LedgerEntryResponse;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component
public class CoinLedgerPdfExporter {
	
	private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public void export(List<LedgerEntryResponse> rows, OutputStream os) throws IOException, DocumentException {
		
		Document document = new Document(PageSize.A4.rotate()); // 가로방향
		PdfWriter.getInstance(document, os);
		
		BaseFont baseFont = BaseFont.createFont("C:/windows/Fonts/malgun.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font titleFont = new Font(baseFont, 16, Font.BOLD);
		Font headerFont = new Font(baseFont, 11, Font.BOLD);
		Font cellFont = new Font(baseFont, 10);
		
		document.open();
		
		// 제목
		Paragraph title = new Paragraph("코인 사용 내역", titleFont);
		title.setSpacingAfter(15f);
		document.add(title);
		
		// 표: 일시, 유형, 금액, 잔액, 설명, 주문ID
		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100);
		table.setWidths(new float[]{ 2.5f, 1.3f, 1.3f, 1.3f, 3.0f, 2.0f });
		
		// 헤더 생성 헬퍼
		addHeaderCell(table, "일시", headerFont);
		addHeaderCell(table, "유형", headerFont);
		addHeaderCell(table, "코인 변화", headerFont);
		addHeaderCell(table, "잔액", headerFont);
		addHeaderCell(table, "설명", headerFont);
		addHeaderCell(table, "주문ID", headerFont);
		
		// 데이터 행
		for (LedgerEntryResponse r : rows) {
			// amount는 항상 양수, 타입보고 부호 붙여줌
			long signedAmount = (r.getType() == LedgerType.USE || r.getType() == LedgerType.REFUND ? -r.getAmount() : r.getAmount());
			String amountStr = String.format("%+d", signedAmount);
			
			addBodyCell(table, r.getCreatedAt() != null ? DATE_TIME_FMT.format(r.getCreatedAt()) : "", cellFont);
			addBodyCell(table, r.getType() != null ? r.getType().name() : "", cellFont);
			addBodyCell(table, amountStr, cellFont);
			addBodyCell(table, r.getBalanceAfter() != null ? r.getBalanceAfter().toString() : "", cellFont);
			addBodyCell(table, r.getDescription() != null ? r.getDescription() : "", cellFont);
			addBodyCell(table, r.getOrderId() != null ? r.getOrderId() : "", cellFont);
		}
		
		document.add(table);
		document.close();
	}
	
	private void addHeaderCell(PdfPTable table, String text, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(4f);
		table.addCell(cell);
	}
	
	private void addBodyCell(PdfPTable table, String text, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(3f);
		table.addCell(cell);
	}
}
