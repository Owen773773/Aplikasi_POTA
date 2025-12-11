package com.application.pota.notifikasi;

import java.time.LocalDateTime;
import java.util.List;

public interface NotifikasiRepository {
    Notifikasi getById(int id);//bisi perlu

    List<Notifikasi> getNotifikasiLengkapByUsername(String username);

    List<Notifikasi> getNotifikasiLengkapByIdPengguna(String idPengguna);

    //    void buatNotifikasiBaru(Notifikasi notif, String username);
//    // List<Notifikasi> getTanggalBimbinganByUsername(String username);
    // List<Notifikasi> getWaktuMulaiBimbinganByUsername(String username);
    // List<Notifikasi> getWaktuSelesaiBimbinganByUsername(String username);
    // List<Notifikasi> getRuanganByUsername(String username);
    // List<Notifikasi> getWaktuAcaraByUsername(String username);//ini untuk kanan atas yg history
    Integer insertNotifikasi(String tipe);

    void insertMahasiswaNotifikasi(String idMhs, int idNotif);

    void insertDosenNotifikasi(String idPengguna, int idNotif);

    void insertBimbinganNotifikasi(int idNotif, int idBim);

}