package com.application.pota.tugasakhir;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class TugasAkhirJdbc implements TugasAkhirRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public TugasAkhir getProfilMahasiswa(String idMahasiswa) {
        String sql1 = """
            SELECT ta.IdTa, ta.TopikTA, ta.TanggalUTS, ta.TanggalUas, m.TahapTA
            FROM TugasAkhir ta
            JOIN Mahasiswa m ON ta.IdMahasiswa = m.IdPengguna
            WHERE ta.IdMahasiswa = ?
                """;
        List <TugasAkhir> hasilSql1 = jdbcTemplate.query(sql1, this::mapRowToTugasAkhir, idMahasiswa);
        if(hasilSql1.isEmpty()){
            return null;
        }
        TugasAkhir profilMahasiswa = hasilSql1.get(0);
        String sql2 = """
            SELECT p.nama 
            FROM Dosen_Pembimbing dp 
            JOIN Pengguna p ON dp.IdDosen = p.IdPengguna 
            WHERE dp.idTA = ?
        """;
        
        List<String> listDosen = jdbcTemplate.queryForList(sql2, String.class, profilMahasiswa.getIdTa());
        
        if (listDosen.size() > 1) {
            profilMahasiswa.setNamaDosen1(listDosen.get(0));
            profilMahasiswa.setNamaDosen2(listDosen.get(1));
        }
        else {
            profilMahasiswa.setNamaDosen1(listDosen.get(0));
            profilMahasiswa.setNamaDosen2("-");
        }        

        String sql3 = """
            SELECT COUNT(*) 
            FROM TopikBimbingan tb
            JOIN PenjadwalanBimbingan pb ON tb.IdBim = pb.IdBim
            JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
            WHERE tb.IdTA = ? AND tb.StatusBimbingan = 'Selesai' 
            AND j.tanggal <= ?
        """;
        Integer praUTS = jdbcTemplate.queryForObject(sql3, Integer.class, profilMahasiswa.getIdTa(), profilMahasiswa.getTanggalUTS());
        if(praUTS != null){
            profilMahasiswa.setJumlahSesiPraUts(praUTS);
        } else {
            profilMahasiswa.setJumlahSesiPraUts(0);
        }

        String sql4 = """
            SELECT COUNT(*) 
            FROM TopikBimbingan tb
            JOIN PenjadwalanBimbingan pb ON tb.IdBim = pb.IdBim
            JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
            WHERE tb.IdTA = ? AND tb.StatusBimbingan = 'Selesai' 
            AND j.tanggal > ?
        """;
        Integer pascaUTS = jdbcTemplate.queryForObject(sql4, Integer.class, profilMahasiswa.getIdTa(), profilMahasiswa.getTanggalUTS());
        if(pascaUTS != null){
            profilMahasiswa.setJumlahSesiPascaUts(pascaUTS);
        } else {
            profilMahasiswa.setJumlahSesiPascaUts(0);
        }
        return profilMahasiswa;
    }

    private TugasAkhir mapRowToTugasAkhir(ResultSet rs, int rowNum) throws SQLException {
        TugasAkhir ta = new TugasAkhir();
        ta.setIdTa(rs.getInt("IdTa"));
        ta.setTopikTA(rs.getString("TopikTA"));
        ta.setTanggalUTS(rs.getDate("TanggalUTS"));
        ta.setTanggalUas(rs.getDate("TanggalUas"));
        int tahap = rs.getInt("TahapTA");
        ta.setTahapSkripsi("Tugas Akhir " + tahap); 
        ta.setSemesterAktif("Ganjil 2025/2026"); //ini hardcode, ntar ganti 
        return ta;
    }

    // private TugasAkhir mapRowToTugasAkhir(ResultSet rs, int rowNum) throws SQLException {
    //     TugasAkhir  ta = new TugasAkhir();
    //     ta.setIdTa(rs.getInt("Idta"));
    //     ta.setTopikTA(rs.getString("tanggal"));
    //     ta.setTanggalUTS(rs.getDate("WaktuMulai"));
    //     ta.setTanggalUas(rs.getDate("WaktuSelesai"));
    //     return ta;
    // }


//    @Override
//    public TugasAkhir getTugasAkhir(String idPengguna) {
//        String sql = "SELECT j.* FROM tugasakhir t " +
//                "JOIN Pengguna pr ON j.IdJadwal = pr.IdJadwal " +
//                "WHERE pr.IdRuangan = ? AND j.tanggal = ?";
//        return jdbcTemplate.query(sql, this::mapRowToJadwal, idRuangan, date);
//        return null;
//    }
}