package com.application.pota.bimbingan;

import com.application.pota.dosen.DosenService;
import com.application.pota.jadwal.JadwalService;
import com.application.pota.mahasiswa.MahasiswaService;
import com.application.pota.notifikasi.NotifikasiService;
import com.application.pota.pengguna.PenggunaService;
import com.application.pota.tugasakhir.TugasAkhirService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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


        int idJadwal = jadwalService.insertJadwal(tanggal, waktuMulai, waktuSelesai);

        bimbinganRepository.insertJadwalBimbingan(idJadwal);

        int idBim = bimbinganRepository.insertBimbingan(deskripsi.isEmpty() ? "-" : deskripsi, topik, 1, null);

        bimbinganRepository.insertPenjadwalanBimbingan(idJadwal, idBim);

        bimbinganRepository.insertTopikBimbingan(
                idBim,
                idTA,
                "Menyetujui",  // StatusMhs
                "Menunggu",    // StatusDosen1
                dosenList.size() > 1 ? "Menunggu" : null,  // StatusDosen2
                "Proses"       // StatusBimbingan
        );

        int idNotif = (int) notifikasiService.insertNotifikasi("Menunggu");

        notifikasiService.insertMahasiswaNotifikasi(idMahasiswa, idNotif);
        notifikasiService.insertBimbinganNotifikasi(idNotif, idBim);

        for (String dosen : dosenList) {
            notifikasiService.insertDosenNotifikasi(dosen, idNotif);
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
                // Mahasiswa memvalidasi → bimbingan dianggap selesai
                bimbinganRepository.updateStatusMahasiswa(idBim, "Tervalidasi");
                bimbinganRepository.updateCatatanBimbingan(idBim, catatan);
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

        if (idPengguna.equals(listDosen.get(0).getIdPengguna())) return "dosen1";
        if (listDosen.size() >= 2 && idPengguna.equals(listDosen.get(1).getIdPengguna())) return "dosen2";

        return "unknown";
    }

    public void batalkanBimbingan(int idBim, String peran, String catatan) {
        bimbinganRepository.updateCatatanBimbingan(idBim, catatan);
        bimbinganRepository.updateStatusBimbingan(idBim, "Gagal");

        List<String> dosenList = bimbinganRepository.getListDosenByIdBim(idBim);
        int idNotifikasi = notifikasiService.insertNotifikasi("Ditolak");
        List<String> listMahasiswa = bimbinganRepository.getMahasiswaBimbingan(idBim);

        switch (peran.toLowerCase()) {
            case "dosen1":
                bimbinganRepository.updateStatusDosen1(idBim, "Dibatalkan");
                for (String mhs : listMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(mhs, idNotifikasi);
                }
                break;

            case "dosen2":
                bimbinganRepository.updateStatusDosen2(idBim, "Dibatalkan");
                for (String mhs : listMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(mhs, idNotifikasi);
                }
                break;

            case "mahasiswa":
                bimbinganRepository.updateStatusMahasiswa(idBim, "Dibatalkan");
                notifikasiService.insertDosenNotifikasi(dosenList.get(0), idNotifikasi);
                if (dosenList.size() > 1) {
                    notifikasiService.insertDosenNotifikasi(dosenList.get(1), idNotifikasi);
                }
                break;

            default:
                throw new IllegalArgumentException("Peran tidak valid: " + peran);
        }
    }

    public void terimaBimbingan(int idBim, String peran) {
        List<String> dosenList = bimbinganRepository.getListDosenByIdBim(idBim);
        int notifikasi = notifikasiService.insertNotifikasi("Menerima");
        List<String> listMahasiswa = bimbinganRepository.getMahasiswaBimbingan(idBim);
        switch (peran.toLowerCase()) {
            case "dosen1":
                bimbinganRepository.updateStatusDosen1(idBim, "Menyetujui");
                for (String mhs : listMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(mhs, notifikasi);
                }
                break;

            case "dosen2":
                bimbinganRepository.updateStatusDosen2(idBim, "Menyetujui");
                for (String mhs : listMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(mhs, notifikasi);
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
        BimbinganDetailStatus temp = bimbinganRepository.getDetailStatusBimbingan(idBim);

        if (temp.getStatusMhs().equals("Menyetujui") && temp.getStatusDosen1().equals("Menyetujui") || temp.getStatusDosen1().equals("Menyetujui")) {
            bimbinganRepository.updateStatusBimbingan(idBim, "Terjadwalkan");
        }
    }

    public void tolakBimbingan(int idBim, String peran, String catatan) {
        bimbinganRepository.updateCatatanBimbingan(idBim, catatan);
        bimbinganRepository.updateStatusBimbingan(idBim, "Gagal");
        List<String> dosenList = bimbinganRepository.getListDosenByIdBim(idBim);
        int idNotifikasi = notifikasiService.insertNotifikasi("Ditolak");
        List<String> listMahasiswa = bimbinganRepository.getMahasiswaBimbingan(idBim);

        switch (peran.toLowerCase()) {
            case "dosen1":
                bimbinganRepository.updateStatusDosen1(idBim, "Menolak");
                for (String mhs : listMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(mhs, idNotifikasi);
                }
                notifikasiService.insertDosenNotifikasi(dosenList.get(0), idNotifikasi);
                break;

            case "dosen2":
                bimbinganRepository.updateStatusDosen2(idBim, "Menolak");
                for (String mhs : listMahasiswa) {
                    notifikasiService.insertMahasiswaNotifikasi(mhs, idNotifikasi);
                }

                break;

            case "mahasiswa":
                bimbinganRepository.updateStatusMahasiswa(idBim, "Menolak");
                notifikasiService.insertDosenNotifikasi(dosenList.get(0), idNotifikasi);
                if (dosenList.size() > 1) {
                    notifikasiService.insertDosenNotifikasi(dosenList.get(1), idNotifikasi);
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
