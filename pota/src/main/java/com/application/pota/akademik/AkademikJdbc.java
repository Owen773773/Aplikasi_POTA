package com.application.pota.akademik;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.application.pota.notifikasi.Notifikasi;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class AkademikJdbc implements AkademikRepository {

    private final JdbcTemplate jdbcTemplate;
    public int getMinimumPra(int idSemester){
        String querySql = """
                SELECT minimumPra FROM Akademik WHERE idSemester = ?
                """
                ;
        return jdbcTemplate.queryForObject(querySql, Integer.class, idSemester);
    }

    public int getMinimumPasca(int idSemester){
        String querySql = """
                SELECT minimumPasca FROM Akademik WHERE idSemester = ?
                """
                ;
        return jdbcTemplate.queryForObject(querySql, Integer.class, idSemester);
    }
   
}