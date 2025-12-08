package com.application.pota.dosen;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    public int getTahunDanSemester(String idPengguna) {
        String query = """
            
        """;
    }
}