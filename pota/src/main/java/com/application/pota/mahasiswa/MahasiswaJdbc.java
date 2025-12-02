package com.application.pota.mahasiswa;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MahasiswaJdbc implements MahasiswaRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public ProfilMahasiswa makeProfileByIdPengguna (String id) {
        String query = """
            SELECT p.IdPengguna, p.username, p.nama, p.tipeAkun
            FROM Pengguna p
            JOIN Mahasiswa m ON m.IdPengguna = p.IdPengguna
            WHERE p.IdPengguna = ?
        """;

        ProfilMahasiswa profilMahasiswa = jdbcTemplate.queryForObject(query, this::mapRowToPengguna, id);
        
        query = """
            SELECT d1.nama AS namaDosen
            FROM Mahasiswa m
            JOIN TugasAkhir ta ON ta.IdMahasiswa = m.IdPengguna
            JOIN Dosen_Pembimbing dp ON dp.idTA = ta.IdTA
            JOIN Dosen d ON d.IdPengguna = dp.IdDosen
            JOIN Pengguna d1 ON d1.IdPengguna = d.IdPengguna
            WHERE m.IdPengguna = ?
        """;

        List<String> dospem = jdbcTemplate.query(query, this::mapRowToDosenPembimbing, id);

        profilMahasiswa.setDosen1(dospem.size() > 0 ? dospem.get(0) : null);
        profilMahasiswa.setDosen2(dospem.size() > 1 ? dospem.get(1) : null);

        query = "";

        return profilMahasiswa;
    }

    public ProfilMahasiswa mapRowToPengguna(ResultSet rs, int rowNum) throws SQLException {
        ProfilMahasiswa profilMahasiswa = new ProfilMahasiswa();
        profilMahasiswa.setNpm(rs.getString("IdPengguna"));
        profilMahasiswa.setNama(rs.getString("nama"));
        profilMahasiswa.setPeran(rs.getString("tipeAkun"));

        return profilMahasiswa;
    }

    public String mapRowToDosenPembimbing(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("namaDosen");
    }
}