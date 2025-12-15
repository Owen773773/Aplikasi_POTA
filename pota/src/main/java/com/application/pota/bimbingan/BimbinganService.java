package com.application.pota.bimbingan;

import com.application.pota.dosen.DosenService;
import com.application.pota.jadwal.JadwalService;
import com.application.pota.mahasiswa.MahasiswaService;
import com.application.pota.notifikasi.NotifikasiService;
import com.application.pota.pengguna.PenggunaService;
import com.application.pota.tugasakhir.TugasAkhirService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private MahasiswaService mahasiswaService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BimbinganSiapKirim> dapatkanBimbingan(String tipeAkun, String tipeStatus, String idPengguna) {
        // Mengirim tipeAkun ke repository
        return bimbinganRepository.getBimbinganUserBertipe(tipeAkun, tipeStatus, idPengguna);
    }


    public List<BimbinganSiapKirim> dapatkanBimbinganTerjadwal(String tipeAkun, String idPengguna) {
        return dapatkanBimbingan(tipeAkun, "Terjadwalkan", idPengguna);
    }

    public List<PilihanPengguna> getDosenPembimbingPilihan(int idTa) {
        return bimbinganRepository.getDosenPembimbingPilihan(idTa);
    }

    public List<PilihanPengguna> getMahasiswaPilihan(String idPengguna) {
        return bimbinganRepository.getMahasiswaPilihan(idPengguna);
    }


    public List<BimbinganSiapKirim> dapatkanBimbinganSelesai(String tipeAkun, String idPengguna) {
        return dapatkanBimbingan(tipeAkun, "Selesai", idPengguna);
    }


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

        int idTA = tugasAkhirService.getIdTugasAkhir(idMahasiswa);

        List<PilihanPengguna> dosenPembimbingResmi = bimbinganRepository.getDosenPembimbingPilihan(idTA);

        String idDosen1Resmi = dosenPembimbingResmi.get(0).getIdPengguna();
        String idDosen2Resmi = dosenPembimbingResmi.size() > 1 ? dosenPembimbingResmi.get(1).getIdPengguna() : null;

        int idJadwal = jadwalService.insertJadwal(tanggal, waktuMulai, waktuSelesai);
        bimbinganRepository.insertJadwalBimbingan(idJadwal);

        int idBim = bimbinganRepository.insertBimbingan(deskripsi.isEmpty() ? "-" : deskripsi, topik, 1, null);
        bimbinganRepository.insertPenjadwalanBimbingan(idJadwal, idBim);

        String statusDosen1;
        String statusDosen2;

        boolean pilihDosen1 = dosenList.contains(idDosen1Resmi);
        boolean pilihDosen2 = idDosen2Resmi != null && dosenList.contains(idDosen2Resmi);

        if (pilihDosen1 && pilihDosen2) {
            statusDosen1 = "Menunggu";
            statusDosen2 = "Menunggu";
        } else if (pilihDosen1) {
            // Hanya pilih dosen 1
            statusDosen1 = "Menunggu";
            statusDosen2 = idDosen2Resmi != null ? "Tidak Terpilih" : null;
        } else if (pilihDosen2) {
            // Hanya pilih dosen 2
            statusDosen1 = "Tidak Terpilih";
            statusDosen2 = "Menunggu";
        } else {
            // Default jika tidak ada yang cocok
            statusDosen1 = "Menunggu";
            statusDosen2 = idDosen2Resmi != null ? "Menunggu" : null;
        }

        bimbinganRepository.insertTopikBimbingan(
                idBim,
                idTA,
                "Menyetujui",      // StatusMhs
                statusDosen1,      // StatusDosen1
                statusDosen2,      // StatusDosen2
                "Proses"           // StatusBimbingan
        );

        int idNotif = (int) notifikasiService.insertNotifikasi("Menunggu");
        notifikasiService.insertMahasiswaNotifikasi(idMahasiswa, idNotif);
        notifikasiService.insertBimbinganNotifikasi(idNotif, idBim);

        for (String dosen : dosenList) {
            if (dosen.equals(idDosen1Resmi) || (idDosen2Resmi != null && dosen.equals(idDosen2Resmi))) {
                notifikasiService.insertDosenNotifikasi(dosen, idNotif);
            }
        }
    }

    public void ajukanBimbinganDosen(
            String idPengguna,
            List<String> listMahasiswa,
            String topik,
            String deskripsi,
            LocalDate tanggalMulai,
            LocalTime waktuMulai,
            LocalTime waktuSelesai,
            Integer idRuangan,
            Integer tiapBerapaMinggu   // interval antar bimbingan (0 = hanya 1x)
    ) {

        if (tiapBerapaMinggu == null || tiapBerapaMinggu < 0) {
            tiapBerapaMinggu = 0;
        }

        // Simpan ID TA dan tanggal UTS/UAS untuk semua mahasiswa
        Map<String, Integer> idTAmap = new HashMap<>();
        Map<String, LocalDate> utsMap = new HashMap<>();
        Map<String, LocalDate> uasMap = new HashMap<>();

        for (String idMhs : listMahasiswa) {
            int idTA = tugasAkhirService.getIdTugasAkhir(idMhs);
            LocalDate uts = tugasAkhirService.getTanggalUtsByIdMahasiswa(idMhs);
            LocalDate uas = tugasAkhirService.getTanggalUasByIdMahasiswa(idMhs);

            idTAmap.put(idMhs, idTA);
            utsMap.put(idMhs, uts);
            uasMap.put(idMhs, uas);
        }

        List<LocalDate> daftarTanggal = new ArrayList<>();

        // CASE 1: Jika interval = 0 → hanya 1x
        if (tiapBerapaMinggu == 0) {
            daftarTanggal.add(tanggalMulai);
        }
        // CASE 2: Jika interval > 0 → generate sampai batas UAS
        else {
            LocalDate current = tanggalMulai;

            // batas maksimal iterasi: sampai salah satu mahasiswa melewati UAS
            while (true) {

                boolean validUntukSemua = true;
                boolean sudahLewatUAS = false;

                for (String idMhs : listMahasiswa) {
                    LocalDate uts = utsMap.get(idMhs);
                    LocalDate uas = uasMap.get(idMhs);

                    // stop total jika sudah lewat atau sama dengan uas
                    if (uas != null && !current.isBefore(uas)) {
                        sudahLewatUAS = true;
                        break;
                    }

                    // skip jika kena UTS
                    if (uts != null && current.isEqual(uts)) {
                        validUntukSemua = false;
                    }
                }

                if (sudahLewatUAS) break;
                if (validUntukSemua) daftarTanggal.add(current);

                current = current.plusWeeks(tiapBerapaMinggu);
            }
        }

        // Eksekusi INSERT untuk setiap tanggal valid
        for (LocalDate tglBim : daftarTanggal) {

            int idJadwal = jadwalService.insertJadwal(tglBim, waktuMulai, waktuSelesai);
            bimbinganRepository.insertJadwalBimbingan(idJadwal);

            int idBim = bimbinganRepository.insertBimbingan(
                    deskripsi.isEmpty() ? "-" : deskripsi,
                    topik,
                    listMahasiswa.size(),
                    idRuangan
            );

            bimbinganRepository.insertPenjadwalanBimbingan(idJadwal, idBim);

            // Insert status mahasiswa & dosen
            for (String idMhs : listMahasiswa) {
                bimbinganRepository.insertTopikBimbingan(
                        idBim,
                        idTAmap.get(idMhs),
                        "Menunggu",
                        "Menyetujui",
                        null,
                        "Proses"
                );
            }

            // Notifikasi
            int idNotif = (int) notifikasiService.insertNotifikasi("Menunggu");

            for (String idMhs : listMahasiswa) {
                notifikasiService.insertMahasiswaNotifikasi(idMhs, idNotif);
            }
            notifikasiService.insertBimbinganNotifikasi(idNotif, idBim);
            notifikasiService.insertDosenNotifikasi(idPengguna, idNotif);
        }
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
                // Mahasiswa memvalidasi
                bimbinganRepository.updateStatusMahasiswa(idBim, "Tervalidasi");
                bimbinganRepository.updateCatatanBimbingan(idBim, catatan);
                break;

            default:
                throw new IllegalArgumentException("Peran tidak valid: " + peran);
        }

        // CEK apakah semua sudah validasi
        if (isSemuaValidasi(idBim)) {
            // Update status bimbingan ke Selesai
            bimbinganRepository.updateStatusBimbingan(idBim, "Selesai");
            System.out.println("✅ Semua sudah validasi! Status diupdate ke Selesai");
        }
    }


    public String getPeranDalamTA(String idPengguna, int idTa) {

        String idMahasiswa = tugasAkhirService.getIdMahasiswaByIdTa(idTa);
        if (idMahasiswa != null && idMahasiswa.equalsIgnoreCase(idPengguna)) {
            return "mahasiswa";
        }

        List<PilihanPengguna> listDosen = bimbinganRepository.getDosenPembimbingPilihan(idTa);

        if (idPengguna.equals(listDosen.get(0).getIdPengguna())) return "dosen1";
        if (listDosen.size() >= 2 && idPengguna.equals(listDosen.get(1).getIdPengguna())) return "dosen2";

        return "unknown";
    }

    public String getIdMahasiswaByUsername(String username) {
        String sql = """
                    SELECT m.IdPengguna
                    FROM Mahasiswa m
                    JOIN Pengguna p ON m.IdPengguna = p.IdPengguna
                    WHERE p.username = ?
                """;

        return jdbcTemplate.queryForObject(sql, String.class, username);
    }

    public String getIdDosenByUsername(String username) {
        String sql = """
                    SELECT d.IdPengguna
                    FROM dosen d
                    JOIN Pengguna p ON d.IdPengguna = p.IdPengguna
                    WHERE p.username = ?
                """;

        return jdbcTemplate.queryForObject(sql, String.class, username);
    }

    public void batalkanBimbingan(int idBim, String peran, String catatan) {
        bimbinganRepository.updateCatatanBimbingan(idBim, catatan);
        bimbinganRepository.updateStatusBimbingan(idBim, "Gagal");

        List<String> dosenList = bimbinganRepository.getListDosenByIdBim(idBim);
        int idNotifikasi = notifikasiService.insertNotifikasi("Ditolak");
        List<String> listMahasiswa = bimbinganRepository.getIdMahasiswaBimbingan(idBim);

        switch (peran.toLowerCase()) {
            case "dosen1":
                bimbinganRepository.updateStatusDosen1(idBim, "Dibatalkan");
                for (String mhs : listMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(getIdMahasiswaByUsername(mhs), idNotifikasi);
                }
                break;

            case "dosen2":
                bimbinganRepository.updateStatusDosen2(idBim, "Dibatalkan");
                for (String mhs : listMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(getIdMahasiswaByUsername(mhs), idNotifikasi);
                }
                break;

            case "mahasiswa":
                bimbinganRepository.updateStatusMahasiswa(idBim, "Dibatalkan");
                notifikasiService.insertDosenNotifikasi(getIdDosenByUsername(dosenList.get(0)), idNotifikasi);
                if (dosenList.size() > 1) {
                    notifikasiService.insertDosenNotifikasi(getIdDosenByUsername(dosenList.get(1)), idNotifikasi);
                }
                break;

            default:
                throw new IllegalArgumentException("Peran tidak valid: " + peran);
        }
    }

    private boolean cekSemuaMahasiswaSetuju(int idBim) {
        String sql = """
                    SELECT COUNT(*) as total,
                           SUM(CASE WHEN StatusMhs = 'Menyetujui' THEN 1 ELSE 0 END) as setuju
                    FROM TopikBimbingan
                    WHERE IdBim = ?
                """;

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, idBim);
        int total = ((Number) result.get("total")).intValue();
        int setuju = ((Number) result.get("setuju")).intValue();

        return total > 0 && total == setuju;
    }

    public void terimaBimbingan(int idBim, String peran, Integer idRuangan) {
        List<String> dosenList = bimbinganRepository.getListDosenByIdBim(idBim);
        int notifikasi = notifikasiService.insertNotifikasi("Menerima");
        List<String> listIdMahasiswa = bimbinganRepository.getIdMahasiswaBimbingan(idBim);

        // Update ruangan jika idRuangan tidak null
        if (idRuangan != null) {
            bimbinganRepository.updateRuanganBimbingan(idBim, idRuangan);
        }

        switch (peran.toLowerCase()) {
            case "dosen1":
                bimbinganRepository.updateStatusDosen1(idBim, "Menyetujui");
                for (String idMhs : listIdMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(idMhs, notifikasi);
                }
                break;

            case "dosen2":
                bimbinganRepository.updateStatusDosen2(idBim, "Menyetujui");
                for (String idMhs : listIdMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(idMhs, notifikasi);
                }
                break;

            case "mahasiswa":
                bimbinganRepository.updateStatusMahasiswa(idBim, "Menyetujui");
                notifikasiService.insertDosenNotifikasi(dosenList.get(0), notifikasi);
                if (dosenList.size() > 1) {
                    notifikasiService.insertDosenNotifikasi(dosenList.get(1), notifikasi);
                }
                break;

            default:
                throw new IllegalArgumentException("Peran tidak valid: " + peran);
        }

        // CEK ULANG status setelah update
        BimbinganDetailStatus temp = bimbinganRepository.getDetailStatusBimbingan(idBim);

        boolean dosen1Setuju = "Menyetujui".equals(temp.getStatusDosen1());
        boolean dosen2SetujuAtauTidakTerpilih =
                temp.getStatusDosen2() == null ||
                        "Menyetujui".equals(temp.getStatusDosen2()) ||
                        "Tidak Terpilih".equals(temp.getStatusDosen2());

        // Cek juga status mahasiswa - SEMUA mahasiswa harus setuju
        boolean semuaMahasiswaSetuju = cekSemuaMahasiswaSetuju(idBim);

        System.out.println("=== DEBUG STATUS ===");
        System.out.println("IdBim: " + idBim);
        System.out.println("StatusDosen1: " + temp.getStatusDosen1());
        System.out.println("StatusDosen2: " + temp.getStatusDosen2());
        System.out.println("StatusMhs: " + temp.getStatusMhs());
        System.out.println("Dosen1 Setuju: " + dosen1Setuju);
        System.out.println("Dosen2 OK: " + dosen2SetujuAtauTidakTerpilih);
        System.out.println("Semua Mhs Setuju: " + semuaMahasiswaSetuju);
        System.out.println("==================");

        // Update ke Terjadwalkan jika SEMUA kondisi terpenuhi
        if (dosen1Setuju && dosen2SetujuAtauTidakTerpilih && semuaMahasiswaSetuju) {
            bimbinganRepository.updateStatusBimbingan(idBim, "Terjadwalkan");
            System.out.println("✅ Status diupdate ke Terjadwalkan");
        } else {
            System.out.println("❌ Belum bisa update ke Terjadwalkan, ada yang belum setuju");
        }
    }

    public void tolakBimbingan(int idBim, String peran, String catatan) {
        bimbinganRepository.updateCatatanBimbingan(idBim, catatan);
        bimbinganRepository.updateStatusBimbingan(idBim, "Gagal");
        List<String> dosenList = bimbinganRepository.getListDosenByIdBim(idBim);
        int idNotifikasi = notifikasiService.insertNotifikasi("Ditolak");
        List<String> listMahasiswa = bimbinganRepository.getIdMahasiswaBimbingan(idBim);

        switch (peran.toLowerCase()) {
            case "dosen1":
                bimbinganRepository.updateStatusDosen1(idBim, "Menolak");
                for (String mhs : listMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(getIdMahasiswaByUsername(mhs), idNotifikasi);
                }
                if (dosenList.size() > 1) {
                    notifikasiService.insertDosenNotifikasi(getIdDosenByUsername(dosenList.get(1)), idNotifikasi);

                }

                break;

            case "dosen2":
                bimbinganRepository.updateStatusDosen2(idBim, "Menolak");
                for (String mhs : listMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(getIdMahasiswaByUsername(mhs), idNotifikasi);
                }
                notifikasiService.insertDosenNotifikasi(getIdDosenByUsername(dosenList.get(0)), idNotifikasi);

                break;

            case "mahasiswa":
                bimbinganRepository.updateStatusMahasiswa(idBim, "Menolak");
                notifikasiService.insertDosenNotifikasi(getIdDosenByUsername(dosenList.get(0)), idNotifikasi);
                if (dosenList.size() > 1) {
                    notifikasiService.insertDosenNotifikasi(getIdDosenByUsername(dosenList.get(1)), idNotifikasi);
                }
                break;

            default:
                throw new IllegalArgumentException("Peran tidak valid: " + peran);
        }
    }


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
                return "Menunggu".equals(status.getStatusMhs())
                        && !"Terjadwalkan".equals(status.getStatusBimbingan());
            case "dosen1":
                // Cek apakah status bukan "Tidak Terpilih"
                return "Menunggu".equals(status.getStatusDosen1()) &&
                        !"Tidak Terpilih".equals(status.getStatusDosen1());
            case "dosen2":
                return status.getStatusDosen2() != null &&
                        "Menunggu".equals(status.getStatusDosen2()) &&
                        !"Tidak Terpilih".equals(status.getStatusDosen2());
            default:
                return false;
        }
    }

    public boolean isSemuaValidasi(int idBim) {
        String sql = """
        SELECT 
            StatusMhs,
            StatusDosen1,
            StatusDosen2,
            StatusBimbingan
        FROM TopikBimbingan
        WHERE IdBim = ?
        LIMIT 1
    """;

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, idBim);

        String statusMhs = (String) result.get("StatusMhs");
        String statusDosen1 = (String) result.get("StatusDosen1");
        String statusDosen2 = (String) result.get("StatusDosen2");
        String statusBimbingan = (String) result.get("StatusBimbingan");

        // Cek apakah status bimbingan Terjadwalkan
        if (!"Terjadwalkan".equals(statusBimbingan)) {
            return false;
        }

        // Cek validasi dosen
        boolean dosen1Valid = "Tervalidasi".equals(statusDosen1);
        boolean dosen2Valid = statusDosen2 == null ||
                "Tervalidasi".equals(statusDosen2) ||
                "Tidak Terpilih".equals(statusDosen2);

        // Cek validasi mahasiswa
        boolean mhsValid = "Tervalidasi".equals(statusMhs);

        // Semua harus valid
        return dosen1Valid && dosen2Valid && mhsValid;
    }

    public boolean bisaValidasi(int idBim, String idPengguna) {
        BimbinganDetailStatus status = getDetailStatusBimbingan(idBim, idPengguna);
        String peran = status.getPeranPengguna();

        // Cek apakah bimbingan sudah Terjadwalkan
        if (!"Terjadwalkan".equals(status.getStatusBimbingan())) {
            return false; // Hanya bisa validasi jika status Terjadwalkan
        }

        switch (peran.toLowerCase()) {
            case "mahasiswa":
                // Mahasiswa bisa validasi jika:
                // 1. Statusnya Menyetujui (sudah accept)
                // 2. Minimal 1 dosen sudah validasi
                boolean mahasiswaSudahSetuju = "Menyetujui".equals(status.getStatusMhs());
                boolean dosen1Valid = "Tervalidasi".equals(status.getStatusDosen1());
                boolean dosen2Valid = status.getStatusDosen2() == null ||
                        "Tervalidasi".equals(status.getStatusDosen2()) ||
                        "Tidak Terpilih".equals(status.getStatusDosen2());

                // Minimal 1 dosen harus sudah validasi
                boolean minimalSatuDosenValidasi = dosen1Valid ||
                        (status.getStatusDosen2() != null && "Tervalidasi".equals(status.getStatusDosen2()));

                return mahasiswaSudahSetuju && minimalSatuDosenValidasi;

            case "dosen1":
                // Dosen1 bisa validasi jika:
                // 1. Statusnya Menyetujui (sudah accept)
                // 2. Bukan "Tidak Terpilih"
                return "Menyetujui".equals(status.getStatusDosen1()) &&
                        !"Tidak Terpilih".equals(status.getStatusDosen1());

            case "dosen2":
                // Dosen2 bisa validasi jika:
                // 1. Status bukan null
                // 2. Statusnya Menyetujui (sudah accept)
                // 3. Bukan "Tidak Terpilih"
                return status.getStatusDosen2() != null &&
                        "Menyetujui".equals(status.getStatusDosen2()) &&
                        !"Tidak Terpilih".equals(status.getStatusDosen2());

            default:
                return false;
        }
    }


    public boolean bisaTolak(int idBim, String idPengguna) {
        return bisaTerima(idBim, idPengguna);
    }

    public boolean bisaBatalkan(int idBim, String idPengguna) {
        BimbinganDetailStatus status = getDetailStatusBimbingan(idBim, idPengguna);
        String peran = status.getPeranPengguna();

        switch (peran) {
            case "mahasiswa":
                return "Menyetujui".equals(status.getStatusMhs()) ||
                        "Terjadwalkan".equals(status.getStatusBimbingan());
            case "dosen1":
                return ("Menyetujui".equals(status.getStatusDosen1()) ||
                        "Terjadwalkan".equals(status.getStatusBimbingan())) &&
                        !"Tidak Terpilih".equals(status.getStatusDosen1());
            case "dosen2":
                return status.getStatusDosen2() != null &&
                        ("Menyetujui".equals(status.getStatusDosen2()) ||
                                "Terjadwalkan".equals(status.getStatusBimbingan())) &&
                        !"Tidak Terpilih".equals(status.getStatusDosen2());
            default:
                return false;
        }
    }
}
