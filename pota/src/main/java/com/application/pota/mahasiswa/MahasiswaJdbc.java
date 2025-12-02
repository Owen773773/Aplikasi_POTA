package com.application.pota.mahasiswa;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.application.pota.pengguna.Pengguna;

@Repository
@RequiredArgsConstructor
public class MahasiswaJdbc implements MahasiswaRepository {
    private final JdbcTemplate jdbcTemplate;

    public Mahasiswa mapRowToPengguna(ResultSet rs, int rowNum) throws SQLException {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setIdPengguna(rs.getString("IdPengguna"));
        mahasiswa.setUsername(rs.getString("username"));
        mahasiswa.setPassword(rs.getString("password"));
        mahasiswa.setNama(rs.getString("nama"));
        mahasiswa.setStatusAktif(rs.getBoolean("statusAktif"));
        mahasiswa.setTipeAkun(rs.getString("tipeAkun"));
        mahasiswa.setLastLogin(rs.getTimestamp("lastLogin").toLocalDateTime());

        mahasiswa.setIdMahasiswa(rs.getString(null));

        return mahasiswa;
    }

    public Mahasiswa getById(String id) {
        String sql = "SELECT * FROM Pengguna WHERE IdPengguna = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToPengguna, id);
    }

}