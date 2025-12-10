package com.application.pota.dosen;
import com.application.pota.bimbingan.BimbinganDosenDashboard;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DosenRepository {
    ProfilDosen makeProfileByIdPengguna(String username);
    LocalDate getTanggalUtsByIdPengguna(String idPengguna);
    LocalDate getTanggalUasByIdPengguna(String idPengguna);
    int getBanyakMahasiswaByIdPengguna(String idPengguna);
    int getBanyakBimbinganHariIniByIdPengguna(String idPengguna);
    int getBanyakPengajuanByIdPengguna(String idPengguna);
    int getJumlahMahasiswaMemenuhiTargetPraUTS(String idPengguna, LocalDate tanggalUts, LocalDate tanggalUas);
    int getJumlahMahasiswaMemenuhiTargetPascaUTS(String idPengguna, LocalDate tanggalAwalMasuk, LocalDate tanggalUts);
    BimbinganDosenDashboard getBimbinganSaatIniByIdPengguna(String idPengguna);
}