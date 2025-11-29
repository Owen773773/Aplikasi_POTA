package com.application.pota.akademik;
import org.springframework.stereotype.Repository;

@Repository
public interface AkademikRepository {
    int getMinimumPra(int idSemester);
    int getMinimumPasca(int idSemester);
}