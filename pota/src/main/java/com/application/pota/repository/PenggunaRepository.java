package com.application.pota.repository;

import com.application.pota.model.Pengguna;
import java.util.List;

public interface PenggunaRepository {
    Pengguna getById(String id);
    Pengguna getByUsername(String username);
    List<Pengguna> getByType(String type); // Asumsi parameter type ditambahkan untuk filtering
    List<Pengguna> findAll(); // Tambahkan findAll untuk showAllUsers
    void add(Pengguna pengguna);
    void edit(Pengguna pengguna);
    void delete(String id);
}