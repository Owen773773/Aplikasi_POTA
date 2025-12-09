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
import java.util.ArrayList;
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

    public List<PilihanPengguna> getMahasiswaPilihan(String idPengguna) {
        return bimbinganRepository.getMahasiswaPilihan(idPengguna);
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

        int idNotif = (int)notifikasiService.insertNotifikasi("Menunggu");

        notifikasiService.insertMahasiswaNotifikasi(idMahasiswa, idNotif);
        notifikasiService.insertBimbinganNotifikasi(idNotif, idBim);

        for (String dosen : dosenList) {
            notifikasiService.insertDosenNotifikasi(dosen, idNotif);
        }
    }

    public void ajukanBimbinganDosen(String idPengguna, List<String> ListMahasiswa, String topik,
                                     String deskripsi,
                                     LocalDate tanggal,
                                     LocalTime waktuMulai,
                                     LocalTime waktuSelesai) {
        // Dapatkan ID semua tugas akhir
        List<Integer> idTA = new ArrayList<>();
        for(String idMahasiswa : ListMahasiswa) {
            idTA.add(tugasAkhirService.getIdTugasAkhir(idMahasiswa));
        }


        //Insert Jadwal → ambil IdJadwal
        int idJadwal = jadwalService.insertJadwal(tanggal, waktuMulai, waktuSelesai);

        // Insert ke Jadwal_Bimbingan
        bimbinganRepository.insertJadwalBimbingan(idJadwal);

        //Insert Bimbingan → ambil IdBim
        int idBim = bimbinganRepository.insertBimbingan(deskripsi.isEmpty()?"-":deskripsi, topik, 1, null);

        //Link Jadwal—Bimbingan
        bimbinganRepository.insertPenjadwalanBimbingan(idJadwal, idBim);

        for (int idTa : idTA) {
            bimbinganRepository.insertTopikBimbingan(
                    idBim,
                    idTa,
                    "Menunggu",  // StatusMhs
                    "Menyetujui",    // StatusDosen1
                    null,  // StatusDosen2
                    "Proses"       // StatusBimbingan
            );
        }


        // Insert Notifikasi
        int idNotif = (int) notifikasiService.insertNotifikasi("Menunggu");

        // Kirim notifikasi ke semua mahasiswa
        for (String idMahasiswa : ListMahasiswa) {
            notifikasiService.insertMahasiswaNotifikasi(idMahasiswa, idNotif);
        }

        // Link notifikasi dengan bimbingan
        notifikasiService.insertBimbinganNotifikasi(idNotif, idBim);

        // Kirim notifikasi ke dosen pembimbing
        notifikasiService.insertDosenNotifikasi(idPengguna, idNotif);
    }


    public void validasiBimbingan(int idBim, String peran, String catatan) {

        switch (peran.toLowerCase()) {
            case "dosen1":
                bimbinganRepository.updateStatusDosen1(idBim, "Tervalidasi");
                break;

            case "dosen2":
                bimbinganRepository.updateStatusDosen2(idBim, "Tervalidasi");
                break;

            case "mahasiswa":
                // Mahasiswa memvalidasi → bimbingan dianggap selesai
                bimbinganRepository.updateStatusMahasiswa(idBim, "Tervalidasi");
                bimbinganRepository.updateCatatanBimbingan(idBim,  catatan);
                break;

            default:
                throw new IllegalArgumentException("Peran tidak valid: " + peran);
        }
    }
    public String getPeranDalamTA(String idPengguna, int idTa) {
        
        String idMahasiswa = tugasAkhirService.getIdMahasiswaByIdTa(idTa);
        if (idMahasiswa != null && idMahasiswa.equalsIgnoreCase(idPengguna)) {
            return "mahasiswa";
        }

        List<PilihanPengguna> listDosen = bimbinganRepository.getDosenPembimbingPilihan(idTa);

        if (idPengguna.equals(listDosen.get(0).getIdPengguna()))return "dosen1";
        if (listDosen.size() >= 2 && idPengguna.equals(listDosen.get(1).getIdPengguna()))return "dosen2";

        return "unknown";
    }

    public void batalkanBimbingan(int idBim, String peran, String catatan) {
        bimbinganRepository.updateCatatanBimbingan(idBim, catatan);
        bimbinganRepository.updateStatusBimbingan(idBim, "Gagal");
        switch (peran.toLowerCase()) {
            case "dosen1":
                bimbinganRepository.updateStatusDosen1(idBim, "Dibatalkan");
                break;

            case "dosen2":
                bimbinganRepository.updateStatusDosen2(idBim, "Dibatalkan");
                break;

            case "mahasiswa":
                bimbinganRepository.updateStatusMahasiswa(idBim, "Dibatalkan");
                break;

            default:
                throw new IllegalArgumentException("Peran tidak valid: " + peran);
        }
    }

    public void terimaBimbingan(int idBim, String peran) {
        switch (peran.toLowerCase()) {
            case "dosen1":
                bimbinganRepository.updateStatusDosen1(idBim, "Menyetujui");
                break;

            case "dosen2":
                bimbinganRepository.updateStatusDosen2(idBim, "Menyetujui");
                break;

            case "mahasiswa":
                bimbinganRepository.updateStatusMahasiswa(idBim, "Menyetujui");
                break;

            default:
                throw new IllegalArgumentException("Peran tidak valid: " + peran);
        }
    }

    public void tolakBimbingan(int idBim, String peran, String catatan) {
        bimbinganRepository.updateCatatanBimbingan(idBim, catatan);
        bimbinganRepository.updateStatusBimbingan(idBim, "Gagal");
        switch (peran.toLowerCase()) {
            case "dosen1":
                bimbinganRepository.updateStatusDosen1(idBim, "Menolak");
                break;

            case "dosen2":
                bimbinganRepository.updateStatusDosen2(idBim, "Menolak");
                break;

            case "mahasiswa":
                bimbinganRepository.updateStatusMahasiswa(idBim, "Menolak");
                break;

            default:
                throw new IllegalArgumentException("Peran tidak valid: " + peran);
        }
    }

    // Tambahkan di BimbinganService.java

    public BimbinganDetailStatus getDetailStatusBimbingan(int idBim, String idPengguna) {
        BimbinganDetailStatus status = bimbinganRepository.getDetailStatusBimbingan(idBim);

        // Tentukan peran user
        List<Integer> idTaList = bimbinganRepository.getIdTaByIdBim(idBim);
        String peran = "unknown";

        for (int idTa : idTaList) {
            String peranCheck = getPeranDalamTA(idPengguna, idTa);
            if (!peranCheck.equals("unknown")) {
                peran = peranCheck;
                break;
            }
        }

        status.setPeranPengguna(peran);
        status.setIdPengguna(idPengguna);

        return status;
    }

    public boolean bisaTerima(int idBim, String idPengguna) {
        BimbinganDetailStatus status = getDetailStatusBimbingan(idBim, idPengguna);
        String peran = status.getPeranPengguna();

        switch (peran) {
            case "mahasiswa":
                return "Menunggu".equals(status.getStatusMhs());
            case "dosen1":
                return "Menunggu".equals(status.getStatusDosen1());
            case "dosen2":
                return status.getStatusDosen2() != null && "Menunggu".equals(status.getStatusDosen2());
            default:
                return false;
        }
    }

    public boolean bisaValidasi(int idBim, String idPengguna) {
        BimbinganDetailStatus status = getDetailStatusBimbingan(idBim, idPengguna);
        String peran = status.getPeranPengguna();

        if ("mahasiswa".equals(peran)) {
            // Mahasiswa bisa validasi jika minimal salah satu dosen sudah validasi
            boolean dosen1Valid = "Tervalidasi".equals(status.getStatusDosen1());
            boolean dosen2Valid = status.getStatusDosen2() == null || "Tervalidasi".equals(status.getStatusDosen2());
            return (dosen1Valid || dosen2Valid) && "Menyetujui".equals(status.getStatusMhs());
        } else if ("dosen1".equals(peran)) {
            return "Menyetujui".equals(status.getStatusDosen1());
        } else if ("dosen2".equals(peran)) {
            return status.getStatusDosen2() != null && "Menyetujui".equals(status.getStatusDosen2());
        }

        return false;
    }

    public boolean bisaTolak(int idBim, String idPengguna) {
        return bisaTerima(idBim, idPengguna); // Sama dengan terima
    }

    public boolean bisaBatalkan(int idBim, String idPengguna) {
        BimbinganDetailStatus status = getDetailStatusBimbingan(idBim, idPengguna);
        String peran = status.getPeranPengguna();

        // Bisa batalkan jika sudah menyetujui atau terjadwalkan
        switch (peran) {
            case "mahasiswa":
                return "Menyetujui".equals(status.getStatusMhs()) || "Terjadwalkan".equals(status.getStatusBimbingan());
            case "dosen1":
                return "Menyetujui".equals(status.getStatusDosen1()) || "Terjadwalkan".equals(status.getStatusBimbingan());
            case "dosen2":
                return status.getStatusDosen2() != null &&
                        ("Menyetujui".equals(status.getStatusDosen2()) || "Terjadwalkan".equals(status.getStatusBimbingan()));
            default:
                return false;
        }
    }
}
