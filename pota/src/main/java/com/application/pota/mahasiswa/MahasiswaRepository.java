package com.application.pota.mahasiswa;

import java.time.LocalDate;

import org.springframework.stereotype.Repository;

@Repository
public interface MahasiswaRepository {
    ProfilMahasiswa makeProfileByIdPengguna(String id);
    LocalDate getTanggalUtsByIdMahasiswa(String id);
}