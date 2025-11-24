package com.application.pota.akademik;

import com.application.pota.admin.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class AkademikJdbc implements AkademikRepository {

    private final JdbcTemplate jdbcTemplate;
}