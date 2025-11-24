package com.application.pota.admin;

import com.application.pota.pengguna.Pengguna;
import com.application.pota.pengguna.PenggunaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PenggunaService penggunaService;

    @GetMapping({"", "/"})
    public String halamanAdmin(Model model) {
        return halamanAkun(model);
    }
    
    @GetMapping("/akun")
    public String halamanAkun(Model model) {
        List<Pengguna> listUser = penggunaService.getAllPengguna();

        if (listUser == null) {
            listUser = new ArrayList<>();
        }

        model.addAttribute("listUser", listUser);

        model.addAttribute("user", new Pengguna());
        model.addAttribute("thesis", new Object());

        return "Admin_Akun";
    }

    @GetMapping("/ruangan")
    public String halamanRuangan(Model model) {
        return "Admin_Ruangan";
    }

    @GetMapping("/pengaturan")
        public String halamanPengaturan() {
            return "Admin_Pengaturan";
        }
}