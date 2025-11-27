package com.application.pota.jadwal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

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

    private JadwalWithStatus mapRowToJadwalWithStatus(ResultSet rs, int rowNum) throws SQLException {
        Jadwal jadwal = new Jadwal();
        jadwal.setIdJadwal(rs.getInt("IdJadwal"));
        jadwal.setTanggal(rs.getDate("tanggal"));
        jadwal.setWaktuMulai(rs.getTime("WaktuMulai"));
        jadwal.setWaktuSelesai(rs.getTime("WaktuSelesai"));
        jadwal.setBerulang(rs.getInt("berulang"));

        String status = rs.getString("Status");

        return new JadwalWithStatus(jadwal, status);
    }

    @Override
    public List<Jadwal> findByIdPengguna(String IdPengguna, Date date) {
        String sql = "SELECT j.* FROM Jadwal j " +
                "JOIN Jadwal_Pribadi jp ON j.IdJadwal = jp.IdJadwal " +
                "WHERE jp.IdPengguna = ? AND j.tanggal = ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, IdPengguna, date);
    }

    @Override
    public List<Jadwal> findByDateByidRuangan(int idRuangan, Date date) {
        String sql = "SELECT j.* FROM Jadwal j " +
                "JOIN PemblokiranRuangan pr ON j.IdJadwal = pr.IdJadwal " +
                "WHERE pr.IdRuangan = ? AND j.tanggal = ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, idRuangan, date);
    }

    @Override
    public List<Jadwal> findByIdbimbingan(String idBim, Date date) {
        String sql = "SELECT j.* FROM Jadwal j " +
                "JOIN PenjadwalanBimbingan pb ON j.IdJadwal = pb.IdJadwal " +
                "WHERE pb.IdBim = ? AND j.tanggal = ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, idBim, date);
    }

    @Override
    public void addJadwal(Jadwal target) {
        String sql = "INSERT INTO Jadwal (tanggal, WaktuMulai, WaktuSelesai, berulang) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                target.getTanggal(),
                target.getWaktuMulai(),
                target.getWaktuSelesai(),
                target.getBerulang());
    }

    @Override
    public void removeJadwal(int idJadwal) {
        String sql = "DELETE FROM Jadwal WHERE IdJadwal = ?";
        jdbcTemplate.update(sql, idJadwal);
    }

    @Override
    public List<Jadwal> findByWeekRangePengguna(LocalDate startOfWeek, LocalDate endOfWeek, String IdPengguna) {
        String sql = "SELECT j.* FROM Jadwal j " +
                "JOIN Jadwal_Pribadi jp ON j.IdJadwal = jp.IdJadwal " +
                "WHERE jp.IdPengguna = ? AND j.tanggal >= ? AND j.tanggal <= ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, IdPengguna, startOfWeek, endOfWeek);
    }

    @Override
    public List<Jadwal> findByWeekRangeRuangan(LocalDate startOfWeek, LocalDate endOfWeek, int idRuangan) {
        String sql = "SELECT j.* FROM Jadwal j " +
                "JOIN PemblokiranRuangan pr ON j.IdJadwal = pr.IdJadwal " +
                "WHERE pr.IdRuangan = ? AND j.tanggal >= ? AND j.tanggal <= ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, idRuangan, startOfWeek, endOfWeek);
    }

    @Override
    public List<JadwalWithStatus> findBimbinganByWeekRangeRuangan(LocalDate startOfWeek, LocalDate endOfWeek, int idRuangan) {
        String sql = "SELECT j.*, b.Status FROM Jadwal j " +
                "JOIN Jadwal_Bimbingan jb ON j.IdJadwal = jb.IdJadwal " +
                "JOIN PenjadwalanBimbingan pb ON jb.IdJadwal = pb.IdJadwal " +
                "JOIN Bimbingan b ON pb.IdBim = b.IdBim " +
                "WHERE b.idRuangan = ? AND j.tanggal >= ? AND j.tanggal <= ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwalWithStatus, idRuangan, startOfWeek, endOfWeek);
    }

    @Override
    public List<JadwalWithStatus> findBimbinganByWeekRangePengguna(LocalDate startOfWeek, LocalDate endOfWeek, String idPengguna) {
        // Query untuk mendapatkan jadwal bimbingan pengguna
        // Cek apakah pengguna terlibat sebagai mahasiswa ATAU dosen dalam bimbingan
        String sql = "SELECT DISTINCT j.*, b.Status FROM Jadwal j " +
                "JOIN Jadwal_Bimbingan jb ON j.IdJadwal = jb.IdJadwal " +
                "JOIN PenjadwalanBimbingan pb ON jb.IdJadwal = pb.IdJadwal " +
                "JOIN Bimbingan b ON pb.IdBim = b.IdBim " +
                "WHERE (" +
                "   EXISTS (" +
                "       SELECT 1 FROM MahasiswaProsesBimbingan mpb " +
                "       WHERE mpb.IdBimbingan = b.IdBim AND mpb.IdMahasiswa = ?" +
                "   ) OR EXISTS (" +
                "       SELECT 1 FROM DosenProsesBimbingan dpb " +
                "       WHERE dpb.IdBimbingan = b.IdBim AND dpb.IdDosen = ?" +
                "   )" +
                ") " +
                "AND j.tanggal >= ? AND j.tanggal <= ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwalWithStatus,
                idPengguna, idPengguna, startOfWeek, endOfWeek);
    }

    // Inner class untuk menampung jadwal dengan status
    public static class JadwalWithStatus {
        private final Jadwal jadwal;
        private final String status;

        public JadwalWithStatus(Jadwal jadwal, String status) {
            this.jadwal = jadwal;
            this.status = status;
        }

        public Jadwal getJadwal() {
            return jadwal;
        }

        public String getStatus() {
            return status;
        }
    }
}