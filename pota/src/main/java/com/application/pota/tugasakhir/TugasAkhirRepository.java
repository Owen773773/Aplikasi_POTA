package com.application.pota.tugasakhir;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface TugasAkhirRepository {
//    TugasAkhir getTugasAkhir(String idPengguna);
    int getIdTugasAkhir(String idPengguna);
    TugasAkhir getProfilMahasiswa(String idMahasiswa);
    String getIdMahasiswaByIdTa(int idTa);
    List<Integer> getListIdTugasAkhir(String idPengguna);
}