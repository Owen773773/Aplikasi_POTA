package com.application.pota.tugasakhir;
import org.springframework.stereotype.Repository;


public interface TugasAkhirRepository {
//    TugasAkhir getTugasAkhir(String idPengguna);
    TugasAkhir getProfilMahasiswa(String idMahasiswa);
}