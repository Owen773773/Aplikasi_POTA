package com.application.pota.bimbingan;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class BimbinganJdbc implements BimbinganRepository {

    private final JdbcTemplate jdbcTemplate;
}