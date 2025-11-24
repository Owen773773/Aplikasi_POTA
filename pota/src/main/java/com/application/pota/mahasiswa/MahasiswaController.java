package com.application.pota.mahasiswa;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@RequestMapping("/")
public class MahasiswaController {
    @PostMapping("/mahasiswa/beranda")
    @ResponseBody
    public String beranda(Model model, @RequestParam String akunHolder, @RequestParam String passHolder) {
        return akunHolder + " " + passHolder;
    }
}
