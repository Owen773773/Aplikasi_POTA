package com.application.pota.bimbingan;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.application.pota.dosen.DosenService;
import com.application.pota.jadwal.JadwalService;
import com.application.pota.notifikasi.NotifikasiService;
import com.application.pota.pengguna.PenggunaService;
import com.application.pota.tugasakhir.TugasAkhirService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BimbinganService {
    @Autowired
    private final BimbinganRepository bimbinganRepository;
    private PenggunaService penggunaService;
    private JadwalService jadwalService;
    private TugasAkhirService tugasAkhirService;
    private DosenService dosenService;
    private NotifikasiService notifikasiService;

    /**
     * Mendapatkan list bimbingan sesuai tipe status, tipe akun, dan id pengguna
     * @param tipeAkun Tipe akun pengguna (Mahasiswa/Dosen) [New Param]
     * @param tipeStatus Status bimbingan yang ingin diambil (Terjadwalkan, Selesai, Gagal, Proses)
     * @param idPengguna ID dari pengguna (mahasiswa atau dosen)
     * @return List BimbinganSiapKirim yang siap dikirim ke view
     */
    public List<BimbinganSiapKirim> dapatkanBimbingan(String tipeAkun, String tipeStatus, String idPengguna){
        // Mengirim tipeAkun ke repository
        return bimbinganRepository.getBimbinganUserBertipe(tipeAkun, tipeStatus, idPengguna);
    }

    /**
     * Mendapatkan bimbingan yang terjadwalkan
     * (Asumsi tipeAkun diambil dari session di Controller, lalu diteruskan)
     */
    public List<BimbinganSiapKirim> dapatkanBimbinganTerjadwal(String tipeAkun, String idPengguna) {
        return dapatkanBimbingan(tipeAkun, "Terjadwalkan", idPengguna);
    }

    /**
     * Mendapatkan bimbingan yang selesai
     */
    public List<BimbinganSiapKirim> dapatkanBimbinganSelesai(String tipeAkun, String idPengguna) {
        return dapatkanBimbingan(tipeAkun, "Selesai", idPengguna);
    }

    /**
     * Mendapatkan bimbingan yang gagal
     */
    public List<BimbinganSiapKirim> dapatkanBimbinganGagal(String tipeAkun, String idPengguna) {
        return dapatkanBimbingan(tipeAkun, "Gagal", idPengguna);
    }

    public List<BimbinganSiapKirim> dapatkanBimbinganProses(String tipeAkun, String idPengguna) {
        return dapatkanBimbingan(tipeAkun, "Proses", idPengguna);
    }
    
    public Bimbingan getJadwalTerdekat(String idMahasiswa) {
        Bimbingan hasil = bimbinganRepository.bimbinganTerdekat(idMahasiswa);
        if (hasil == null) {
            Bimbingan kosong = new Bimbingan();
            kosong.setPesan("Tidak ada jadwal bimbingan terdekat.");
            kosong.setTopikBim("-");
            kosong.setNamaDosen("-");
            kosong.setTanggal("-");
            kosong.setJam("-");
            kosong.setNamaRuangan("-");
            return kosong;
        }

        return hasil;
    }
}
