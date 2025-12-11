package com.application.pota.dosen;

import com.application.pota.bimbingan.BimbinganDosenDashboard;
import org.springframework.cglib.core.Local;
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

    public int getBanyakPengajuan(String idPengguna) {
        return dosenRepository.getBanyakPengajuanByIdPengguna(idPengguna);
    }

    public int getBanyakBimbinganHariIni(String idPengguna) {
        return dosenRepository.getBanyakBimbinganHariIniByIdPengguna(idPengguna);
    }

    public int getJumlahMahasiswaMemenuhiTarget(String idPengguna, String tahapBimb) {
        LocalDate tanggalUts = dosenRepository.getTanggalUtsByIdPengguna(idPengguna);
        LocalDate tanggalUas = dosenRepository.getTanggalUasByIdPengguna(idPengguna);
        LocalDate tanggalAwalMasuk = LocalDate.of(LocalDate.now().getYear(), 8, 1);

        if (LocalDate.now().isAfter(tanggalUts)) {
            return dosenRepository.getJumlahMahasiswaMemenuhiTargetPascaUTS(idPengguna, tanggalUts, tanggalUas);
        }
        else {
            return dosenRepository.getJumlahMahasiswaMemenuhiTargetPraUTS(idPengguna, tanggalAwalMasuk, tanggalUts);
        }
    }

    public BimbinganDosenDashboard getBimbinganSaatIni(String idPengguna) {
        BimbinganDosenDashboard curBim = dosenRepository.getBimbinganSaatIniByIdPengguna(idPengguna);

        if(curBim == null) return null;
        else {
            curBim.setDateNow(LocalDate.now());
            return curBim;
        }
    }

    public LocalDate getTanggalUtsByIdPengguna(String idPengguna){
        return  dosenRepository.getTanggalUtsByIdPengguna(idPengguna);
    };
    public LocalDate getTanggalUasByIdPengguna(String idPengguna){
        return  dosenRepository.getTanggalUasByIdPengguna(idPengguna);
    };
}
