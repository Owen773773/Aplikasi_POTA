package com.application.pota.pengguna;

import java.util.List;

public interface PenggunaRepository {
    Pengguna getById(String id);
    Pengguna getByUsername(String username);
    Pengguna authenticate(String username, String password);
    List<Pengguna> getByType(String tipeAkun);
    void add(Pengguna pengguna);
    void edit(Pengguna pengguna);
    void delete(String id);
    List<Pengguna>findAll();
    boolean getStatus(String idPengguna);
    void ubahStatus(String idPengguna);
}