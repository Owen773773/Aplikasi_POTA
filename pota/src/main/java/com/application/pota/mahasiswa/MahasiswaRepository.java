package com.application.pota.mahasiswa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.application.pota.bimbingan.BimbinganSiapKirim;

@Repository
public interface MahasiswaRepository {
    ProfilMahasiswa makeProfileByIdPengguna(String id);
    LocalDate getTanggalUtsByIdMahasiswa(String id);
    List<Integer> getBatasKelayakanPraPasca(String id);

    DashboardDataMhs getDashboardDataMhs(String idMhs);
    BimbinganSiapKirim getBimbinganMendatang(String idMhs);
}