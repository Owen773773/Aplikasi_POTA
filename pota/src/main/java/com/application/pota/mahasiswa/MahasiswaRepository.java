package com.application.pota.mahasiswa;

import com.application.pota.bimbingan.BimbinganSiapKirim;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MahasiswaRepository {
    ProfilMahasiswa makeProfileByIdPengguna(String id);
    LocalDate getTanggalUtsByIdMahasiswa(String id);
    List<Integer> getBatasKelayakanPraPasca(String id);

    DashboardDataMhs getDashboardDataMhs(String idMhs);
    BimbinganSiapKirim getBimbinganMendatang(String idMhs);
}