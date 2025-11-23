package com.application.pota.tugasakhir;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class TugasAkhirJdbc implements TugasAkhirRepository {

    private final JdbcTemplate jdbcTemplate;
}