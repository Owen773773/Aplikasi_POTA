package com.application.pota.jadwal;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;

/**
 * Service untuk mengelola jadwal dan membuat timetable
 *
 * ALUR KERJA:
 * 1. Terima parameter minggu (string) dan ID target (pengguna/ruangan)
 * 2. Konversi minggu ke range tanggal (Senin-Jumat)
 * 3. Ambil data jadwal dari database
 * 4. Kelompokkan jadwal per hari
 * 5. Buat grid timetable (baris=jam, kolom=hari)
 * 6. Return data lengkap (grid + header tanggal)
 */
@Service
@RequiredArgsConstructor
public class JadwalService {

    private final PembuatGridJadwal pembuatGridJadwal = new PembuatGridJadwal();
    private final JadwalRepository jadwalRepository;

    /**
     * Data jadwal dengan informasi tipe dan status
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class JadwalDenganTipe {
        private Jadwal jadwal;
        private String tipe; // "PEMBLOKIRAN" atau "BIMBINGAN" atau "PRIBADI"
        private String status; // untuk bimbingan: status dari tabel Bimbingan
    }

    /**
     * Struktur data lengkap untuk tampilan jadwal mingguan
     */
    @Value
    public static class DataJadwalMingguan {
        Map<DayOfWeek, String> tanggalHeader;
        List<List<SlotWaktu>> gridJadwal;
        LocalDate tanggalMulaiMinggu;
        LocalDate tanggalAkhirMinggu;
    }

    /**
     * Mendapatkan jadwal mingguan untuk pengguna atau ruangan
     *
     * @param minggu Format: "2024-W01"
     * @param idTarget ID pengguna atau ID ruangan
     * @param apakahRuangan true jika mencari jadwal ruangan, false jika pengguna
     * @return Data jadwal mingguan lengkap dengan grid
     */
    public DataJadwalMingguan dapatkanJadwalMingguan(String minggu, String idTarget, boolean apakahRuangan) {
        // Konversi minggu ke tanggal Senin dan Jumat
        LocalDate hariSenin = konversiMingguKeHariSenin(minggu);
        LocalDate hariJumat = hariSenin.plusDays(4);

        Map<DayOfWeek, List<JadwalDenganTipe>> jadwalPerHari;
        List<List<SlotWaktu>> gridJadwal;

        if (apakahRuangan) {
            // Ambil jadwal untuk ruangan (pemblokiran + bimbingan)
            List<Jadwal> jadwalPemblokiran = jadwalRepository.findByWeekRangeRuangan(
                    hariSenin, hariJumat, Integer.parseInt(idTarget)
            );

            List<JadwalJdbc.JadwalWithStatus> jadwalBimbingan = jadwalRepository.findBimbinganByWeekRangeRuangan(
                    hariSenin, hariJumat, Integer.parseInt(idTarget)
            );

            // Gabungkan semua jadwal dengan tipenya
            List<JadwalDenganTipe> semuaJadwal = new ArrayList<>();

            // Tambahkan jadwal pemblokiran
            for (Jadwal j : jadwalPemblokiran) {
                semuaJadwal.add(new JadwalDenganTipe(j, "PEMBLOKIRAN", null));
            }

            // Tambahkan jadwal bimbingan dengan statusnya
            for (JadwalJdbc.JadwalWithStatus jws : jadwalBimbingan) {
                semuaJadwal.add(new JadwalDenganTipe(jws.getJadwal(), "BIMBINGAN", jws.getStatus()));
            }

            jadwalPerHari = kelompokkanJadwalPerHari(semuaJadwal);
            gridJadwal = pembuatGridJadwal.buatGridJadwal(jadwalPerHari);

        } else {
            // Ambil jadwal untuk pengguna (jadwal pribadi + bimbingan)
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

    /**
     * Kelompokkan daftar jadwal berdasarkan hari (Senin-Jumat)
     */
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

    public List<String> cariSlotGabungan(List<String> listIdDosen, String idMahasiswa, LocalDate tanggal) {
        List<String> slotTersedia = new ArrayList<>();
        int jamMulaiKerja = 7;
        int jamSelesaiKerja = 18;

        // 1. Ambil jadwal Mahasiswa (Pribadi + Bimbingan) hari itu
        // Menggunakan method findByWeekRange... dengan start & end date yang sama
        List<Jadwal> mhsPribadi = jadwalRepository.findByWeekRangePengguna(tanggal, tanggal, idMahasiswa);
        List<JadwalJdbc.JadwalWithStatus> mhsBimbingan = jadwalRepository.findBimbinganByWeekRangePengguna(tanggal, tanggal, idMahasiswa);

        // 2. Ambil jadwal Dosen (Pribadi + Bimbingan) hari itu
        List<List<Jadwal>> listDosenPribadi = new ArrayList<>();
        List<List<JadwalJdbc.JadwalWithStatus>> listDosenBimbingan = new ArrayList<>();

        for (String idDosen : listIdDosen) {
            listDosenPribadi.add(jadwalRepository.findByWeekRangePengguna(tanggal, tanggal, idDosen));
            listDosenBimbingan.add(jadwalRepository.findBimbinganByWeekRangePengguna(tanggal, tanggal, idDosen));
        }

        // 3. Loop per jam (07:00 - 17:00)
        for (int jam = jamMulaiKerja; jam < jamSelesaiKerja; jam++) {

            // Cek apakah Mahasiswa sibuk?
            if (isSibuk(jam, mhsPribadi, mhsBimbingan)) {
                continue; // Skip, mahasiswa sibuk
            }

            // Cek apakah SEMUA Dosen bisa?
            boolean semuaDosenBisa = true;
            for (int i = 0; i < listIdDosen.size(); i++) {
                // Cek dosen ke-i
                if (isSibuk(jam, listDosenPribadi.get(i), listDosenBimbingan.get(i))) {
                    semuaDosenBisa = false;
                    break; // Salah satu dosen sibuk, jam ini hangus
                }
            }

            // Jika semua aman, masukkan ke list
            if (semuaDosenBisa) {
                slotTersedia.add(String.format("%02d:00", jam));}
        }

        return slotTersedia;
    }
    public int insertJadwal(LocalDate tanggal, LocalTime mulai, LocalTime selesai){
        return jadwalRepository.insertJadwal(tanggal,mulai,selesai);
    }
    /**
     * Helper untuk mengecek tabrakan jadwal
     */
    private boolean isSibuk(int jamCek, List<Jadwal> jadwalPribadi, List<JadwalJdbc.JadwalWithStatus> jadwalBimbingan) {
        // Cek Jadwal Pribadi/Pemblokiran
        for (Jadwal j : jadwalPribadi) {
            // Konversi SQL Time ke LocalTime lalu ambil jam-nya
            int jamMulai = j.getWaktuMulai().toLocalTime().getHour();
            int jamSelesai = j.getWaktuSelesai().toLocalTime().getHour();

            // Logic: jamCek ada di antara jamMulai (inklusif) dan jamSelesai (eksklusif)
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
}