package com.application.pota.jadwal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class JadwalJdbc implements JadwalRepository {

    private final JdbcTemplate jdbcTemplate;
}