package com.application.pota.dosen;

import com.application.pota.bimbingan.BimbinganDosenDashboard;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DosenJdbc implements DosenRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public ProfilDosen makeProfileByIdPengguna(String idPengguna) {
        String query = """
            SELECT p.IdPengguna, p.username, p.nama, p.tipeAkun
            FROM Pengguna p
            JOIN Dosen d ON d.IdPengguna = p.IdPengguna
            WHERE p.IdPengguna = ?
        """;
        return jdbcTemplate.queryForObject(query, this::mapRowToProfilDosen, idPengguna);
    }

    private ProfilDosen mapRowToProfilDosen(ResultSet rs, int rowNum) throws SQLException {
        ProfilDosen profilDosen = new ProfilDosen();
        profilDosen.setUsername(rs.getString("username"));
        profilDosen.setNama(rs.getString("nama"));
        profilDosen.setPeran(rs.getString("tipeAkun"));
        profilDosen.setNpm(rs.getString("IdPengguna"));
        return profilDosen;
    }

    @Override
    public LocalDate getTanggalUtsByIdPengguna(String idPengguna) {
        String query = """
            SELECT ta.TanggalUTS
            FROM TugasAkhir ta
            JOIN Dosen_Pembimbing dp ON dp.idTA = ta.IdTa
            WHERE dp.IdDosen = ?
            ORDER BY ta.TanggalUTS ASC
            LIMIT 1;
        """;

        try {
            return jdbcTemplate.queryForObject(query, LocalDate.class, idPengguna);
        } catch (Exception e) {
            // Dosen belum punya mahasiswa yang dibimbing
            System.err.println("Warning: Dosen " + idPengguna + " belum memiliki mahasiswa dengan tanggal UAS");
            return null;
        }}

    @Override
    public LocalDate getTanggalUasByIdPengguna(String idPengguna) {
        String query = """
            SELECT ta.TanggalUAS
             FROM TugasAkhir ta
             JOIN Dosen_Pembimbing dp ON dp.idTA = ta.IdTa
             WHERE dp.IdDosen = ?
             ORDER BY ta.TanggalUAS ASC
             LIMIT 1;
        """;

        try {
            return jdbcTemplate.queryForObject(query, LocalDate.class, idPengguna);
        } catch (Exception e) {
            // Dosen belum punya mahasiswa yang dibimbing
            System.err.println("Warning: Dosen " + idPengguna + " belum memiliki mahasiswa dengan tanggal UTS");
            return null;
        }}

    public int getBanyakMahasiswaByIdPengguna(String idPengguna) {
        String query = """
            SELECT COUNT(*)
            FROM Dosen_Pembimbing dp
            JOIN TugasAkhir ta ON ta.IdTa = dp.idTA
            JOIN Pengguna p ON p.IdPengguna = ta.IdMahasiswa
            WHERE dp.IdDosen = ?;
        """;

        return jdbcTemplate.queryForObject(query, Integer.class, idPengguna);
    }

    public int getBanyakPengajuanByIdPengguna(String idPengguna) {
        String query = """
            SELECT COUNT(*)
            FROM Dosen_Pembimbing dp
            JOIN TopikBimbingan tb ON tb.IdTA = dp.idTA
            WHERE dp.IdDosen = ? AND tb.StatusMhs = 'Menunggu';
        """;

        return jdbcTemplate.queryForObject(query, Integer.class, idPengguna);
    }

    public int getBanyakBimbinganHariIniByIdPengguna(String idPengguna) {
        String query = """
            SELECT COUNT(*)
            FROM Dosen_Pembimbing dp
            JOIN TopikBimbingan tb ON tb.IdTA = dp.idTA
            JOIN PenjadwalanBimbingan pb ON pb.IdBim = tb.IdBim
            JOIN Jadwal j ON j.IdJadwal = pb.IdJadwal
            WHERE dp.IdDosen = ? AND j.tanggal = CURRENT_DATE;
        """;

        return jdbcTemplate.queryForObject(query, Integer.class, idPengguna);
    }

    @Override
    public int getJumlahMahasiswaMemenuhiTargetPascaUTS(String idPengguna, LocalDate tanggalUts, LocalDate tanggalUas) {
        String query = """
            SELECT COUNT(DISTINCT ta.IdMahasiswa) AS jumlah
            FROM Dosen_Pembimbing dp
            JOIN TugasAkhir ta ON ta.IdTa = dp.idTA
            JOIN TopikBimbingan tb ON tb.IdTA = ta.IdTa
            JOIN PenjadwalanBimbingan pb ON pb.IdBim = tb.IdBim
            JOIN Jadwal j ON j.IdJadwal = pb.IdJadwal
            WHERE dp.IdDosen = ? AND j.tanggal BETWEEN ? AND ?
        """;

        return jdbcTemplate.queryForObject(query, Integer.class, idPengguna, tanggalUts, tanggalUas);
    }

    @Override
    public int getJumlahMahasiswaMemenuhiTargetPraUTS(String idPengguna, LocalDate tanggalAwalMasuk, LocalDate tanggalUts) {
        String query = """
            SELECT COUNT(DISTINCT ta.IdMahasiswa) AS jumlah
            FROM Dosen_Pembimbing dp
            JOIN TugasAkhir ta ON ta.IdTa = dp.idTA
            JOIN TopikBimbingan tb ON tb.IdTA = ta.IdTa
            JOIN PenjadwalanBimbingan pb ON pb.IdBim = tb.IdBim
            JOIN Jadwal j ON j.IdJadwal = pb.IdJadwal
            WHERE dp.IdDosen = ? AND j.tanggal BETWEEN ? AND ?
        """;

        return jdbcTemplate.queryForObject(query, Integer.class, idPengguna, tanggalAwalMasuk, tanggalUts);
    }

    @Override
    public BimbinganDosenDashboard getBimbinganSaatIniByIdPengguna(String idPengguna) {
        String query = """
            SELECT
                b.IdBim,
                b.DeskripsiBim,
                b.TopikBim,
                j.tanggal,
                j.WaktuMulai,
                j.WaktuSelesai,
                r.namaRuangan,
        
                STRING_AGG(DISTINCT p_dosen.nama, ', ') AS daftar_dosen,
                STRING_AGG(DISTINCT p_mhs.nama, ', ') AS daftar_mahasiswa
        
            FROM PenjadwalanBimbingan pb
            JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
            JOIN Bimbingan b ON pb.IdBim = b.IdBim
            LEFT JOIN Ruangan r ON b.idRuangan = r.idRuangan
        
            -- Mahasiswa
            LEFT JOIN TopikBimbingan tb ON tb.IdBim = b.IdBim
            LEFT JOIN TugasAkhir ta ON ta.IdTa = tb.IdTA
            LEFT JOIN Mahasiswa m ON m.IdPengguna = ta.IdMahasiswa
            LEFT JOIN Pengguna p_mhs ON p_mhs.IdPengguna = m.IdPengguna
        
            -- Dosen pembimbing
            LEFT JOIN Dosen_Pembimbing dp ON dp.idTA = ta.IdTa
            LEFT JOIN Dosen d ON d.IdPengguna = dp.IdDosen
            LEFT JOIN Pengguna p_dosen ON p_dosen.IdPengguna = d.IdPengguna
        
            WHERE
                j.tanggal = CURRENT_DATE
                AND j.WaktuMulai >= CURRENT_TIME
                AND p_dosen.IdPengguna = ?
        
            GROUP BY
                b.IdBim, b.DeskripsiBim, b.TopikBim,
                j.tanggal, j.WaktuMulai, j.WaktuSelesai,
                r.namaRuangan
        
            ORDER BY j.WaktuMulai ASC
            LIMIT 1;
        """;

        List<BimbinganDosenDashboard> currentBimb = jdbcTemplate.query(query, this::mapRowToBimbinganDashboard, idPengguna);
        return currentBimb.stream().findFirst().orElse(null);
    }

    private BimbinganDosenDashboard mapRowToBimbinganDashboard(ResultSet rs, int rowNum) throws SQLException {
        return new BimbinganDosenDashboard(
            null,
                rs.getTime("WaktuMulai"),
                rs.getTime("WaktuSelesai"),
                rs.getString("namaRuangan"),
                rs.getString("daftar_dosen"),
                rs.getString("daftar_mahasiswa")
        );
    }
}