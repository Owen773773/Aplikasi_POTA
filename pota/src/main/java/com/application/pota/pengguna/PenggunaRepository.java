package com.application.pota.pengguna;

import java.util.List;

public interface PenggunaRepository {
    Pengguna getById(String id);
    Pengguna getByUsername(String username);
    List<Pengguna> getByType(String tipeAkun);
    void add(Pengguna pengguna);
    void edit(Pengguna pengguna);
    void delete(String id);
    List<Pengguna>findAll();
}