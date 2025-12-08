package com.application.pota.dosen;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.application.pota.pengguna.Pengguna;
import com.application.pota.pengguna.PenggunaJdbc;

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

    public LocalDate getTanggalUtsByIdPengguna(String idPengguna) {
        String query = """
            SELECT ta.TanggalUTS
            FROM TugasAkhir ta
            JOIN Dosen_Pembimbing dp ON dp.idTA = ta.IdTa
            WHERE dp.IdDosen = ?;
        """;

        return jdbcTemplate.queryForObject(query, LocalDate.class, idPengguna);
    }

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

    public int getBanyakBimbinganHariIniByIdPengguna(String idPengguna) {
        String query = """
            SELECT COUNT(*)
            FROM Dosen_Pembimbing dp
            JOIN TopikBimbingan tb ON tb.IdTA = dp.idTA
            JOIN PenjadwalanBimbingan pb ON pb.IdBim = tb.IdBim
            JOIN Jadwal j ON j.IdJadwal = pb.IdJadwal
            WHERE dp.IdDosen = ?
              AND j.tanggal = CURRENT_DATE;
        """;

        return jdbcTemplate.queryForObject(query, Integer.class, idPengguna);
    }
}