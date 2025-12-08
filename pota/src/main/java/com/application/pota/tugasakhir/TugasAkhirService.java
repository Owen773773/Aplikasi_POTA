package com.application.pota.tugasakhir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TugasAkhirService {

    @Autowired
    private  TugasAkhirRepository tugasAkhirRepository;
//    public TugasAkhir TAbyIdPengguna(String idPengguna) {
//        return tugasAkhirRepository.getTugasAkhir(idPengguna);
//    }
    public int getIdTugasAkhir(String idPengguna){
        return tugasAkhirRepository.getIdTugasAkhir(idPengguna);
    }
}
