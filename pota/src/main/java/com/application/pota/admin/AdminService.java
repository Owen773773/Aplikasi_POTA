package com.application.pota.admin;

import com.application.pota.jadwal.Jadwal;
import com.application.pota.jadwal.JadwalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;

    /**
     * Membuat grid 11 jam × 5 hari untuk kemudahan iterasi di Thymeleaf
     */
    public List<List<TimeSlot>> buildTimetableGrid(Map<DayOfWeek, List<JadwalService.JadwalWithType>> scheduledSlots) {
        List<List<TimeSlot>> grid = new ArrayList<>();
        List<DayOfWeek> workDays = Arrays.asList(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        );

        // Loop untuk setiap jam (07:00 - 17:00)
        for (int hour = 7; hour <= 17; hour++) {
            List<TimeSlot> hourRow = new ArrayList<>();

            // Loop untuk setiap hari kerja
            for (DayOfWeek day : workDays) {
                List<JadwalService.JadwalWithType> jadwalList = scheduledSlots.get(day);
                TimeSlot slot = findSlotAtHour(jadwalList, hour, day);
                hourRow.add(slot);
            }

            grid.add(hourRow);
        }

        return grid;
    }

    /**
     * Mencari jadwal pada jam tertentu dan menentukan statusnya
     */
    private TimeSlot findSlotAtHour(List<JadwalService.JadwalWithType> jadwalList, int hour, DayOfWeek day) {
        TimeSlot slot = new TimeSlot();
        slot.setHour(hour);
        slot.setDay(day);
        slot.setDayIndex(day.getValue() - 1);

        if (jadwalList != null && !jadwalList.isEmpty()) {
            for (JadwalService.JadwalWithType jadwalWithType : jadwalList) {
                Jadwal jadwal = jadwalWithType.getJadwal();
                LocalTime waktuMulai = jadwal.getWaktuMulai().toLocalTime();
                LocalTime waktuSelesai = jadwal.getWaktuSelesai().toLocalTime();

                // Cek apakah jam ini berada dalam rentang jadwal
                if (waktuMulai.getHour() <= hour && waktuSelesai.getHour() > hour) {
                    slot.setBookingId(jadwal.getIdJadwal());
                    slot.setBookingType(jadwalWithType.getType());

                    // Tentukan status berdasarkan tipe
                    if ("PEMBLOKIRAN".equals(jadwalWithType.getType())) {
                        slot.setStatus("blocked");
                    } else if ("BIMBINGAN".equals(jadwalWithType.getType())) {
                        // Cek status bimbingan dari database
                        String statusBimbingan = jadwalWithType.getStatus();

                        // Sesuaikan dengan nilai di database (case-insensitive)
                        if (statusBimbingan != null) {
                            statusBimbingan = statusBimbingan.trim().toUpperCase();

                            if (statusBimbingan.equals("CONFIRMED") ||
                                    statusBimbingan.equals("TERKONFIRMASI") ||
                                    statusBimbingan.equals("APPROVED")) {
                                slot.setStatus("scheduled");
                            } else if (statusBimbingan.equals("PENDING") ||
                                    statusBimbingan.equals("MENUNGGU")) {
                                slot.setStatus("pending");
                            } else {
                                // Status lain (DIBATALKAN, dll) tetap pending
                                slot.setStatus("pending");
                            }
                        } else {
                            // Jika status null, anggap pending
                            slot.setStatus("pending");
                        }
                    } else {
                        slot.setStatus("occupied");
                    }

                    return slot;
                }
            }
        }

        slot.setStatus("available");
        return slot;
    }
}