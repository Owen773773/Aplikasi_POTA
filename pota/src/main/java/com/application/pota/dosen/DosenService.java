package com.application.pota.dosen;

import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DosenService {
    private final DosenRepository dosenRepository;

    public ProfilDosen ambilProfil(String idPengguna) {
        ProfilDosen profilDosen = dosenRepository.makeProfileByIdPengguna(idPengguna);

        return profilDosen;
    }

    public String getSemesterAktif(String idPengguna) {
        int firstYear = LocalDate.now().getYear();
        int secondYear = LocalDate.now().getYear() + 1;

        int currentMonth = LocalDate.now().getMonthValue();
        String ganjilGenap = "Genap";

        if (currentMonth >= 8 || currentMonth == 1) { //8 = agustus
            ganjilGenap = "Ganjil";

            if (currentMonth == 1) {
                firstYear--;
                secondYear--;
            }
        }

        return ganjilGenap + " " + String.valueOf(firstYear) + "/" + String.valueOf(secondYear);
    }

    public String getTahapBimbingan(String idPengguna) {
        LocalDate tanggalUts = dosenRepository.getTanggalUtsByIdPengguna(idPengguna);

        if (LocalDate.now().isAfter(tanggalUts)) return "Pasca - UTS";
        return "Pra - UTS";
    }

    public int getBanyakMHDibimbing(String idPengguna) {
        return dosenRepository.getBanyakMahasiswaByIdPengguna(idPengguna);
    }

    public int getBanyakBimbinganHariIni(String idPengguna) {
        return dosenRepository.getBanyakBimbinganHariIniByIdPengguna(idPengguna);
    }
}
