package com.application.pota.mahasiswa;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mahasiswa")
public class MahasiswaController {
    @GetMapping({"/", ""})
    public String berandaDefault() {
        return beranda();
    }
    
    @GetMapping("/beranda")
    public String beranda() {
        return "TemplateMahasiswa";
    }
}
