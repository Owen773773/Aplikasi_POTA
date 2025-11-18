package com.application.pota.auth;

import com.application.pota.model.Pengguna;
import com.application.pota.repository.PenggunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PenggunaRepository penggunaRepository;
    private final BCryptPasswordEncoder passwordEncoder; // Untuk membandingkan password yang di-hash

    @Autowired
    public AuthService(PenggunaRepository penggunaRepository) {
        this.penggunaRepository = penggunaRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Inisialisasi encoder
    }

    // Mengikuti diagram: + authChecking(username : String, password : String): boolean
    public boolean authChecking(String username, String rawPassword) {
        Pengguna pengguna = penggunaRepository.getByUsername(username);

        if (pengguna == null || !pengguna.isStatusAktif()) {
            return false; // Pengguna tidak ditemukan atau tidak aktif
        }
        
        // Membandingkan password mentah (rawPassword) dengan password yang di-hash di repository
        return passwordEncoder.matches(rawPassword, pengguna.getPassword());
    }

    // Service untuk simulasi reset password (Menggunakan edit pada Repository)
    public boolean resetPassword(String username, String newRawPassword) {
        Pengguna pengguna = penggunaRepository.getByUsername(username);

        if (pengguna != null && pengguna.isStatusAktif()) {
            // Hash password baru sebelum menyimpan
            String newHashedPassword = passwordEncoder.encode(newRawPassword);
            pengguna.setPassword(newHashedPassword);
            
            penggunaRepository.edit(pengguna); // Update pengguna di Repository
            return true;
        }
        return false;
    }
}