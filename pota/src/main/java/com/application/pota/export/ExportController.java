package com.application.pota.export;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/admin/laporan")
    public void getExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=kelayakan_sidang.xlsx");

        ByteArrayInputStream stream = exportService.exportExcelKelayakan();
        IOUtils.copy(stream, response.getOutputStream());
        response.flushBuffer();
    }
}