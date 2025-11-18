package com.application.pota.repository;

import com.application.pota.model.Pengguna;
import com.application.pota.model.PenggunaImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PenggunaRepositoryImpl implements PenggunaRepository {

    private final Map<String, Pengguna> storage = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Inisialisasi data mock saat aplikasi dimulai
    @PostConstruct
    public void init() {
        Pengguna admin = new PenggunaImpl();
        admin.setIdPengguna("A001");
        admin.setUsername("admin1");
        // Password "adminpass" di-hash
        admin.setPassword(passwordEncoder.encode("adminpass")); 
        admin.setNama("Admin Utama");
        admin.setTipeAkun("Admin");
        admin.setStatusAktif(true);
        
        Pengguna dosen = new PenggunaImpl();
        dosen.setIdPengguna("D001");
        dosen.setUsername("dosen1");
        // Password "dosenpass" di-hash
        dosen.setPassword(passwordEncoder.encode("dosenpass"));
        dosen.setNama("Dr. Budi");
        dosen.setTipeAkun("Dosen");
        dosen.setStatusAktif(true);

        storage.put(admin.getIdPengguna(), admin);
        storage.put(dosen.getIdPengguna(), dosen);
    }

    @Override
    public Pengguna getById(String id) {
        return storage.get(id);
    }

    @Override
    public Pengguna getByUsername(String username) {
        return storage.values().stream()
                .filter(p -> p.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Pengguna> getByType(String type) {
        return storage.values().stream()
                .filter(p -> p.getTipeAkun().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pengguna> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void add(Pengguna pengguna) {
        // Logika untuk menambahkan ID baru jika belum ada
        storage.put(pengguna.getIdPengguna(), pengguna);
    }

    @Override
    public void edit(Pengguna pengguna) {
        if (storage.containsKey(pengguna.getIdPengguna())) {
            storage.put(pengguna.getIdPengguna(), pengguna);
        }
    }

    @Override
    public void delete(String id) {
        storage.remove(id);
    }
}