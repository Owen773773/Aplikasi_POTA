package com.application.pota.notifikasi;

import java.util.List;

public interface NotifikasiRepository {
    Notifikasi getById(int id);//bisi perlu
    List<Notifikasi> getNotifikasiLengkapByUsername(String username);
    List<Notifikasi> getNotifikasiLengkapByIdPengguna(String idPengguna);
    Integer insertNotifikasi(String tipe);
    void insertMahasiswaNotifikasi(String idMhs, int idNotif);
    void insertDosenNotifikasi(String idPengguna, int idNotif);
    void insertBimbinganNotifikasi(int idNotif, int idBim);
}