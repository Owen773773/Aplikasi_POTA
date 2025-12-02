 package com.application.pota.bimbingan;

 import lombok.RequiredArgsConstructor;

 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.time.LocalDate;
 import java.time.LocalTime;
 import java.util.List;

 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.jdbc.core.JdbcTemplate;
 import org.springframework.jdbc.core.RowMapper;
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

//     //jgn lupaa ganti return type
//     public void getTipeAkunByIdPengguna(String idPengguna, Bimbingan bimbingan, LocalDate tanggal, LocalTime waktuMulai, LocalTime waktuSelesai, Integer idRuangan){
//         String tipeAkunQuery = """
//                     SELECT tipeAkun
//                     FROM Pengguna
//                     WHERE IdPengguna ILIKE ?
//                 """;
//         String tipeAkun = jdbcTemplate.queryForObject(tipeAkunQuery, String.class, idPengguna);
//         String sqlQuery="";
//         if("Mahasiswa".equalsIgnoreCase(tipeAkun)){
//             masukkanBimbinganBaruMahasiswa(idPengguna, bimbingan, tanggal, waktuMulai, waktuSelesai, idRuangan);
//         } else if ("Dosen".equalsIgnoreCase(tipeAkun)){
//             masukkanBimbinganBaruDosen(idPengguna, bimbingan, tanggal, waktuMulai, waktuSelesai, idRuangan);
//         } else{
//             throw new IllegalArgumentException("Tipe akun tidak valid");
//         }
//     }
//
//     public boolean masukkanBimbinganBaruDosen(String idPengguna, Bimbingan bimbingan, LocalDate tanggal, LocalTime waktuMulai, LocalTime waktuSelesai, Integer idRuangan){
//         String sqlMaxId = "SELECT MAX(IdBim) FROM Bimbingan";
//         Integer maxId = jdbcTemplate.queryForObject(sqlMaxId, Integer.class);
//
//         String sqlQuery = """
//                         INSERT INTO Bimbingan
//                         """;
//         return;
//
//     }
//     public boolean masukkanBimbinganBaruMahasiswa(String idPengguna, Bimbingan bimbingan, LocalDate tanggal, LocalTime waktuMulai, LocalTime waktuSelesai, Integer idRuangan){
//         String sqlQuery = "INSERT INTO ";
//
//

