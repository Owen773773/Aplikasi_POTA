package com.application.pota.mahasiswa;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.pota.bimbingan.BimbinganSiapKirim;

@Service
public class MahasiswaService {
    @Autowired
    private MahasiswaRepository mahasiswaRepository;
    public ProfilMahasiswa makeProfile(String id) {
        ProfilMahasiswa profilMahasiswa = mahasiswaRepository.makeProfileByIdPengguna(id);
        String kelayakan = "Tidak Layak";

        LocalDate tanggalUts = mahasiswaRepository.getTanggalUtsByIdMahasiswa(id);
        LocalDate timeSekarang = LocalDate.now();

        List<Integer> batasPraPasca = mahasiswaRepository.getBatasKelayakanPraPasca(id);
        int batasPra = batasPraPasca.get(0);
        int batasPasca = batasPraPasca.get(1);

        if(timeSekarang.isAfter(tanggalUts)) {
            //pasca
            if (profilMahasiswa.getTotBimPas() >= batasPasca) {
                kelayakan = "Layak";
            }
        }
        else {
            //pra
            if (profilMahasiswa.getTotBimPra() >= batasPra) {
                kelayakan = "Layak";
            }
        }

        profilMahasiswa.setSyaratKelayakan(kelayakan);

        return profilMahasiswa;
    }

    public DashboardDataMhs getDashboardData(String idPengguna) {
        DashboardDataMhs data = mahasiswaRepository.getDashboardDataMhs(idPengguna);
        BimbinganSiapKirim bimbingan = mahasiswaRepository.getBimbinganMendatang(idPengguna);
        data.setBimbinganMendatang(bimbingan);
        return data;
    }
}