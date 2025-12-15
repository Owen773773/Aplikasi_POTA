// package com.application.pota.ExcelJadwal;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;


// @Service
// public class ExcelJadwalService {
//     @Autowired
//     private ExcelJadwalRepository excelRepository;
    
//     private static final String UPLOAD_DIR = "uploads/";

//     public List<ExcelJadwal> readExcelFile(MultipartFile file) {
//         try {
//             // Simpan file temporary
//             String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//             Path uploadPath = Paths.get(UPLOAD_DIR);
            
//             if (!Files.exists(uploadPath)) {
//                 Files.createDirectories(uploadPath);
//             }
            
//             Path filePath = uploadPath.resolve(fileName);
//             Files.copy(file.getInputStream(), filePath);
            
//             // Baca data dari Excel
//             List<ExcelJadwal> dataList = excelRepository.readExcelFile(filePath.toString());
            
//             // Hapus file temporary
//             Files.deleteIfExists(filePath);
            
//             return dataList;
            
//         } catch (IOException e) {
//             throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
//         }
//     }
    
//     public List<ExcelJadwal> readExcelFromPath(String filePath) {
//         return excelRepository.readExcelFile(filePath);
//     }
// }
