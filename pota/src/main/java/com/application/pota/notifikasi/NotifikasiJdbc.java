package com.application.pota.notifikasi;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class NotifikasiJdbc implements NotifikasiRepository {

    private final JdbcTemplate jdbcTemplate;
}