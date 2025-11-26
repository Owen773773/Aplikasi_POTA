package com.application.pota.notifikasi;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public interface NotifikasiRepository {
    List<Notifikasi> getNotifikasiLengkapByUsername(String username);
    List<Notifikasi> getNotifikasiLengkapByIdPengguna(String idPengguna);
    void buatNotifikasiBaru(Notifikasi notif, String username);
    // List<Notifikasi> getTanggalBimbinganByUsername(String username);
    // List<Notifikasi> getWaktuMulaiBimbinganByUsername(String username);
    // List<Notifikasi> getWaktuSelesaiBimbinganByUsername(String username);
    // List<Notifikasi> getRuanganByUsername(String username);
    // List<Notifikasi> getWaktuAcaraByUsername(String username);//ini untuk kanan atas yg history
}