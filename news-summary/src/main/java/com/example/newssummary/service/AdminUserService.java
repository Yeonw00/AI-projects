package com.example.newssummary.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.newssummary.dto.admin.AdminUserView;
import com.example.newssummary.repository.admin.AdminUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {
	
	@Autowired
	private AdminUserRepository adminUserRepository;
	
	public Page<AdminUserView> getUsers(
			int page,
			int size,
			String keyword,
			String role,
			Boolean social
	){
		Pageable pageable = PageRequest.of(
			page, 
			size, 
			Sort.by(Sort.Direction.DESC, "createdAt")
		);
		
		return adminUserRepository.findAdminUsers(
			keyword, 
			role, 
			social, 
			pageable
		);
	}
	
	public List<AdminUserView> getUsersForExport(
			String keyword,
			String role,
			Boolean social
	) {
		return adminUserRepository.findAdminUsersForExport(keyword, role, social);
	}
	
	public void exportUsers(List<AdminUserView> rows, OutputStream os) throws IOException {
		try (Workbook wb = new XSSFWorkbook()) {
			Sheet sheet = wb.createSheet("User List");
			int rowIdx = 0;
			
			Row header = sheet.createRow(rowIdx++);
			header.createCell(0).setCellValue("id");
			header.createCell(1).setCellValue("email");
			header.createCell(2).setCellValue("username");
			header.createCell(3).setCellValue("role");
			header.createCell(4).setCellValue("socialLogin");
			header.createCell(5).setCellValue("coinBalance");
			header.createCell(6).setCellValue("createdAt");
			
			for (AdminUserView r : rows) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(r.getId());
				row.createCell(1).setCellValue(r.getEmail());
				row.createCell(2).setCellValue(r.getUserName());
				row.createCell(3).setCellValue(r.getRole());
				row.createCell(4).setCellValue(r.getSocialLogin());
				row.createCell(5).setCellValue(r.getCoinBalance());
				row.createCell(6).setCellValue(r.getCreatedAt());
			}
			
			wb.write(os);
		}
	}

}
