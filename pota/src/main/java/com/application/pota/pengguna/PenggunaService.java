package com.application.pota.pengguna;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PenggunaService {

    private final PenggunaRepository PenggunaRepository;  // Interface, bukan Jdbc!

    public Pengguna getProfilePengguna(String idPengguna) {
        return PenggunaRepository.getById(idPengguna);
    }

    public List<Pengguna> getAllPengguna() {
        return PenggunaRepository.findAll();
    }

    public void addPengguna(Pengguna Pengguna) {
        PenggunaRepository.add(Pengguna);
    }

    public void editPengguna(Pengguna Pengguna) {
        PenggunaRepository.edit(Pengguna);
    }
}