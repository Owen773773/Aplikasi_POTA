package com.application.pota.ruangan;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RuanganJdbc implements RuanganRepository {

    private final JdbcTemplate jdbcTemplate;


    private Ruangan mapRowToRuangan(ResultSet rs, int rowNum) throws SQLException {
        Ruangan ruangan = new Ruangan();
        ruangan.setIdRuangan(rs.getInt("idRuangan"));
        ruangan.setNamaRuangan(rs.getString("namaRuangan"));
        return ruangan;
    }

    @Override
    public List<Ruangan> getAllRuang() {
        String sql = "SELECT * FROM Ruangan";
        return jdbcTemplate.query(sql, this::mapRowToRuangan);
    }

}