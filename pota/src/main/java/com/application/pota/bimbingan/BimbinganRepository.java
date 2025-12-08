package com.application.pota.bimbingan;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BimbinganRepository {
    Bimbingan bimbinganTerdekat(String idMahasiswa);

    List<BimbinganSiapKirim> getBimbinganUserBertipe(String tipeAkun, String tipeStatus, String idPengguna);

    List<PilihanPengguna> getDosenPembimbingPilihan(int idTa);

    List<PilihanPengguna> getMahasiswaPilihan(String idDosen);

    int insertBimbingan(String topik, String deskripsi, int jumlahPeserta, Integer idRuangan);

    void insertPenjadwalanBimbingan(int idJadwal, int idBim);

    void insertJadwalBimbingan(int idJadwal);

    void insertTopikBimbingan(
            int idBim,
            int idTA,
            String statusMhs,
            String statusDosen1,
            String statusDosen2,
            String statusBimbingan
    );

}