package com.application.pota.bimbingan;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.application.pota.dosen.DosenService;
import com.application.pota.jadwal.JadwalService;
import com.application.pota.notifikasi.NotifikasiService;
import com.application.pota.pengguna.PenggunaService;
import com.application.pota.tugasakhir.TugasAkhirService;


@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class BimbinganJdbc implements BimbinganRepository {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    private PenggunaService penggunaService;
    private JadwalService jadwalService;
    private TugasAkhirService tugasAkhirService;
    private DosenService dosenService;
    private NotifikasiService notifikasiService;

    //jgn lupaa ganti return type
    public void getTipeAkunByIdPengguna(String idPengguna, Bimbingan bimbingan, LocalDate tanggal, LocalTime waktuMulai, LocalTime waktuSelesai, Integer idRuangan){
        String tipeAkunQuery = """                    
                    SELECT tipeAkun
                    FROM Pengguna
                    WHERE IdPengguna ILIKE ?
                """;
        String tipeAkun = jdbcTemplate.queryForObject(tipeAkunQuery, String.class, idPengguna);
        if("Mahasiswa".equalsIgnoreCase(tipeAkun)){
            masukkanBimbinganBaruMahasiswa(idPengguna, bimbingan, tanggal, waktuMulai, waktuSelesai, idRuangan);
        } else if ("Dosen".equalsIgnoreCase(tipeAkun)){
            masukkanBimbinganBaruDosen(idPengguna, bimbingan, tanggal, waktuMulai, waktuSelesai, idRuangan);
        } else{
            throw new IllegalArgumentException("Tipe akun tidak valid");
        }
    }

    // public boolean masukkanBimbinganBaruDosen(String idPengguna, Bimbingan bimbingan, LocalDate tanggal, LocalTime waktuMulai, LocalTime waktuSelesai, Integer idRuangan){
    //     String sqlMaxId = "SELECT MAX(IdBim) FROM Bimbingan";
    //     Integer maxId = jdbcTemplate.queryForObject(sqlMaxId, Integer.class);
    //     if (maxId == null) {
    //         maxId = 1;
    //     }
    //     String sqlBimbingan = """
    //                     INSERT INTO Bimbingan (IdBim, DeskripsiBim, Catatan, TopikBim, Status, JumlahPeserta, idRuangan)
    //                     VALUES (?, ?, ?, ?, Menunggu, ?, ?)
    //                     """
    //     ;
    //     jdbcTemplate.update(
    //         sqlBimbingan,
    //         maxId,
    //         bimbingan.getCatatan(),
    //         bimbingan.getTopikBim(),
    //         bimbingan.getJumlahPeserta(),
    //         bimbingan.getIdRuangan()
    //     );

     

    // }
    // public boolean masukkanBimbinganBaruMahasiswa(String idPengguna, Bimbingan bimbingan, LocalDate tanggal, LocalTime waktuMulai, LocalTime waktuSelesai, Integer idRuangan){
    //     String sqlQuery = "INSERT INTO ";

    // }
    


}