package com.application.pota.mahasiswa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class MahasiswaJdbc implements MahasiswaRepository {

    private final JdbcTemplate jdbcTemplate;
}