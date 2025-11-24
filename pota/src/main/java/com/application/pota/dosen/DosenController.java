package com.application.pota.dosen;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dosen")
class DosenController {
    @GetMapping({"/", ""})
    public String berandaDefault() {
        return beranda();
    }
    
    @GetMapping("/beranda")
    public String beranda() {
        return "templateDosen";
    }
}
