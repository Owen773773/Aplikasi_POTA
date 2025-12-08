package com.application.pota.mahasiswa;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MahasiswaJdbc implements MahasiswaRepository {
    private final JdbcTemplate jdbcTemplate;
    
    //ambil nama, peran, dan bpm
    public ProfilMahasiswa makeProfileByIdPengguna (String id) {
        String query = """
            SELECT p.IdPengguna, p.username, p.nama, p.tipeAkun
            FROM Pengguna p
            JOIN Mahasiswa m ON m.IdPengguna = p.IdPengguna
            WHERE p.IdPengguna = ?
        """;

        System.out.println(id);

        ProfilMahasiswa profilMahasiswa = jdbcTemplate.queryForObject(query, this::mapRowToPengguna, id);
        
        //ambil dospem 1 dan 2
        query = """
            SELECT d1.nama AS namaDosen
            FROM Mahasiswa m
            JOIN TugasAkhir ta ON ta.IdMahasiswa = m.IdPengguna
            JOIN Dosen_Pembimbing dp ON dp.idTA = ta.IdTA
            JOIN Dosen d ON d.IdPengguna = dp.IdDosen
            JOIN Pengguna d1 ON d1.IdPengguna = d.IdPengguna
            WHERE m.IdPengguna = ?
        """;

        List<String> dospem = jdbcTemplate.query(query, this::mapRowToDosenPembimbing, id);

        profilMahasiswa.setDosen1(dospem.size() > 0 ? dospem.get(0) : null);
        profilMahasiswa.setDosen2(dospem.size() > 1 ? dospem.get(1) : null);

        //ambil jumlah pra dan pasca
        query = """
            SELECT 
            SUM(
                CASE 
                    WHEN tb.StatusBimbingan = 'Selesai'
                        AND j.tanggal < ta.TanggalUTS
                    THEN 1 ELSE 0
                END
            ) AS sebelum_uts,

            SUM(
                CASE 
                    WHEN tb.StatusBimbingan = 'Selesai'
                        AND j.tanggal >= ta.TanggalUTS
                    THEN 1 ELSE 0
                END
            ) AS sesudah_uts

        FROM TopikBimbingan tb
        JOIN TugasAkhir ta ON tb.IdTA = ta.IdTA
        JOIN Bimbingan b ON tb.IdBim = b.IdBim
        JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
        JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal

        WHERE ta.IdMahasiswa = ?;
        """;

        List<Integer> praPasca = jdbcTemplate.queryForObject(query, this::mapRowToPraPasca, id);

        profilMahasiswa.setTotBimPra(praPasca.get(0));
        profilMahasiswa.setTotBimPas(praPasca.get(1));

        return profilMahasiswa;
    }

    public ProfilMahasiswa mapRowToPengguna(ResultSet rs, int rowNum) throws SQLException {
        ProfilMahasiswa profilMahasiswa = new ProfilMahasiswa();
        profilMahasiswa.setNpm(rs.getString("IdPengguna"));
        profilMahasiswa.setNama(rs.getString("nama"));
        profilMahasiswa.setPeran(rs.getString("tipeAkun"));

        return profilMahasiswa;
    }
  
    public String mapRowToDosenPembimbing(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("namaDosen");
    }

    public List<Integer> mapRowToPraPasca(ResultSet rs, int rowNum) throws SQLException {
        List<Integer> praPasca = new ArrayList<>(); 
        praPasca.add(rs.getInt("sebelum_uts"));
        praPasca.add(rs.getInt("sesudah_uts"));
        return praPasca;
    }

    @Override
    public LocalDate getTanggalUtsByIdMahasiswa(String id) {
        String query = """
            SELECT TanggalUTS
            FROM TugasAkhir
            WHERE IdMahasiswa = ?;
        """;

        return jdbcTemplate.queryForObject(query, this::mapRowToTanggalUts, id);
    }




    public LocalDate mapRowToTanggalUts(ResultSet rs, int rowNum) throws SQLException {
        return rs.getDate("TanggalUTS").toLocalDate();
    }

    public List<Integer> getBatasKelayakanPraPasca(String id) {
        String query = """
            SELECT a.minimumPra, a.minimumPasca
            FROM Akademik a
            JOIN TAtermasukAkademik ta ON a.idSemester = ta.idAkademik
            JOIN TugasAkhir t ON t.IdTa = ta.IdTA
            WHERE t.IdMahasiswa = ?;
        """;

        List<Integer> batasPraPasca = jdbcTemplate.queryForObject(query, this::mapRowToBatasPraPasca, id);

        return  batasPraPasca;
    }

    public List<Integer> mapRowToBatasPraPasca(ResultSet rs, int rowNum) throws SQLException {
        List<Integer> batasPraPasca = new ArrayList<>();
        batasPraPasca.add(rs.getInt("minimumPra"));
        batasPraPasca.add(rs.getInt("minimumPasca"));
        return  batasPraPasca;
    }

}