package com.application.pota.auth;

import com.application.pota.pengguna.Pengguna;
import com.application.pota.pengguna.PenggunaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    private PenggunaService penggunaService;

    @GetMapping("/")
    public String index() {
        return "login";
    }

    @PostMapping("/login")
    public String authenticate(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            HttpServletResponse response,
            Model model
    ) {
        Pengguna pengguna = penggunaService.authenticatePengguna(username, password);

        if (pengguna != null) {
            String tipeAkun = pengguna.getTipeAkun();
            session.setAttribute("user", pengguna);
            Cookie cookie = new Cookie("idPengguna", String.valueOf(pengguna.getIdPengguna()));
            cookie.setMaxAge(3 * 60 * 60); // 3 jam
            cookie.setPath("/");
            response.addCookie(cookie);


            if (tipeAkun.equalsIgnoreCase("Mahasiswa")) {
                return "redirect:/mahasiswa";
            } else if (tipeAkun.equalsIgnoreCase("Dosen")) {
                return "redirect:/dosen";
            } else if (tipeAkun.equalsIgnoreCase("Admin")) {
                return "redirect:/admin";
            } else {
                model.addAttribute("error", "Tipe akun tidak dikenali.");
                return "login";
            }
        } else {
            model.addAttribute("error", "Username atau password salah.");
            return "login";
        }
    }

    @GetMapping("/lupasandi")
    public String forgotPasswordPage() {
        return "LupaSandi";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        if (session != null) {
            session.invalidate();
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("idPengguna".equals(cookie.getName())) {
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }

        return "redirect:/";
    }
}