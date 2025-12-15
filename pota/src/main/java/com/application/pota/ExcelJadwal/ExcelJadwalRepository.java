// package com.application.pota.ExcelJadwal;


// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;

// import org.apache.poi.ss.usermodel.Cell;
// import org.apache.poi.ss.usermodel.DateUtil;
// import org.apache.poi.ss.usermodel.Row;
// import org.apache.poi.ss.usermodel.Workbook;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;


// public class ExcelJadwalRepository {
//         public List<ExcelJadwal> readExcel(String filePath) throws IOException {
//         List<ExcelJadwal> jadwalList = new ArrayList<>();
        
//         FileInputStream fis = new FileInputStream(new File(filePath));
//         Workbook workbook = new XSSFWorkbook(fis);
//         Sheet sheet = workbook.getSheetAt(0);
        
//         // Skip header row (row 0)
//         for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//             Row row = sheet.getRow(i);
//             if (row != null) {
//                 ExcelJadwal jadwal = new ExcelJadwal();
                
//                 // Read idPengguna (column 0)
//                 Cell cell0 = row.getCell(0);
//                 if (cell0 != null) {
//                     jadwal.setIdPengguna(getCellValueAsString(cell0));
//                 }
                
//                 // Read tanggal (column 1)
//                 Cell cell1 = row.getCell(1);
//                 if (cell1 != null) {
//                     jadwal.setTanggal(getCellValueAsString(cell1));
//                 }
                
//                 // Read waktuMulai (column 2)
//                 Cell cell2 = row.getCell(2);
//                 if (cell2 != null) {
//                     jadwal.setWaktuMulai(getCellValueAsString(cell2));
//                 }
                
//                 // Read waktuSelesai (column 3)
//                 Cell cell3 = row.getCell(3);
//                 if (cell3 != null) {
//                     jadwal.setWaktuSelesai(getCellValueAsString(cell3));
//                 }
                
//                 jadwalList.add(jadwal);
//             }
//         }
        
//         workbook.close();
//         fis.close();
        
//         return jadwalList;
//     }
    
//     private String getCellValueAsString(Cell cell) {
//         switch (cell.getCellType()) {
//             case STRING:
//                 return cell.getStringCellValue();
//             case NUMERIC:
//                 if (DateUtil.isCellDateFormatted(cell)) {
//                     return cell.getDateCellValue().toString();
//                 } else {
//                     return String.valueOf((long) cell.getNumericCellValue());
//                 }
//             case BOOLEAN:
//                 return String.valueOf(cell.getBooleanCellValue());
//             case FORMULA:
//                 return cell.getCellFormula();
//             default:
//                 return "";
//         }
//     }
// }