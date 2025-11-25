package com.application.pota.mahasiswa;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.application.pota.pengguna.Pengguna;

@Repository
@RequiredArgsConstructor
public class MahasiswaJdbc implements MahasiswaRepository {
    private final JdbcTemplate jdbcTemplate;
}