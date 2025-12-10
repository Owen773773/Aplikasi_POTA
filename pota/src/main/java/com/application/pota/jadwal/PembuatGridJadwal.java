package com.application.pota.jadwal;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class PembuatGridJadwal {

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
            boolean jamTermasukDalamJadwal = waktuMulai.getHour() <= jam && waktuSelesai.getHour() > jam;

            if (jamTermasukDalamJadwal) {
                // Tentukan status berdasarkan tipe jadwal
                String status = tentukanStatus(jadwalDenganTipe);

                // Jika status adalah available (bimbingan dibatalkan/gagal), skip jadwal ini
                if ("available".equals(status)) {
                    continue; // Lanjut cek jadwal berikutnya
                }

                slot.setIdBooking(jadwal.getIdJadwal());
                slot.setTipeBooking(jadwalDenganTipe.getTipe());
                slot.setStatus(status);

                return slot; // Sudah ketemu jadwal yang valid, keluar dari loop
            }
        }

        // Jika tidak ada jadwal yang cocok, slot available
        slot.setStatus("available");
        return slot;
    }

    private String tentukanStatus(JadwalService.JadwalDenganTipe jadwalDenganTipe) {
        String tipe = jadwalDenganTipe.getTipe();

        if ("PRIBADI".equalsIgnoreCase(tipe)) {
            return "blocked";

        } else if ("BIMBINGAN".equalsIgnoreCase(tipe)) {
            // Untuk bimbingan, cek status dari database
            String statusBimbingan = jadwalDenganTipe.getStatus();

            if (statusBimbingan == null) {
                return "pending";
            }

            // Normalisasi status (uppercase, trim whitespace)
            statusBimbingan = statusBimbingan.trim().toUpperCase();

            // Cek apakah bimbingan dibatalkan atau gagal
            if ("DIBATALKAN".equals(statusBimbingan) || "GAGAL".equals(statusBimbingan)) {
                return "available"; // Slot tersedia karena bimbingan dibatalkan/gagal
            }

            // Cek apakah sudah selesai
            if ("SELESAI".equals(statusBimbingan)) {
                return "done"; // Bimbingan sudah selesai
            }

            // Cek apakah sudah terkonfirmasi/terjadwalkan
            if ("TERJADWALKAN".equals(statusBimbingan) ||
                    "TERKONFIRMASI".equals(statusBimbingan) ||
                    "CONFIRMED".equals(statusBimbingan)) {
                return "scheduled";

            } else {
                // Status PENDING, MENUNGGU, PROSES, dll → pending
                return "pending";
            }

        } else {
            // Tipe lainnya (fallback)
            return "occupied";
        }
    }
}