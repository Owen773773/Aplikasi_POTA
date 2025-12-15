package com.application.pota.akademik;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
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