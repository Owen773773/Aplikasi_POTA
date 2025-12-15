package com.application.pota.notifikasi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotifikasiService {

    @Autowired
    private NotifikasiRepository notifikasiRepository;

    public Notifikasi getNotifikasiById(int id) {
        return notifikasiRepository.getById(id);
    }

    public List<Notifikasi> getNotifikasiInAppByUsername(String username) {
        List<Notifikasi> listNotif = notifikasiRepository.getNotifikasiLengkapByUsername(username);
        for (Notifikasi iterNotif : listNotif) {
            String notifMuncul = "";
            String tipeNotif = iterNotif.getTipeNotif();
            if (tipeNotif.equalsIgnoreCase("Diterima")) { //jika pengajuan disetujui
                notifMuncul = String.format("Bimbingan Anda disetujui untuk dilaksanakan pada %s, pukul %s - %s, bertempat di %s.",
                        iterNotif.getInfoTanggal(), iterNotif.getInfoWaktuMulai(), iterNotif.getInfoWaktuSelesai(),
                        iterNotif.getInfoRuangan());
            } else if (tipeNotif.equalsIgnoreCase("Menunggu")) { //jika pengajuan masih mengunggu konfirmasi
                notifMuncul = String.format("Pengajuan Bimbingan Anda pada %s, pukul %s - %s, bertempat di %s, dalam proses menunggu persetujuan dosen pembimbing.",
                        iterNotif.getInfoTanggal(), iterNotif.getInfoWaktuMulai(), iterNotif.getInfoWaktuSelesai(),
                        iterNotif.getInfoRuangan());
            } else if (tipeNotif.equalsIgnoreCase("Ditolak")) { //jika pengajuan ditolak
                notifMuncul = String.format("Pengajuan Bimbingan Anda pada %s, pukul %s - %s ditolak dengan alasan '%s'",
                        iterNotif.getInfoTanggal(), iterNotif.getInfoWaktuMulai(), iterNotif.getInfoWaktuSelesai(),
                        iterNotif.getCatatan());
            } else {//jika Dibatalkan
                notifMuncul = String.format("Pengajuan Bimbingan Anda pada %s, pukul %s - %s dibatalkan, dengan alasan '%s'",
                        iterNotif.getInfoTanggal(), iterNotif.getInfoWaktuMulai(), iterNotif.getInfoWaktuSelesai(),
                        iterNotif.getCatatan());
            }
            iterNotif.setPesanDiFrontend(notifMuncul);
        }
        return listNotif;
    }

    public List<Notifikasi> getNotifikasiInAppByIdUser(String idUser) {
        List<Notifikasi> listNotif = notifikasiRepository.getNotifikasiLengkapByIdPengguna(idUser);
        for (Notifikasi iterNotif : listNotif) {
            String notifMuncul = "";
            String tipeNotif = iterNotif.getTipeNotif();
            if (tipeNotif.equalsIgnoreCase("Diterima")) { //jika pengajuan disetujui
                notifMuncul = String.format("Bimbingan Anda disetujui untuk dilaksanakan pada %s, pukul %s - %s, bertempat di %s.",
                        iterNotif.getInfoTanggal(), iterNotif.getInfoWaktuMulai(), iterNotif.getInfoWaktuSelesai(),
                        iterNotif.getInfoRuangan());
            } else if (tipeNotif.equalsIgnoreCase("Menunggu")) { //jika pengajuan masih mengunggu konfirmasi
                notifMuncul = String.format("Pengajuan Bimbingan Anda pada %s, pukul %s - %s, bertempat di %s, dalam proses menunggu persetujuan dosen pembimbing.",
                        iterNotif.getInfoTanggal(), iterNotif.getInfoWaktuMulai(), iterNotif.getInfoWaktuSelesai(),
                        iterNotif.getInfoRuangan());
            } else if (tipeNotif.equalsIgnoreCase("Ditolak")) { //jika pengajuan ditolak
                notifMuncul = String.format("Pengajuan Bimbingan Anda pada %s, pukul %s - %s ditolak dengan alasan '%s'",
                        iterNotif.getInfoTanggal(), iterNotif.getInfoWaktuMulai(), iterNotif.getInfoWaktuSelesai(),
                        iterNotif.getCatatan());
            } else {//jika Dibatalkan
                notifMuncul = String.format("Pengajuan Bimbingan Anda pada %s, pukul %s - %s dibatalkan, dengan alasan '%s'",
                        iterNotif.getInfoTanggal(), iterNotif.getInfoWaktuMulai(), iterNotif.getInfoWaktuSelesai(),
                        iterNotif.getCatatan());
            }
            iterNotif.setPesanDiFrontend(notifMuncul);
        }
        return listNotif;
    }

    public int insertNotifikasi(String tipe) {
        return notifikasiRepository.insertNotifikasi(tipe);
    }

    public void insertMahasiswaNotifikasi(String idMhs, int idNotif) {
        notifikasiRepository.insertMahasiswaNotifikasi(idMhs, idNotif);
    }

    public void insertBimbinganNotifikasi(int idNotif, int idBim) {
        notifikasiRepository.insertBimbinganNotifikasi(idNotif, idBim);
    }

    public void insertDosenNotifikasi(String idDosen, int idNotif) {
        notifikasiRepository.insertDosenNotifikasi(idDosen, idNotif);
    }
}
