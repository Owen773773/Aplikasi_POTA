package com.application.pota.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class AdminJdbc implements AdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public void deleteUser(String idPengguna){
        String sql = "DELETE From Pengguna WHERE id_pengguna = ?";
        jdbcTemplate.update(sql, idPengguna);
    }
}