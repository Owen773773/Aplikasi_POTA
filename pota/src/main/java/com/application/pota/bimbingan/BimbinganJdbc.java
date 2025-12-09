package com.application.pota.bimbingan;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    private List<String> getDosenPembimbing(int idTa) {
        String sql = """
                SELECT p.nama
                FROM Dosen_Pembimbing dp
                JOIN Pengguna p ON dp.IdDosen = p.IdPengguna
                WHERE dp.idTA = ?
                ORDER BY dp.IdDosen
                """;
        return jdbcTemplate.queryForList(sql, String.class, idTa);
    }

    // Get dosen pembimbing untuk satu TA tertentu
    @Override
    public List<PilihanPengguna> getDosenPembimbingPilihan(int idTa) {
        String sql = """
                SELECT p.IdPengguna, p.nama
                FROM Dosen_Pembimbing dp
                JOIN Pengguna p ON dp.IdDosen = p.IdPengguna
                WHERE dp.idTA = ?
                ORDER BY dp.IdDosen
                """;
        return jdbcTemplate.query(sql, this::mapRowToPilihanPengguna, idTa);
    }

    // Get semua mahasiswa yang dibimbing oleh dosen tertentu
    @Override
    public List<PilihanPengguna> getMahasiswaPilihan(String idDosen) {
        String sql = """
                SELECT DISTINCT p.IdPengguna, p.nama
                FROM Dosen_Pembimbing dp
                JOIN TugasAkhir ta ON dp.idTA = ta.IdTa
                JOIN Pengguna p ON ta.IdMahasiswa = p.IdPengguna
                WHERE dp.IdDosen = ?
                ORDER BY p.nama
                """;
        return jdbcTemplate.query(sql, this::mapRowToPilihanPengguna, idDosen);
    }

    public void insertJadwalBimbingan(int idJadwal) {
        String sql = """
        INSERT INTO Jadwal_Bimbingan(IdJadwal)
        VALUES (?)
    """;

        jdbcTemplate.update(sql, idJadwal);
    }
    public void insertTopikBimbingan(
            int idBim,
            int idTA,
            String statusMhs,
            String statusDosen1,
            String statusDosen2,
            String statusBimbingan
    ) {
        String sql = """
        INSERT INTO TopikBimbingan(
            IdBim, IdTA, StatusMhs, StatusDosen1, StatusDosen2, StatusBimbingan
        )
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        jdbcTemplate.update(sql, idBim, idTA, statusMhs, statusDosen1, statusDosen2, statusBimbingan);
    }

    // RowMapper untuk PilihanPengguna
    private PilihanPengguna mapRowToPilihanPengguna(ResultSet rs, int rowNum) throws SQLException {
        return new PilihanPengguna(
                rs.getString("IdPengguna"),
                rs.getString("nama")
        );
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

    public Bimbingan bimbinganTerdekat(String idMahasiswa) {
        String sql = """
                SELECT 
                    j.tanggal, j.WaktuMulai, j.WaktuSelesai, r.namaRuangan, p_dosen.nama AS namaDosen,
                    b.IdBim, b.TopikBim, b.DeskripsiBim
                FROM 
                    Bimbingan b
                    JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
                    JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
                    JOIN Ruangan r ON b.idRuangan = r.idRuangan
                    JOIN TopikBimbingan tb ON b.IdBim = tb.IdBim
                    JOIN TugasAkhir ta ON tb.IdTA = ta.IdTA
                    JOIN Dosen_Pembimbing dp ON ta.IdTa = dp.idTA
                    JOIN Pengguna p_dosen ON dp.IdDosen = p_dosen.IdPengguna
                    WHERE ta.IdMahasiswa = ?
                        AND (j.tanggal > CURRENT_DATE OR (j.tanggal = CURRENT_DATE AND j.WaktuMulai > CURRENT_TIME))
                        ORDER BY j.tanggal ASC, j.WaktuMulai ASC
                        LIMIT 1
                """; //waktu durasi dikurang di java aj
        List<Bimbingan> hasil = jdbcTemplate.query(sql, this::mapRowToBimbingan, idMahasiswa);
        if (hasil.isEmpty()) {
            return null;
        } else {
            return hasil.get(0);
        }
    }

    private Bimbingan mapRowToBimbingan(ResultSet rs, int rowNum) throws SQLException {
        Bimbingan bimbingan = new Bimbingan();
        bimbingan.setIdBim(rs.getString("IdBim"));
        bimbingan.setTopikBim(rs.getString("TopikBim"));
        bimbingan.setDeskripsiBim(rs.getString("DeskripsiBim"));
        bimbingan.setNamaRuangan(rs.getString("namaRuangan"));
        bimbingan.setNamaDosen(rs.getString("namaDosen"));
        bimbingan.setTanggal(rs.getString("tanggal"));

        // Logika Waktu & Durasi
        String mulai = rs.getString("WaktuMulai").substring(0, 5); // Ambil 10:00
        String selesai = rs.getString("WaktuSelesai").substring(0, 5); // Ambil 12:00
        bimbingan.setJam(mulai + " - " + selesai);

        // Hitung durasi
        try {
            int angkamulai = Integer.parseInt(mulai.substring(0, 2));
            int angkaselesai = Integer.parseInt(selesai.substring(0, 2));
            bimbingan.setDurasi((angkaselesai - angkamulai) + " Jam");
        } catch (NumberFormatException e) {
            bimbingan.setDurasi("-"); // Jaga-jaga kalau error parsing
        }

        return bimbingan;
    }

    public int insertBimbingan(String deskripsi, String topik, int jumlahPeserta, Integer idRuangan) {
        String sql = """
                    INSERT INTO Bimbingan(DeskripsiBim, TopikBim, JumlahPeserta, idRuangan)
                    VALUES (?, ?, ?, ?)
                    RETURNING IdBim
                """;

        return jdbcTemplate.queryForObject(sql, Integer.class,
                deskripsi, topik, jumlahPeserta, idRuangan);
    }

    public void insertPenjadwalanBimbingan(int idJadwal, int idBim) {
        String sql = """
                    INSERT INTO PenjadwalanBimbingan(IdJadwal, IdBim)
                    VALUES (?, ?)
                """;

        jdbcTemplate.update(sql, idJadwal, idBim);
    }

}