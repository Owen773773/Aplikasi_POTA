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

import com.application.pota.bimbingan.BimbinganSiapKirim;

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
            WHERE IdMahasiswa = ?
            LIMIT 1;
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

    
    private String getJudulSkripsi(String idMhs) {
        String sql = """
                SELECT TopikTA
                FROM TugasAkhir
                WHERE IdMahasiswa = ?
                LIMIT 1 
                """;
        return jdbcTemplate.queryForObject(sql, String.class, idMhs);
    }

    private String getTahapSkripsi(String idMhs) {
            String sql = """
                SELECT TahapTA
                FROM Mahasiswa
                WHERE IdPengguna = ?
            """;
        int tahap = jdbcTemplate.queryForObject(sql, Integer.class, idMhs);
        if(tahap == 1){
            return "Tugas Akhir I (TA 1)";
        } else{
            return "Tugas Akhir II (TA 2)";
        }
    }

    
    public String getNamaSemester(String idMhs){
        String sql = """
            SELECT ta.idakademik
            FROM Pengguna p 
            JOIN TugasAkhir t on p.IdPengguna = t.idMahasiswa
            JOIN TAtermasukAkademik ta on t.idTa = ta.idTa 
            WHERE p.IdPengguna = ?
            LIMIT 1						
                """;
        int tahap =  jdbcTemplate.queryForObject(sql, Integer.class, idMhs);
        int thn = tahap/10;
        int smster = tahap%10;
        String hasil = "";
        if(smster == 1){
            hasil += "Ganjil "; 
        } else if(smster == 2){
            hasil += "Genap ";
        } else{
            hasil += "Pendek ";
        }
        int thn2 = thn+1;
        hasil += thn + "/" + thn2;
        return hasil;
    }

    public DashboardDataMhs getDashboardDataMhs(String idMhs){
        DashboardDataMhs dashboardDataMhs = new DashboardDataMhs();
        ProfilMahasiswa profil = makeProfileByIdPengguna(idMhs);
        dashboardDataMhs.setDosenPembimbing1(profil.getDosen1() != null ? profil.getDosen1() : "-");
        dashboardDataMhs.setDosenPembimbing2(profil.getDosen2() != null ? profil.getDosen2() : "-");
        dashboardDataMhs.setSesiPraUTS(profil.getTotBimPra());
        dashboardDataMhs.setSesiPascaUTS(profil.getTotBimPas());
        List<Integer> batas = getBatasKelayakanPraPasca(idMhs);
        dashboardDataMhs.setTargetPraUTS(batas.get(0));
        dashboardDataMhs.setTargetPascaUTS(batas.get(1));
        dashboardDataMhs.setSemesterAktif(getNamaSemester(idMhs));
        dashboardDataMhs.setTahapSkripsi(getTahapSkripsi(idMhs));
        dashboardDataMhs.setJudulSkripsi(getJudulSkripsi(idMhs));
        
        return dashboardDataMhs;
    }

    public BimbinganSiapKirim getBimbinganMendatang(String idMhs){
        String query = """
            SELECT 
                b.IdBim, b.TopikBim, b.DeskripsiBim, b.Catatan,
                j.tanggal, j.WaktuMulai, j.WaktuSelesai,
                r.namaRuangan
            FROM Bimbingan b
            JOIN TopikBimbingan tb ON b.IdBim = tb.IdBim
            JOIN TugasAkhir ta ON tb.IdTA = ta.IdTa
            JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
            JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
            JOIN Ruangan r ON b.idRuangan = r.idRuangan
            WHERE ta.IdMahasiswa = ?
            AND tb.StatusBimbingan = 'Terjadwalkan'
            AND j.tanggal >= CURRENT_DATE
            AND j.WaktuMulai >= CURRENT_TIME
            ORDER BY j.tanggal, j.WaktuMulai
            LIMIT 1
            """;
        try {
            BimbinganSiapKirim bim = jdbcTemplate.queryForObject(query,this::mapRowToBimbinganSiapKirim, idMhs);
            ProfilMahasiswa profil = makeProfileByIdPengguna(idMhs);
            bim.setDosenBimbingan1(profil.getDosen1());
            bim.setDosenBimbingan2(profil.getDosen2());
            
            return bim;
        } catch (Exception e) {
            return null; // Tidak ada bimbingan mendatang
        }
    }

    public BimbinganSiapKirim mapRowToBimbinganSiapKirim(ResultSet rs, int rowNum) throws SQLException {
        BimbinganSiapKirim bimbinganSiapKirim = new BimbinganSiapKirim();
        bimbinganSiapKirim.setIdBimbingan(rs.getInt("IdBim"));
        bimbinganSiapKirim.setTopikBimbingan(rs.getString("TopikBim"));
        bimbinganSiapKirim.setDeskripsiBimbingan(rs.getString("DeskripsiBim"));
        
        bimbinganSiapKirim.setTanggalBimbingan(rs.getDate("tanggal"));
        bimbinganSiapKirim.setWaktuMulai(rs.getTime("WaktuMulai"));
        bimbinganSiapKirim.setWaktuSelesai(rs.getTime("WaktuSelesai"));
        bimbinganSiapKirim.setNamaRuangan(rs.getString("namaRuangan"));
        bimbinganSiapKirim.setStatusBimbingan("Terjadwalkan");
        return bimbinganSiapKirim;
    }
}