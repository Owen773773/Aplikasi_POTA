package com.application.pota.pengguna;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class PenggunaJdbc implements PenggunaRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper untuk mapping ResultSet ke Pengguna
    private Pengguna mapRowToPengguna(ResultSet rs, int rowNum) throws SQLException {
        Pengguna pengguna = new Pengguna();
        pengguna.setIdPengguna(rs.getString("IdPengguna"));
        pengguna.setUsername(rs.getString("username"));
        pengguna.setPassword(rs.getString("password"));
        pengguna.setNama(rs.getString("nama"));
        pengguna.setStatusAktif(rs.getBoolean("statusAktif"));
        pengguna.setTipeAkun(rs.getString("tipeAkun"));
        pengguna.setLastLogin(rs.getTimestamp("lastLogin").toLocalDateTime());
        return pengguna;
    }

    @Override
    public Pengguna getById(String id) {
        String sql = "SELECT * FROM Pengguna WHERE IdPengguna = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToPengguna, id);
    }

    @Override
    public Pengguna getByUsername(String username) {
        String sql = "SELECT * FROM Pengguna WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToPengguna, username);
    }

    @Override
    public List<Pengguna> getByType(String tipeAkun) {
        String sql = "SELECT * FROM Pengguna WHERE tipeAkun = ?";
        return jdbcTemplate.query(sql, this::mapRowToPengguna, tipeAkun);
    }

    @Override
    public void add(Pengguna pengguna) {
        String sql = "INSERT INTO Pengguna (IdPengguna, username, password, nama, statusAktif, tipeAkun, lastLogin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                pengguna.getIdPengguna(),
                pengguna.getUsername(),
                pengguna.getPassword(),
                pengguna.getNama(),
                pengguna.isStatusAktif(),
                pengguna.getTipeAkun(),
                pengguna.getLastLogin()
        );
    }

    @Override
    public void edit(Pengguna pengguna) {
        String sql = "UPDATE Pengguna SET username = ?, password = ?, nama = ?, " +
                "statusAktif = ?, tipeAkun = ?, lastLogin = ? WHERE IdPengguna = ?";
        jdbcTemplate.update(sql,
                pengguna.getUsername(),
                pengguna.getPassword(),
                pengguna.getNama(),
                pengguna.isStatusAktif(),
                pengguna.getTipeAkun(),
                pengguna.getLastLogin(),
                pengguna.getIdPengguna()
        );
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Pengguna WHERE IdPengguna = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Pengguna> findAll() {
        String sql = "SELECT * FROM Pengguna";
        return jdbcTemplate.query(sql, this::mapRowToPengguna);
    }
}