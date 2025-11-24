package com.application.pota.jadwal;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JadwalService {

    private final JadwalRepository jadwalRepository;

    /**
     * Inner class untuk data jadwal dengan informasi tipe
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class JadwalWithType {
        private Jadwal jadwal;
        private String type; // "PEMBLOKIRAN" atau "BIMBINGAN"
        private String status; // untuk bimbingan: status dari tabel Bimbingan
    }

    /**
     * Inner class untuk mengembalikan struktur data yang lengkap untuk tampilan jadwal mingguan.
     */
    @Value
    public static class WeeklyScheduleData {
        Map<DayOfWeek, String> headerDates;
        Map<DayOfWeek, List<JadwalWithType>> scheduledSlots;
        LocalDate startOfWeek;
        LocalDate endOfWeek;
    }

    public WeeklyScheduleData getWeeklySchedule(String week, String targetId, boolean isRuangan) {
        LocalDate weekStartDate = getHariSenin(week);
        LocalDate weekEndDate = weekStartDate.plusDays(4);

        Map<DayOfWeek, List<JadwalWithType>> groupedSlots;

        if (isRuangan) {
            // Ambil pemblokiran ruangan
            List<Jadwal> blockedSlots = jadwalRepository.findByWeekRangeRuangan(
                    weekStartDate, weekEndDate, Integer.parseInt(targetId)
            );

            // Ambil jadwal bimbingan dengan status dari database
            List<JadwalJdbc.JadwalWithStatus> bimbinganSlots = jadwalRepository.findBimbinganByWeekRangeRuangan(
                    weekStartDate, weekEndDate, Integer.parseInt(targetId)
            );

            // Gabungkan dengan tipe
            List<JadwalWithType> combined = new ArrayList<>();

            // Tambahkan pemblokiran
            for (Jadwal j : blockedSlots) {
                combined.add(new JadwalWithType(j, "PEMBLOKIRAN", null));
            }

            // Tambahkan bimbingan dengan status dari database
            for (JadwalJdbc.JadwalWithStatus jws : bimbinganSlots) {
                combined.add(new JadwalWithType(jws.getJadwal(), "BIMBINGAN", jws.getStatus()));
            }

            groupedSlots = processJadwalWithType(combined);
        } else {
            List<Jadwal> rawJadwalList = jadwalRepository.findByWeekRangePengguna(
                    weekStartDate, weekEndDate, targetId
            );
            List<JadwalWithType> jadwalWithTypes = new ArrayList<>();
            for (Jadwal j : rawJadwalList) {
                jadwalWithTypes.add(new JadwalWithType(j, "PRIBADI", null));
            }
            groupedSlots = processJadwalWithType(jadwalWithTypes);
        }

        Map<DayOfWeek, String> headerDates = formatHariTanggal(weekStartDate);

        return new WeeklyScheduleData(headerDates, groupedSlots, weekStartDate, weekEndDate);
    }

    public static LocalDate getHariSenin(String week) {
        int year = Integer.parseInt(week.substring(0, 4));
        int weekNum = Integer.parseInt(week.substring(6));

        return LocalDate.now()
                .with(IsoFields.WEEK_BASED_YEAR, year)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNum)
                .with(DayOfWeek.MONDAY);
    }

    private Map<DayOfWeek, List<JadwalWithType>> processJadwalWithType(List<JadwalWithType> jadwalList) {
        Map<DayOfWeek, List<JadwalWithType>> mapJadwal = new LinkedHashMap<>();

        List<DayOfWeek> workDays = List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        );

        for (DayOfWeek day : workDays) {
            mapJadwal.put(day, new ArrayList<>());
        }

        for (JadwalWithType jadwalWithType : jadwalList) {
            DayOfWeek day = jadwalWithType.getJadwal().getTanggal().toLocalDate().getDayOfWeek();
            if (mapJadwal.containsKey(day)) {
                mapJadwal.get(day).add(jadwalWithType);
            }
        }

        return mapJadwal;
    }

    private Map<DayOfWeek, String> formatHariTanggal(LocalDate mondayDate) {
        Map<DayOfWeek, String> headerMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        for (int i = 0; i < 5; i++) {
            LocalDate date = mondayDate.plusDays(i);
            DayOfWeek day = date.getDayOfWeek();
            String formattedDate = date.format(formatter);
            headerMap.put(day, formattedDate);
        }

        return headerMap;
    }
}