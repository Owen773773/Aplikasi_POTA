package com.application.pota.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminJdbc implements AdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public void deleteUser(String idPengguna){
        String sql = "DELETE From Pengguna WHERE id_pengguna = ?";
        jdbcTemplate.update(sql, idPengguna);
    }
}