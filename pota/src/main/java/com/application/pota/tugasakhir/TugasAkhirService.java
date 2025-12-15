package com.application.pota.tugasakhir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TugasAkhirService {
    @Autowired
    private  TugasAkhirRepository tugasAkhirRepository;

    public int getIdTugasAkhir(String idPengguna){
        return tugasAkhirRepository.getIdTugasAkhir(idPengguna);
    }

    public List<Integer> getListIdTugasAkhir(String idPengguna) {
       return tugasAkhirRepository.getListIdTugasAkhir(idPengguna);
    }

    public String getIdMahasiswaByIdTa(int idTa){
        return tugasAkhirRepository.getIdMahasiswaByIdTa(idTa);
    }

    public LocalDate getTanggalUtsByIdMahasiswa(String idMahasiswa) {
        return tugasAkhirRepository.getTanggalUtsByIdMahasiswa(idMahasiswa);
    }

    public LocalDate getTanggalUasByIdMahasiswa(String idMahasiswa) {
        return tugasAkhirRepository.getTanggalUasByIdMahasiswa(idMahasiswa);
    }
}
