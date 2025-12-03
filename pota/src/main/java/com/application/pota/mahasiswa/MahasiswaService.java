package com.application.pota.mahasiswa;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MahasiswaService {
    @Autowired
    private MahasiswaRepository mahasiswaRepository;
    public ProfilMahasiswa makeProfile(String id) {
        ProfilMahasiswa profilMahasiswa = mahasiswaRepository.makeProfileByIdPengguna(id);
        String kelayakan = "Tidak Layak";

        LocalDate tanggalUts = mahasiswaRepository.getTanggalUtsByIdMahasiswa(id);
        LocalDate timeSekarang = LocalDate.now();

        if(timeSekarang.isAfter(tanggalUts)) {
            //pasca
            int batasKelayakanPasca;
        }
        else {
            //pra
            int batasKelayakanPra;
        }

        return profilMahasiswa;
    }
}