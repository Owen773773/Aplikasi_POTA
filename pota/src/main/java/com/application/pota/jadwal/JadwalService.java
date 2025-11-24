package com.application.pota.jadwal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JadwalService {

    private final JadwalRepository jadwalRepository;

    /**
     * Mencari jadwal pada hari tertentu dengan mempertimbangkan pengulangan
     */
    public List<Jadwal> getJadwalByDay(LocalDate targetDate) {
        return jadwalRepository.findByDayWithRecurrence(targetDate);
    }

    /**
     * Mendapatkan schedule slots untuk timetable mingguan
     * Format: List<List<ScheduleSlot>> dimana outer list = jam, inner list = hari
     */
    public List<List<ScheduleSlot>> getScheduleSlotsForWeek(String week) {
        // Parse week parameter (format: "2025-W02")
        int tahun = Integer.parseInt(week.substring(0, 4));
        int minggu = Integer.parseInt(week.substring(6));

        // Hitung tanggal mulai dan akhir minggu
        LocalDate startOfWeek = LocalDate.of(tahun, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, minggu)
                .with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Ambil semua jadwal dalam minggu ini
        List<Jadwal> weekJadwal = jadwalRepository.findByWeekRange(startOfWeek, endOfWeek);

        // Buat structure untuk schedule slots (11 jam x 7 hari)
        List<List<ScheduleSlot>> scheduleSlots = new ArrayList<>();

        // Loop untuk setiap jam (07.00 - 17.00 = 11 jam)
        for (int hour = 7; hour <= 17; hour++) {
            List<ScheduleSlot> hourSlots = new ArrayList<>();

            // Loop untuk setiap hari (0 = Senin, 6 = Minggu)
            for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
                LocalDate currentDate = startOfWeek.plusDays(dayIndex);

                // Cari jadwal yang aktif pada tanggal dan jam ini
                ScheduleSlot slot = findScheduleSlot(weekJadwal, currentDate, hour, dayIndex);
                hourSlots.add(slot);
            }

            scheduleSlots.add(hourSlots);
        }

        return scheduleSlots;
    }

    /**
     * Mendapatkan list hari dalam minggu untuk header timetable
     */
    public List<DayHeader> getDayHeadersForWeek(String week) {
        int tahun = Integer.parseInt(week.substring(0, 4));
        int minggu = Integer.parseInt(week.substring(6));

        LocalDate startOfWeek = LocalDate.of(tahun, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, minggu)
                .with(DayOfWeek.MONDAY);

        List<DayHeader> dayHeaders = new ArrayList<>();
        String[] namaNamaHari = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        for (int i = 0; i < 7; i++) {
            LocalDate tanggal = startOfWeek.plusDays(i);
            DayHeader header = new DayHeader();
            header.setNama(namaNamaHari[i]);
            header.setTanggal(tanggal.format(formatter));
            dayHeaders.add(header);
        }

        return dayHeaders;
    }

    /**
     * Helper method untuk mencari schedule slot pada tanggal dan jam tertentu
     */
    private ScheduleSlot findScheduleSlot(List<Jadwal> jadwalList, LocalDate date, int hour, int dayIndex) {
        ScheduleSlot slot = new ScheduleSlot();
        slot.setHour(hour);
        slot.setDayIndex(dayIndex);

        // Cek setiap jadwal
        for (Jadwal jadwal : jadwalList) {
            // Cek apakah jadwal aktif pada tanggal ini
            if (isJadwalActiveOnDate(jadwal, date)) {
                // Cek apakah jam slot berada dalam rentang waktu jadwal
                int waktuMulaiHour = jadwal.getWaktuMulai().toLocalTime().getHour();
                int waktuSelesaiHour = jadwal.getWaktuSelesai().toLocalTime().getHour();

                if (hour >= waktuMulaiHour && hour < waktuSelesaiHour) {
                    slot.setStatus("booked");
                    slot.setBookingId(jadwal.getIdJadwal());
                    return slot;
                }
            }
        }

        // Default: slot kosong
        slot.setStatus("available");
        return slot;
    }

    /**
     * Helper method untuk mengecek apakah jadwal aktif pada tanggal tertentu
     * (Duplikasi dari JadwalJdbc untuk konsistensi logic)
     */
    private boolean isJadwalActiveOnDate(Jadwal jadwal, LocalDate targetDate) {
        LocalDate jadwalDate = jadwal.getTanggal().toLocalDate();

        if (targetDate.isBefore(jadwalDate)) {
            return false;
        }

        if (jadwal.getBerulang() == 0) {
            return targetDate.equals(jadwalDate);
        }

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(jadwalDate, targetDate);
        return daysBetween % jadwal.getBerulang() == 0;
    }

    // Inner classes untuk return types
    public static class ScheduleSlot {
        private int hour;
        private int dayIndex;
        private String status; // "available", "booked", "blocked"
        private Integer bookingId;

        public int getHour() { return hour; }
        public void setHour(int hour) { this.hour = hour; }

        public int getDayIndex() { return dayIndex; }
        public void setDayIndex(int dayIndex) { this.dayIndex = dayIndex; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Integer getBookingId() { return bookingId; }
        public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    }

    public static class DayHeader {
        private String nama;
        private String tanggal;

        public String getNama() { return nama; }
        public void setNama(String nama) { this.nama = nama; }

        public String getTanggal() { return tanggal; }
        public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    }
}