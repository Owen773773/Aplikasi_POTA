package com.application.pota.bimbingan;

import com.application.pota.dosen.DosenService;
import com.application.pota.jadwal.JadwalService;
import com.application.pota.notifikasi.NotifikasiService;
import com.application.pota.pengguna.PenggunaService;
import com.application.pota.tugasakhir.TugasAkhirService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BimbinganService {
    private final BimbinganRepository bimbinganRepository;
    @Autowired
    private PenggunaService penggunaService;

    @Autowired
    private JadwalService jadwalService;

    @Autowired
    private TugasAkhirService tugasAkhirService;

    @Autowired
    private DosenService dosenService;

    @Autowired
    private NotifikasiService notifikasiService;

    /**
     * Mendapatkan list bimbingan sesuai tipe status, tipe akun, dan id pengguna
     *
     * @param tipeAkun   Tipe akun pengguna (Mahasiswa/Dosen) [New Param]
     * @param tipeStatus Status bimbingan yang ingin diambil (Terjadwalkan, Selesai, Gagal, Proses)
     * @param idPengguna ID dari pengguna (mahasiswa atau dosen)
     * @return List BimbinganSiapKirim yang siap dikirim ke view
     */
    public List<BimbinganSiapKirim> dapatkanBimbingan(String tipeAkun, String tipeStatus, String idPengguna) {
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

    public List<PilihanPengguna> getDosenPembimbingPilihan(int idTa) {
        return bimbinganRepository.getDosenPembimbingPilihan(idTa);
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

    public void ajukanBimbinganMahasiswa(
            String idMahasiswa,
            List<String> dosenList,
            String topik,
            String deskripsi,
            LocalDate tanggal,
            LocalTime waktuMulai,
            LocalTime waktuSelesai
    ) {

        // 1. Dapatkan ID Tugas Akhir mahasiswa
        int idTA = tugasAkhirService.getIdTugasAkhir(idMahasiswa);


        // 3. Insert Jadwal → ambil IdJadwal
        int idJadwal = jadwalService.insertJadwal(tanggal, waktuMulai, waktuSelesai);

        // 4. Insert ke Jadwal_Bimbingan (PENTING!)
        bimbinganRepository.insertJadwalBimbingan(idJadwal);

        // 5. Insert Bimbingan → ambil IdBim
        int idBim = bimbinganRepository.insertBimbingan(deskripsi.isEmpty()?"-":deskripsi, topik, 1, null);

        // 6. Link Jadwal—Bimbingan
        bimbinganRepository.insertPenjadwalanBimbingan(idJadwal, idBim);

        bimbinganRepository.insertTopikBimbingan(
                idBim,
                idTA,
                "Menyetujui",  // StatusMhs
                "Menunggu",    // StatusDosen1
                dosenList.size() > 1 ? "Menunggu" : null,  // StatusDosen2
                "Proses"       // StatusBimbingan
        );

        int idNotif = (int)notifikasiService.insertNotifikasi("Mengajukan");

        notifikasiService.insertMahasiswaNotifikasi(idMahasiswa, idNotif);
        notifikasiService.insertBimbinganNotifikasi(idNotif, idBim);

        for (String dosen : dosenList) {
            notifikasiService.insertDosenNotifikasi(dosen, idNotif);
        }
    }

}
