package com.application.pota.dosen;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DosenRepository {
    ProfilDosen makeProfileByIdPengguna(String username);
    LocalDate getTanggalUtsByIdPengguna(String idPengguna);
    int getBanyakMahasiswaByIdPengguna(String idPengguna);
    int getBanyakBimbinganHariIniByIdPengguna(String idPengguna);
}