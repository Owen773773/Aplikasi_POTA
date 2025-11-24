package com.application.pota.jadwal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JadwalJdbc implements JadwalRepository {

    private final JdbcTemplate jdbcTemplate;

    private Jadwal mapRowToJadwal(ResultSet rs, int rowNum) throws SQLException {
        Jadwal jadwal = new Jadwal();
        jadwal.setIdJadwal(rs.getInt("IdJadwal"));
        jadwal.setTanggal(rs.getDate("tanggal"));
        jadwal.setWaktuMulai(rs.getTime("WaktuMulai"));
        jadwal.setWaktuSelesai(rs.getTime("WaktuSelesai"));
        jadwal.setBerulang(rs.getInt("berulang"));
        return jadwal;
    }

    @Override
    public List<Jadwal> findByUsername(String username) {
        String sql = "SELECT j.* FROM Jadwal j " +
                "JOIN Booking b ON j.IdJadwal = b.IdJadwal " +
                "JOIN Pengguna p ON b.IdPengguna = p.IdPengguna " +
                "WHERE p.username = ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, username);
    }

    @Override
    public List<Jadwal> findByDate(LocalDateTime date) {
        LocalDate targetDate = date.toLocalDate();
        return findByDayWithRecurrence(targetDate);
    }

    @Override
    public List<Jadwal> findByDayWithRecurrence(LocalDate targetDate) {
        // Ambil semua jadwal
        String sql = "SELECT * FROM Jadwal";
        List<Jadwal> allJadwal = jdbcTemplate.query(sql, this::mapRowToJadwal);

        // Filter jadwal yang aktif pada targetDate dengan mempertimbangkan pengulangan
        List<Jadwal> activeJadwal = new ArrayList<>();

        for (Jadwal jadwal : allJadwal) {
            if (isJadwalActiveOnDate(jadwal, targetDate)) {
                activeJadwal.add(jadwal);
            }
        }

        return activeJadwal;
    }

    @Override
    public List<Jadwal> findByWeekRange(LocalDate startOfWeek, LocalDate endOfWeek) {
        // Ambil semua jadwal
        String sql = "SELECT * FROM Jadwal";
        List<Jadwal> allJadwal = jdbcTemplate.query(sql, this::mapRowToJadwal);

        // Map untuk menyimpan jadwal berdasarkan tanggal aktifnya dalam minggu ini
        Map<Integer, List<LocalDate>> jadwalActiveDates = new HashMap<>();

        // Filter jadwal yang aktif dalam rentang minggu
        for (Jadwal jadwal : allJadwal) {
            List<LocalDate> activeDates = new ArrayList<>();
            LocalDate currentDate = startOfWeek;

            while (!currentDate.isAfter(endOfWeek)) {
                if (isJadwalActiveOnDate(jadwal, currentDate)) {
                    activeDates.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }

            if (!activeDates.isEmpty()) {
                jadwalActiveDates.put(jadwal.getIdJadwal(), activeDates);
            }
        }

        // Kembalikan jadwal yang memiliki tanggal aktif
        List<Jadwal> weekJadwal = new ArrayList<>();
        for (Jadwal jadwal : allJadwal) {
            if (jadwalActiveDates.containsKey(jadwal.getIdJadwal())) {
                weekJadwal.add(jadwal);
            }
        }

        return weekJadwal;
    }

    @Override
    public List<Jadwal> findAll() {
        String sql = "SELECT * FROM Jadwal";
        return jdbcTemplate.query(sql, this::mapRowToJadwal);
    }

    /**
     * Helper method untuk mengecek apakah jadwal aktif pada tanggal tertentu
     * berdasarkan tanggal awal dan nilai berulang (dalam hari)
     *
     * @param jadwal Jadwal yang akan dicek
     * @param targetDate Tanggal yang akan dicek
     * @return true jika jadwal aktif pada tanggal tersebut
     */
    private boolean isJadwalActiveOnDate(Jadwal jadwal, LocalDate targetDate) {
        LocalDate jadwalDate = jadwal.getTanggal().toLocalDate();

        // Jika targetDate sebelum tanggal jadwal dimulai, return false
        if (targetDate.isBefore(jadwalDate)) {
            return false;
        }

        // Jika berulang = 0, hanya aktif pada tanggal yang sama persis
        if (jadwal.getBerulang() == 0) {
            return targetDate.equals(jadwalDate);
        }

        // Hitung selisih hari antara jadwalDate dan targetDate
        long daysBetween = ChronoUnit.DAYS.between(jadwalDate, targetDate);

        // Jika selisih hari habis dibagi berulang, maka jadwal aktif
        return daysBetween % jadwal.getBerulang() == 0;
    }
}