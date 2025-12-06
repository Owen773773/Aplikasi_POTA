package com.application.pota.mahasiswa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface MahasiswaRepository {
    ProfilMahasiswa makeProfileByIdPengguna(String id);
    LocalDate getTanggalUtsByIdMahasiswa(String id);
    List<Integer> getBatasKelayakanPraPasca(String id);
}