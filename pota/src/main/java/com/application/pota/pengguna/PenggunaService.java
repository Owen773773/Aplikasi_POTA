package com.application.pota.pengguna;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PenggunaService {
    private final PenggunaRepository PenggunaRepository;

    public Pengguna getProfilePengguna(String idPengguna) {
        return PenggunaRepository.getById(idPengguna);
    }

    public Pengguna authenticatePengguna(String username, String password) {
        return PenggunaRepository.authenticate(username, password);
    }

    public void ubaStatus(String idPengguna) {
         PenggunaRepository.ubahStatus(idPengguna);
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

    public void delete(String id) {
        PenggunaRepository.delete(id);
    }
}