//     @Override
//     public List<BimbinganSiapKirim> getBimbinganUserBertipe(String tipeStatus, String idPengguna) {
//         // Cek tipe akun pengguna
//         String tipeAkunQuery = """
//                SELECT tipeAkun
//                FROM Pengguna
//                WHERE IdPengguna ILIKE ?
//            """;
//         String tipeAkun = jdbcTemplate.queryForObject(tipeAkunQuery, String.class, idPengguna);
//
//         // Sesuaikan query berdasarkan tipe akun
//         if ("Mahasiswa".equalsIgnoreCase(tipeAkun)) {
//             return getBimbinganMahasiswaByStatus(tipeStatus, idPengguna);
//         } else if ("Dosen".equalsIgnoreCase(tipeAkun)) {
//             return getBimbinganDosenByStatus(tipeStatus, idPengguna);
//         } else {
//             throw new IllegalArgumentException("Tipe akun tidak valid atau tidak memiliki bimbingan");
//         }
//     }

     @Override
     public List<BimbinganSiapKirim> getBimbinganUserBertipe(String tipeAkun, String tipeStatus, String idPengguna) {

         // Langsung menggunakan parameter tipeAkun
         if ("Mahasiswa".equalsIgnoreCase(tipeAkun)) {
             return getBimbinganMahasiswaByStatus(tipeStatus, idPengguna);
         } else if ("Dosen".equalsIgnoreCase(tipeAkun)) {
             return getBimbinganDosenByStatus(tipeStatus, idPengguna);
         } else {
             // Admin atau tipe akun lain yang tidak memiliki list bimbingan
             // Kita kembalikan list kosong
             return List.of();
         }
     }

     private List<BimbinganSiapKirim> getBimbinganMahasiswaByStatus(String tipeStatus, String idPengguna) {
         // ... (sama seperti sebelumnya)
         String sql = """
            SELECT 
                b.IdBim,
                b.TopikBim,
                b.DeskripsiBim,
                b.Catatan,
                r.namaRuangan,
                j.tanggal,
                j.WaktuMulai,
                j.WaktuSelesai,
                tb.StatusBimbingan,
                ta.IdTa
            FROM Bimbingan b
            JOIN TopikBimbingan tb ON b.IdBim = tb.IdBim
            JOIN TugasAkhir ta ON tb.IdTA = ta.IdTa
            LEFT JOIN Ruangan r ON b.idRuangan = r.idRuangan
            LEFT JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
            LEFT JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
            WHERE ta.IdMahasiswa ILIKE ? AND tb.StatusBimbingan ILIKE ?
            ORDER BY j.tanggal DESC, j.WaktuMulai DESC
            """;

         return jdbcTemplate.query(sql, new BimbinganRowMapper(), idPengguna, tipeStatus);
     }

     private List<BimbinganSiapKirim> getBimbinganDosenByStatus(String tipeStatus, String idPengguna) {
         // ... (sama seperti sebelumnya)
         String sql = """
            SELECT 
                b.IdBim,
                b.TopikBim,
                b.DeskripsiBim,
                b.Catatan,
                r.namaRuangan,
                j.tanggal,
                j.WaktuMulai,
                j.WaktuSelesai,
                tb.StatusBimbingan,
                ta.IdTa
            FROM Bimbingan b
            JOIN TopikBimbingan tb ON b.IdBim = tb.IdBim
            JOIN TugasAkhir ta ON tb.IdTA = ta.IdTa
            JOIN Dosen_Pembimbing dp ON ta.IdTa = dp.idTA
            LEFT JOIN Ruangan r ON b.idRuangan = r.idRuangan
            LEFT JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
            LEFT JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
            WHERE dp.IdDosen ILIKE ? AND tb.StatusBimbingan ILIKE ?
            ORDER BY j.tanggal DESC, j.WaktuMulai DESC
            """;

         return jdbcTemplate.query(sql, new BimbinganRowMapper(), idPengguna, tipeStatus);
     }

     private class BimbinganRowMapper implements RowMapper<BimbinganSiapKirim> {
         @Override
         public BimbinganSiapKirim mapRow(ResultSet rs, int rowNum) throws SQLException {
             Integer idBim = rs.getInt("IdBim");
             Integer idTa = rs.getInt("IdTa");

             List<String> dosenList = getDosenPembimbing(idTa);
             String dosen1 = dosenList.size() > 0 ? dosenList.get(0) : null;
             String dosen2 = dosenList.size() > 1 ? dosenList.get(1) : null;

             // Ambil daftar mahasiswa yang mengikuti bimbingan ini
             List<String> mahasiswaList = getMahasiswaBimbingan(idBim);

             return BimbinganSiapKirim.builder()
                     .idBimbingan(idBim)
                     .topikBimbingan(rs.getString("TopikBim"))
                     .deskripsiBimbingan(rs.getString("DeskripsiBim"))
                     .namaRuangan(rs.getString("namaRuangan"))
                     .DosenBimbingan1(dosen1)
                     .DosenBimbingan2(dosen2)
                     .TanggalBimbingan(rs.getDate("tanggal"))
                     .waktuMulai(rs.getTime("WaktuMulai"))
                     .waktuSelesai(rs.getTime("WaktuSelesai"))
                     .listNamaMahasiswa(mahasiswaList)
                     .statusBimbingan(rs.getString("StatusBimbingan"))
                     .build();
         }
     }
     private List<String> getDosenPembimbing(Integer idTa) {
         String sql = """
            SELECT p.nama
            FROM Dosen_Pembimbing dp
            JOIN Pengguna p ON dp.IdDosen = p.IdPengguna
            WHERE dp.idTA = ?
            ORDER BY dp.IdDosen
            """;
         return jdbcTemplate.queryForList(sql, String.class, idTa);
     }

     private List<String> getMahasiswaBimbingan(Integer idBim) {
         String sql = """
            SELECT DISTINCT p.nama
            FROM TopikBimbingan tb
            JOIN TugasAkhir ta ON tb.IdTA = ta.IdTa
            JOIN Pengguna p ON ta.IdMahasiswa = p.IdPengguna
            WHERE tb.IdBim = ?
            """;
         return jdbcTemplate.queryForList(sql, String.class, idBim);
     }
 }