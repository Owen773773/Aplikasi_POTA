package com.application.pota.tugasakhir;

import java.time.LocalDate;
import java.util.List;

public interface TugasAkhirRepository {
    int getIdTugasAkhir(String idPengguna);
    TugasAkhir getProfilMahasiswa(String idMahasiswa);
    String getIdMahasiswaByIdTa(int idTa);
    List<Integer> getListIdTugasAkhir(String idPengguna);
    LocalDate getTanggalUasByIdMahasiswa(String idMahasiswa);
    LocalDate getTanggalUtsByIdMahasiswa(String idMahasiswa);
}