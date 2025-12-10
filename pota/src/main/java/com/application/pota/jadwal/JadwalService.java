package com.application.pota.jadwal;

import com.application.pota.ruangan.Ruangan;
import com.application.pota.ruangan.RuanganService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JadwalService {

    private final PembuatGridJadwal pembuatGridJadwal = new PembuatGridJadwal();
    private final JadwalRepository jadwalRepository;
    private final RuanganService ruanganService;


    @Data
    @AllArgsConstructor
    public static class JadwalDenganTipe {
        private Jadwal jadwal;
        private String tipe; // "BIMBINGAN" atau "PRIBADI"
        private String status; // untuk bimbingan: status dari tabel Bimbingan
    }

    @Value
    public static class DataJadwalMingguan {
        Map<DayOfWeek, String> tanggalHeader;
        List<List<SlotWaktu>> gridJadwal;
        LocalDate tanggalMulaiMinggu;
        LocalDate tanggalAkhirMinggu;
    }

    public DataJadwalMingguan dapatkanJadwalMingguan(String minggu, String idTarget, boolean apakahRuangan) {
        // Konversi minggu ke tanggal Senin dan Jumat
        LocalDate hariSenin = konversiMingguKeHariSenin(minggu);
        LocalDate hariJumat = hariSenin.plusDays(4);

        Map<DayOfWeek, List<JadwalDenganTipe>> jadwalPerHari;
        List<List<SlotWaktu>> gridJadwal;

        if (apakahRuangan) {
            // KHUSUS RUANGAN: Ambil HANYA jadwal bimbingan di ruangan yang dipilih
            // idTarget adalah ID ruangan yang dipilih admin untuk monitoring
            int idRuangan = Integer.parseInt(idTarget);

            List<JadwalJdbc.JadwalWithStatus> jadwalBimbingan =
                    jadwalRepository.findBimbinganByWeekRangeRuangan(
                            hariSenin, hariJumat, idRuangan
                    );

            // Konversi ke JadwalDenganTipe
            List<JadwalDenganTipe> semuaJadwal = new ArrayList<>();
            for (JadwalJdbc.JadwalWithStatus jws : jadwalBimbingan) {
                semuaJadwal.add(new JadwalDenganTipe(jws.getJadwal(), "BIMBINGAN", jws.getStatus()));
            }

            jadwalPerHari = kelompokkanJadwalPerHari(semuaJadwal);
            gridJadwal = pembuatGridJadwal.buatGridJadwal(jadwalPerHari);

        } else {
            List<Jadwal> jadwalPribadi = jadwalRepository.findByWeekRangePengguna(
                    hariSenin, hariJumat, idTarget
            );

            List<JadwalJdbc.JadwalWithStatus> jadwalBimbingan = jadwalRepository.findBimbinganByWeekRangePengguna(
                    hariSenin, hariJumat, idTarget
            );

            // Gabungkan semua jadwal pengguna
            List<JadwalDenganTipe> semuaJadwal = new ArrayList<>();

            // Tambahkan jadwal pribadi (kelas/acara pribadi)
            for (Jadwal j : jadwalPribadi) {
                semuaJadwal.add(new JadwalDenganTipe(j, "PRIBADI", null));
            }

            // Tambahkan jadwal bimbingan dengan statusnya
            for (JadwalJdbc.JadwalWithStatus jws : jadwalBimbingan) {
                semuaJadwal.add(new JadwalDenganTipe(jws.getJadwal(), "BIMBINGAN", jws.getStatus()));
            }

            jadwalPerHari = kelompokkanJadwalPerHari(semuaJadwal);
            gridJadwal = pembuatGridJadwal.buatGridJadwal(jadwalPerHari);
        }

        Map<DayOfWeek, String> headerTanggal = buatHeaderTanggal(hariSenin);

        return new DataJadwalMingguan(headerTanggal, gridJadwal, hariSenin, hariJumat);
    }

    /**
     * Konversi string minggu (format: "2024-W01") ke tanggal hari Senin
     */
    public static LocalDate konversiMingguKeHariSenin(String minggu) {
        int tahun = Integer.parseInt(minggu.substring(0, 4));
        int nomorMinggu = Integer.parseInt(minggu.substring(6));

        return LocalDate.now()
                .with(IsoFields.WEEK_BASED_YEAR, tahun)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, nomorMinggu)
                .with(DayOfWeek.MONDAY);
    }


    private Map<DayOfWeek, List<JadwalDenganTipe>> kelompokkanJadwalPerHari(List<JadwalDenganTipe> daftarJadwal) {
        Map<DayOfWeek, List<JadwalDenganTipe>> mapJadwal = new LinkedHashMap<>();

        // Inisialisasi untuk hari kerja (Senin-Jumat)
        List<DayOfWeek> hariKerja = List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        );

        for (DayOfWeek hari : hariKerja) {
            mapJadwal.put(hari, new ArrayList<>());
        }

        // Masukkan jadwal ke hari yang sesuai
        for (JadwalDenganTipe jadwalDenganTipe : daftarJadwal) {
            DayOfWeek hari = jadwalDenganTipe.getJadwal().getTanggal().toLocalDate().getDayOfWeek();
            if (mapJadwal.containsKey(hari)) {
                mapJadwal.get(hari).add(jadwalDenganTipe);
            }
        }

        return mapJadwal;
    }

    /**
     * Mencari slot waktu yang tersedia untuk semua individu yang terlibat
     * Digunakan saat membuat jadwal bimbingan baru
     */
    public List<String> cariSlotGabungan(
            List<String> listIdIndividu,   // Pengguna lain yang ikut dicek (dosen pembimbing)
            String idIndividuUtama,        // Pengguna yang sedang login (mahasiswa)
            LocalDate tanggal) {

        List<String> slotTersedia = new ArrayList<>();
        int jamMulaiKerja = 7;
        int jamSelesaiKerja = 18;

        // 1. Ambil jadwal individu utama (misalnya mahasiswa)
        List<Jadwal> utamaPribadi =
                jadwalRepository.findByWeekRangePengguna(tanggal, tanggal, idIndividuUtama);

        List<JadwalJdbc.JadwalWithStatus> utamaBimbingan =
                jadwalRepository.findBimbinganByWeekRangePengguna(tanggal, tanggal, idIndividuUtama);

        // 2. Ambil jadwal semua individu di list
        List<List<Jadwal>> listPribadi = new ArrayList<>();
        List<List<JadwalJdbc.JadwalWithStatus>> listBimbingan = new ArrayList<>();

        for (String idIndividu : listIdIndividu) {
            listPribadi.add(
                    jadwalRepository.findByWeekRangePengguna(tanggal, tanggal, idIndividu)
            );
            listBimbingan.add(
                    jadwalRepository.findBimbinganByWeekRangePengguna(tanggal, tanggal, idIndividu)
            );
        }

        // 3. Loop 07.00 – 17.00 (1 jam interval)
        for (int jam = jamMulaiKerja; jam < jamSelesaiKerja; jam++) {

            // Cek apakah individu utama sibuk?
            if (isSibuk(jam, utamaPribadi, utamaBimbingan)) {
                continue;
            }

            // Cek apakah SEMUA individu lain tidak sibuk
            boolean semuaBisa = true;

            for (int i = 0; i < listIdIndividu.size(); i++) {
                if (isSibuk(jam, listPribadi.get(i), listBimbingan.get(i))) {
                    semuaBisa = false;
                    break;
                }
            }

            if (semuaBisa) {
                slotTersedia.add(String.format("%02d:00", jam));
            }
        }

        return slotTersedia;
    }

    public int insertJadwal(LocalDate tanggal, LocalTime mulai, LocalTime selesai){
        return jadwalRepository.insertJadwal(tanggal,mulai,selesai);
    }

    private boolean isSibuk(int jamCek, List<Jadwal> jadwalPribadi, List<JadwalJdbc.JadwalWithStatus> jadwalBimbingan) {
        // Cek Jadwal Pribadi
        for (Jadwal j : jadwalPribadi) {
            int jamMulai = j.getWaktuMulai().toLocalTime().getHour();
            int jamSelesai = j.getWaktuSelesai().toLocalTime().getHour();

            if (jamCek >= jamMulai && jamCek < jamSelesai) {
                return true;
            }
        }

        // Cek Jadwal Bimbingan
        for (JadwalJdbc.JadwalWithStatus js : jadwalBimbingan) {
            String status = js.getStatus();
            // Abaikan jadwal yang batal/gagal
            if (status != null && (status.equalsIgnoreCase("DIBATALKAN") || status.equalsIgnoreCase("GAGAL"))) {
                continue;
            }

            int jamMulai = js.getJadwal().getWaktuMulai().toLocalTime().getHour();
            int jamSelesai = js.getJadwal().getWaktuSelesai().toLocalTime().getHour();

            if (jamCek >= jamMulai && jamCek < jamSelesai) {
                return true;
            }
        }

        return false;
    }

    /**
     * Membuat header tanggal untuk grid (Senin-Jumat)
     */
    private Map<DayOfWeek, String> buatHeaderTanggal(LocalDate hariSenin) {
        Map<DayOfWeek, String> headerMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        // Senin sampai Jumat (5 hari kerja)
        for (int i = 0; i < 5; i++) {
            LocalDate tanggal = hariSenin.plusDays(i);
            DayOfWeek hari = tanggal.getDayOfWeek();
            String tanggalTerformat = tanggal.format(formatter);
            headerMap.put(hari, tanggalTerformat);
        }

        return headerMap;
    }


    public List<Ruangan> cariRuanganTersedia(LocalDate tanggal, LocalTime mulai, LocalTime selesai) {
        List<Ruangan> semuaRuangan = ruanganService.getAllRuang();
        Map<Integer, List<JadwalDenganTipe>> jadwalPerRuangan = new HashMap<>();

        for (Ruangan r : semuaRuangan) {
            List<JadwalJdbc.JadwalWithStatus> bimbingan =
                    jadwalRepository.findBimbinganByWeekRangeRuangan(
                            tanggal, tanggal, r.getIdRuangan()
                    );

            List<JadwalDenganTipe> daftar = new ArrayList<>();

            // Filter jadwal bimbingan yang aktif (bukan dibatalkan/gagal)
            for (JadwalJdbc.JadwalWithStatus jws : bimbingan) {
                if (jws.getStatus() != null &&
                        (jws.getStatus().equalsIgnoreCase("GAGAL") ||
                                jws.getStatus().equalsIgnoreCase("DIBATALKAN"))) {
                    continue; // Skip jadwal yang dibatalkan
                }

                daftar.add(new JadwalDenganTipe(jws.getJadwal(), "BIMBINGAN", jws.getStatus()));
            }

            jadwalPerRuangan.put(r.getIdRuangan(), daftar);
        }

        List<Ruangan> ruanganTersedia = new ArrayList<>();

        for (Ruangan r : semuaRuangan) {
            boolean bentrok = false;
            List<JadwalDenganTipe> daftar = jadwalPerRuangan.get(r.getIdRuangan());

            for (JadwalDenganTipe jdt : daftar) {
                LocalTime m = jdt.getJadwal().getWaktuMulai().toLocalTime();
                LocalTime s = jdt.getJadwal().getWaktuSelesai().toLocalTime();

                if (!(selesai.isBefore(m) || selesai.equals(m) || mulai.isAfter(s) || mulai.equals(s))) {
                    bentrok = true;
                    break;
                }
            }

            if (!bentrok) {
                ruanganTersedia.add(r);
            }
        }

        return ruanganTersedia;
    }

}