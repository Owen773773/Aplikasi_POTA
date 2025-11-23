package com.application.pota.dosen;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class DosenJdbc implements DosenRepository {

    private final JdbcTemplate jdbcTemplate;
}