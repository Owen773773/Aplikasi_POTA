// package com.application.pota.ExcelJadwal;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// @Controller
// @RequestMapping("/admin")
// public class ExcelJadwalController {

//     @Autowired
//     private ExcelJadwalService service;

//     @PostMapping("/upload")
//     public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {
//         if (file.isEmpty()) {
//             ra.addFlashAttribute("error", "File kosong!");
//             return "redirect:/admin/ruangan";
//         }

//         try {
//             service.saveJadwalFromExcel(file);
//             ra.addFlashAttribute("success", "Berhasil upload jadwal!");
//         } catch (Exception e) {
//             e.printStackTrace();
//             ra.addFlashAttribute("error", "Gagal: " + e.getMessage());
//         }

//         return "redirect:/admin/ruangan";
//     }
// }