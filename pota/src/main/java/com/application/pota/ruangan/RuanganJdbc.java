package com.application.pota.ruangan;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class RuanganJdbc implements RuanganRepository {

    private final JdbcTemplate jdbcTemplate;
}