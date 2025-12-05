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
        jadwal.setIdJadwal(rs.getInt("idjadwal"));
        jadwal.setTanggal(rs.getDate("tanggal"));
        jadwal.setWaktuMulai(rs.getTime("waktumulai"));
        jadwal.setWaktuSelesai(rs.getTime("waktuselesai"));
        jadwal.setBerulang(rs.getInt("berulang"));
        return jadwal;
    }

    private JadwalWithStatus mapRowToJadwalWithStatus(ResultSet rs, int rowNum) throws SQLException {
        Jadwal jadwal = new Jadwal();
        jadwal.setIdJadwal(rs.getInt("idjadwal"));
        jadwal.setTanggal(rs.getDate("tanggal"));
        jadwal.setWaktuMulai(rs.getTime("waktumulai"));
        jadwal.setWaktuSelesai(rs.getTime("waktuselesai"));
        jadwal.setBerulang(rs.getInt("berulang"));

        String status = rs.getString("statusbimbingan");
        return new JadwalWithStatus(jadwal, status);
    }

    @Override
    public List<Jadwal> findByIdPengguna(String IdPengguna, Date date) {
        String sql = "SELECT j.* FROM jadwal j " +
                "JOIN jadwal_pribadi jp ON j.idjadwal = jp.idjadwal " +
                "WHERE jp.idpengguna = ? AND j.tanggal = ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, IdPengguna, date);
    }

    @Override
    public List<Jadwal> findByDateByidRuangan(int idRuangan, Date date) {
        String sql = "SELECT j.* FROM jadwal j " +
                "JOIN pemblokiranruangan pr ON j.idjadwal = pr.idjadwal " +
                "WHERE pr.idruangan = ? AND j.tanggal = ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, idRuangan, date);
    }

    @Override
    public List<Jadwal> findByIdbimbingan(String idBim, Date date) {
        String sql = "SELECT j.* FROM jadwal j " +
                "JOIN jadwal_bimbingan jb ON j.idjadwal = jb.idjadwal " +
                "JOIN penjadwalanbimbingan pb ON jb.idjadwal = pb.idjadwal " +
                "WHERE pb.idbim = ? AND j.tanggal = ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, Integer.parseInt(idBim), date);
    }

    @Override
    public void addJadwal(Jadwal target) {
        String sql = "INSERT INTO jadwal (tanggal, waktumulai, waktuselesai, berulang) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                target.getTanggal(),
                target.getWaktuMulai(),
                target.getWaktuSelesai(),
                target.getBerulang());
    }

    @Override
    public void removeJadwal(int idJadwal) {
        String sql = "DELETE FROM jadwal WHERE idjadwal = ?";
        jdbcTemplate.update(sql, idJadwal);
    }

    @Override
    public List<Jadwal> findByWeekRangePengguna(LocalDate startOfWeek, LocalDate endOfWeek, String IdPengguna) {
        String sql = "SELECT j.* FROM jadwal j " +
                "JOIN jadwal_pribadi jp ON j.idjadwal = jp.idjadwal " +
                "WHERE jp.idpengguna = ? AND j.tanggal >= ? AND j.tanggal <= ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, IdPengguna, startOfWeek, endOfWeek);
    }

    @Override
    public List<Jadwal> findByWeekRangeRuangan(LocalDate startOfWeek, LocalDate endOfWeek, int idRuangan) {
        String sql = "SELECT j.* FROM jadwal j " +
                "JOIN pemblokiranruangan pr ON j.idjadwal = pr.idjadwal " +
                "WHERE pr.idruangan = ? AND j.tanggal >= ? AND j.tanggal <= ?";
        return jdbcTemplate.query(sql, this::mapRowToJadwal, idRuangan, startOfWeek, endOfWeek);
    }

    @Override
    public List<JadwalWithStatus> findBimbinganByWeekRangeRuangan(LocalDate startOfWeek, LocalDate endOfWeek, int idRuangan) {
        // Query diperbaiki: ambil statusbimbingan dari topikbimbingan, bukan dari bimbingan
        String sql = "SELECT DISTINCT j.idjadwal, j.tanggal, j.waktumulai, j.waktuselesai, j.berulang, tb.statusbimbingan " +
                "FROM jadwal j " +
                "JOIN jadwal_bimbingan jb ON j.idjadwal = jb.idjadwal " +
                "JOIN penjadwalanbimbingan pb ON jb.idjadwal = pb.idjadwal " +
                "JOIN bimbingan b ON pb.idbim = b.idbim " +
                "JOIN topikbimbingan tb ON b.idbim = tb.idbim " +
                "WHERE b.idruangan = ? AND j.tanggal >= ? AND j.tanggal <= ? " +
                "AND tb.statusbimbingan IN ('Terjadwalkan', 'Proses')"; // Filter status aktif
        return jdbcTemplate.query(sql, this::mapRowToJadwalWithStatus, idRuangan, startOfWeek, endOfWeek);
    }

    @Override
    public List<JadwalWithStatus> findBimbinganByWeekRangePengguna(LocalDate startOfWeek, LocalDate endOfWeek, String idPengguna) {
        // Menggunakan TopikBimbingan untuk menghubungkan bimbingan dengan mahasiswa
        // Menggunakan Dosen_Pembimbing untuk menghubungkan dengan dosen
        String sql = "SELECT DISTINCT j.idjadwal, j.tanggal, j.waktumulai, j.waktuselesai, j.berulang, tb.statusbimbingan " +
                "FROM jadwal j " +
                "JOIN jadwal_bimbingan jb ON j.idjadwal = jb.idjadwal " +
                "JOIN penjadwalanbimbingan pb ON jb.idjadwal = pb.idjadwal " +
                "JOIN bimbingan b ON pb.idbim = b.idbim " +
                "JOIN topikbimbingan tb ON b.idbim = tb.idbim " +
                "JOIN tugasakhir ta ON tb.idta = ta.idta " +
                "WHERE (" +
                "    ta.idmahasiswa = ? " +
                "    OR EXISTS (" +
                "        SELECT 1 FROM dosen_pembimbing dp " +
                "        WHERE dp.idta = ta.idta AND dp.iddosen = ?" +
                "    )" +
                ") " +
                "AND j.tanggal >= ? AND j.tanggal <= ? " +
                "AND tb.statusbimbingan IN ('Terjadwalkan', 'Proses')";

        return jdbcTemplate.query(sql, this::mapRowToJadwalWithStatus, idPengguna, idPengguna, startOfWeek, endOfWeek);
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