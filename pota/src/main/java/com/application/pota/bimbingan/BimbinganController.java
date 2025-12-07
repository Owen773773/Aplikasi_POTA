package com.application.pota.bimbingan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller

public class BimbinganController {
    @Autowired
    private BimbinganService bimbinganService;
    @GetMapping("/beranda")
    public String beranda(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        Bimbingan bimbinganTerdekat = bimbinganService.getJadwalTerdekat(idPengguna);
        model.addAttribute("bimbinganTerdekat", bimbinganTerdekat);
        return "mahasiswa/DashboardMahasiswa";
    }
}