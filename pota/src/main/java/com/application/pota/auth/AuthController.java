package com.application.pota.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// Asumsi kita menggunakan Controller untuk melayani halaman HTML dan form submission
@Controller
public class AuthController {

    private final AuthService authService;

    // Tidak perlu Dosen/Mahasiswa/AdminService karena AuthService sudah mencakup PenggunaRepository
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoint untuk menampilkan halaman login (GET /login)
    @GetMapping("/login")
    public String showLoginForm() {
        // Akan merujuk ke src/main/resources/templates/Login.html
        return "Login"; 
    }

    // Endpoint untuk memproses form login (POST /login)
    // Menggantikan sendAuthAcceptance()
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                               @RequestParam String password) {
        
        // Memanggil service untuk cek otentikasi
        if (authService.authChecking(username, password)) {
            // Login sukses, redirect ke halaman utama (misalnya index.html)
            // Note: Dalam aplikasi nyata, ini akan menggunakan Spring Security untuk sesi
            return "redirect:/"; 
        } else {
            // Login gagal, kembali ke halaman login dengan pesan error
            // Tambahkan model untuk menampilkan pesan error di HTML
            return "redirect:/login?error=true";
        }
    }
    
    // Endpoint untuk menampilkan halaman lupa sandi (GET /lupasandi)
    @GetMapping("/lupasandi")
    public String showForgotPasswordForm() {
        // Akan merujuk ke src/main/resources/templates/LupaSandi.html
        return "LupaSandi"; 
    }
    
    // Endpoint untuk memproses reset password (POST /lupasandi)
    @PostMapping("/lupasandi")
    public String processResetPassword(@RequestParam String username,
                                       @RequestParam String newPassword) {
        
        // Proses reset password
        if (authService.resetPassword(username, newPassword)) {
            return "redirect:/login?resetSuccess=true"; // Reset berhasil, kembali ke login
        } else {
            return "redirect:/lupasandi?resetError=true"; // Reset gagal
        }
    }
}