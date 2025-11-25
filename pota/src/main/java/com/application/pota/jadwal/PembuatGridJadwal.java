package com.application.pota.jadwal;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class untuk membuat grid timetable dari jadwal yang sudah dikelompokkan
 *
 * CARA KERJA:
 * 1. Buat grid 2D kosong (11 jam x 5 hari)
 * 2. Loop setiap jam (07:00 - 17:00) sebagai BARIS
 * 3. Loop setiap hari (Senin-Jumat) sebagai KOLOM
 * 4. Untuk setiap sel, cek apakah ada jadwal di jam tersebut
 * 5. Tentukan status slot berdasarkan tipe jadwal dan statusnya
 */
public class PembuatGridJadwal {

    /**
     * Membuat grid timetable dari jadwal yang sudah dikelompokkan per hari
     *
     * @param jadwalPerHari Map yang berisi jadwal untuk setiap hari kerja
     * @return Grid 2D berisi SlotWaktu (baris=jam, kolom=hari)
     */
    public List<List<SlotWaktu>> buatGridJadwal(Map<DayOfWeek, List<JadwalService.JadwalDenganTipe>> jadwalPerHari) {
        List<List<SlotWaktu>> grid = new ArrayList<>();

        List<DayOfWeek> hariKerja = Arrays.asList(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        );

        // Loop untuk setiap jam kerja (07:00 - 17:00) = 11 baris
        for (int jam = 7; jam <= 17; jam++) {
            List<SlotWaktu> barisPerjam = new ArrayList<>();

            // Loop untuk setiap hari kerja = 5 kolom
            for (DayOfWeek hari : hariKerja) {
                List<JadwalService.JadwalDenganTipe> daftarJadwal = jadwalPerHari.get(hari);
                SlotWaktu slot = cariSlotPadaJam(daftarJadwal, jam, hari);
                barisPerjam.add(slot);
            }

            grid.add(barisPerjam);
        }

        return grid;
    }

    /**
     * Mencari apakah ada jadwal pada jam tertentu dan menentukan statusnya
     *
     * LOGIKA STATUS:
     * - PEMBLOKIRAN → blocked
     * - BIMBINGAN CONFIRMED/APPROVED → scheduled
     * - BIMBINGAN PENDING → pending
     * - PRIBADI → occupied
     * - Tidak ada jadwal → available
     *
     * @param daftarJadwal List jadwal pada hari tersebut
     * @param jam Jam yang dicek (0-23)
     * @param hari Hari yang dicek
     * @return SlotWaktu dengan status yang sesuai
     */
    private SlotWaktu cariSlotPadaJam(List<JadwalService.JadwalDenganTipe> daftarJadwal, int jam, DayOfWeek hari) {
        SlotWaktu slot = new SlotWaktu();
        slot.setJam(jam);
        slot.setHari(hari);
        slot.setIndeksHari(hari.getValue() - 1); // 0-4 untuk Senin-Jumat

        // Jika tidak ada jadwal pada hari ini, langsung return available
        if (daftarJadwal == null || daftarJadwal.isEmpty()) {
            slot.setStatus("available");
            return slot;
        }

        // Cek setiap jadwal apakah jam ini termasuk dalam rentang waktu jadwal
        for (JadwalService.JadwalDenganTipe jadwalDenganTipe : daftarJadwal) {
            Jadwal jadwal = jadwalDenganTipe.getJadwal();
            LocalTime waktuMulai = jadwal.getWaktuMulai().toLocalTime();
            LocalTime waktuSelesai = jadwal.getWaktuSelesai().toLocalTime();

            // Cek apakah jam ini berada dalam rentang jadwal
            // Contoh: jadwal 08:00-10:00 akan mengisi slot jam 8 dan 9
            boolean jamTermasukDalamJadwal = waktuMulai.getHour() <= jam && waktuSelesai.getHour() > jam;

            if (jamTermasukDalamJadwal) {
                slot.setIdBooking(jadwal.getIdJadwal());
                slot.setTipeBooking(jadwalDenganTipe.getTipe());

                // Tentukan status berdasarkan tipe jadwal
                String status = tentukanStatus(jadwalDenganTipe);
                slot.setStatus(status);

                return slot; // Sudah ketemu jadwal, keluar dari loop
            }
        }

        // Jika tidak ada jadwal yang cocok, slot available
        slot.setStatus("available");
        return slot;
    }

    /**
     * Menentukan status slot berdasarkan tipe dan status jadwal
     *
     * MAPPING STATUS UNTUK PENGGUNA:
     * - PRIBADI → blocked (kelas/jadwal pribadi = tidak bisa diganggu)
     * - BIMBINGAN CONFIRMED → scheduled (bimbingan terkonfirmasi)
     * - BIMBINGAN PENDING → pending (bimbingan menunggu konfirmasi)
     * - BIMBINGAN lainnya → pending
     *
     * MAPPING STATUS UNTUK RUANGAN:
     * - PEMBLOKIRAN → blocked (ruangan diblokir)
     * - BIMBINGAN CONFIRMED → scheduled
     * - BIMBINGAN PENDING → pending
     */
    private String tentukanStatus(JadwalService.JadwalDenganTipe jadwalDenganTipe) {
        String tipe = jadwalDenganTipe.getTipe();

        if ("PEMBLOKIRAN".equals(tipe) || "PRIBADI".equals(tipe)) {
            // Pemblokiran ruangan ATAU Jadwal pribadi pengguna = blocked
            return "blocked";

        } else if ("BIMBINGAN".equals(tipe)) {
            // Untuk bimbingan, cek status dari database
            String statusBimbingan = jadwalDenganTipe.getStatus();

            if (statusBimbingan == null) {
                return "pending";
            }

            // Normalisasi status (uppercase, trim whitespace)
            statusBimbingan = statusBimbingan.trim().toUpperCase();

            // Cek apakah sudah terkonfirmasi
            if (statusBimbingan.equals("CONFIRMED") ||
                    statusBimbingan.equals("TERKONFIRMASI") ||
                    statusBimbingan.equals("APPROVED")) {
                return "scheduled";

            } else {
                // Status PENDING, MENUNGGU, DIBATALKAN, dll → pending
                return "pending";
            }

        } else {
            // Tipe lainnya (fallback)
            return "occupied";
        }
    }
}