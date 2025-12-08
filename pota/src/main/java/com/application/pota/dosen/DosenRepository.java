package com.application.pota.dosen;
import org.springframework.stereotype.Repository;

@Repository
public interface DosenRepository {
    ProfilDosen makeProfileByIdPengguna(String username);
    getTahunAktifByIdPengguna(String idPengguna);
}