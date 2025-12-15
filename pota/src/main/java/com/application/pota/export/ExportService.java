package com.application.pota.export;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private ExportRepository exportRepository;

    public ByteArrayInputStream exportExcelKelayakan() throws IOException {
        List<KelayakanSidangDTO> data = exportRepository.getDataKelayakan();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Kelayakan Sidang");

        int rowNum = 0;

        // header
        Row header = sheet.createRow(rowNum++);
        header.createCell(0).setCellValue("Nama Mahasiswa");
        header.createCell(1).setCellValue("Topik");
        header.createCell(2).setCellValue("Jumlah Bimbingan Pra UTS");
        header.createCell(3).setCellValue("Jumlah Bimbingan Pasca UTS");
        header.createCell(4).setCellValue("Kelayakan");

        // isi data
        for (KelayakanSidangDTO item : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getNama());
            row.createCell(1).setCellValue(item.getTopik());
            row.createCell(2).setCellValue(item.getPraUts());
            row.createCell(3).setCellValue(item.getPascaUts());
            row.createCell(4).setCellValue(item.getKelayakan());
        }

        // auto size column
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}