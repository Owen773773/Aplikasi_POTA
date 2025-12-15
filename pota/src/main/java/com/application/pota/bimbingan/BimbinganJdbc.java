package com.application.pota.bimbingan;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BimbinganJdbc implements BimbinganRepository {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<BimbinganSiapKirim> getBimbinganUserBertipe(String tipeAkun, String tipeStatus, String idPengguna) {
        if ("Mahasiswa".equalsIgnoreCase(tipeAkun)) {
            return getBimbinganMahasiswaByStatus(tipeStatus, idPengguna);
        } else if ("Dosen".equalsIgnoreCase(tipeAkun)) {
            return getBimbinganDosenByStatus(tipeStatus, idPengguna);
        } else {
            return List.of();
        }
    }

    private List<BimbinganSiapKirim> getBimbinganMahasiswaByStatus(String tipeStatus, String idPengguna) {
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
            tb.statusmhs,
            tb.StatusDosen1,
            tb.StatusDosen2,
            ta.IdTa
        FROM Bimbingan b
        JOIN TopikBimbingan tb ON b.IdBim = tb.IdBim
        JOIN TugasAkhir ta ON tb.IdTA = ta.IdTa
        LEFT JOIN Ruangan r ON b.idRuangan = r.idRuangan
        LEFT JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
        LEFT JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
        WHERE ta.IdMahasiswa = ?
          AND tb.StatusBimbingan = ?
        ORDER BY j.tanggal DESC, j.WaktuMulai DESC
        """;

        return jdbcTemplate.query(sql, this::BimbinganRowMapper, idPengguna, tipeStatus);
    }



    private List<BimbinganSiapKirim> getBimbinganDosenByStatus(String tipeStatus, String idPengguna) {
        String sql = """
        WITH ranked_dosen AS (
            SELECT 
                dp.idTA,
                dp.IdDosen,
                ROW_NUMBER() OVER (PARTITION BY dp.idTA ORDER BY dp.IdDosen) AS rn
            FROM Dosen_Pembimbing dp
        )
        SELECT DISTINCT ON (b.IdBim)
            b.IdBim,
            b.TopikBim,
            b.DeskripsiBim,
            b.Catatan,
            r.namaRuangan,
            j.tanggal,
            j.WaktuMulai,
            j.WaktuSelesai,
            tb.StatusBimbingan,
            tb.statusmhs,
            tb.StatusDosen1,
            tb.StatusDosen2,
            ta.IdTa
        FROM Bimbingan b
        JOIN TopikBimbingan tb ON b.IdBim = tb.IdBim
        JOIN TugasAkhir ta ON tb.IdTA = ta.IdTa
        JOIN ranked_dosen rd ON rd.idTA = ta.IdTa
        LEFT JOIN Ruangan r ON b.idRuangan = r.idRuangan
        LEFT JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
        LEFT JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
        WHERE rd.IdDosen = ?
          AND tb.StatusBimbingan = ?
          AND (
                (rd.rn = 1 AND tb.StatusDosen1 <> 'Tidak Terpilih')
             OR (rd.rn = 2 AND tb.StatusDosen2 <> 'Tidak Terpilih')
          )
        ORDER BY b.IdBim, j.tanggal DESC, j.WaktuMulai DESC
        """;

        return jdbcTemplate.query(sql, this::BimbinganRowMapper, idPengguna, tipeStatus);
    }



    private List<String> getDosenPembimbingTerpilih(int idTa, int idBim) {
        String sql = """
        WITH ranked_dosen AS (
            SELECT 
                dp.idTA,
                p.nama,
                ROW_NUMBER() OVER (PARTITION BY dp.idTA ORDER BY dp.IdDosen) AS rn
            FROM Dosen_Pembimbing dp
            JOIN Pengguna p ON dp.IdDosen = p.IdPengguna
            WHERE dp.idTA = ?
        )
        SELECT rd.nama
        FROM ranked_dosen rd
        JOIN TopikBimbingan tb ON tb.IdTA = rd.idTA AND tb.IdBim = ?
        WHERE 
            (rd.rn = 1 AND tb.StatusDosen1 <> 'Tidak Terpilih')
         OR (rd.rn = 2 AND tb.StatusDosen2 <> 'Tidak Terpilih')
        ORDER BY rd.rn
    """;

        return jdbcTemplate.queryForList(sql, String.class, idTa, idBim);
    }



    public BimbinganSiapKirim BimbinganRowMapper(ResultSet rs, int rowNum) throws SQLException {
        Integer idBim = rs.getInt("IdBim");
        Integer idTa = rs.getInt("IdTa");

        // Pass idBim juga ke method ini
        List<String> dosenList = getDosenPembimbingTerpilih(idTa, idBim);

        String dosen1 = dosenList.size() > 0 ? dosenList.get(0) : null;
        String dosen2 = dosenList.size() > 1 ? dosenList.get(1) : null;

        // Handle jika dosen yang sama
        if (dosen1 != null && dosen1.equals(dosen2)) {
            dosen2 = null;
        }

        String statusmhs = rs.getString("statusmhs");
        String statusDosen1 = rs.getString("StatusDosen1");
        String statusDosen2 = rs.getString("StatusDosen2");

        List<String> mahasiswaList = getMahasiswaBimbingan(idBim);

        return BimbinganSiapKirim.builder()
                .idBimbingan(idBim)
                .topikBimbingan(rs.getString("TopikBim"))
                .deskripsiBimbingan(rs.getString("DeskripsiBim"))
                .namaRuangan(rs.getString("namaRuangan"))
                .DosenBimbingan1(dosen1)
                .DosenBimbingan2(dosen2)
                .statusMhs(statusmhs)
                .statusDosen1(statusDosen1)
                .statusDosen2(statusDosen2)
                .TanggalBimbingan(rs.getDate("tanggal"))
                .waktuMulai(rs.getTime("WaktuMulai"))
                .waktuSelesai(rs.getTime("WaktuSelesai"))
                .listNamaMahasiswa(mahasiswaList)
                .statusBimbingan(rs.getString("StatusBimbingan"))
                .Catatan(rs.getString("Catatan"))
                .build();
    }

    private List<String> getDosenPembimbing(int idTa) {
        String sql = """
                SELECT p.nama
                FROM Dosen_Pembimbing dp
                JOIN Pengguna p ON dp.IdDosen = p.IdPengguna
                WHERE dp.idTA = ?
                ORDER BY dp.IdDosen ASC 
                """;
        return jdbcTemplate.queryForList(sql, String.class, idTa);
    }

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

    @Override
    public void updateStatusDosen1(int idBim, String status) {
        String sql = """
            UPDATE TopikBimbingan
            SET StatusDosen1 = ?
            WHERE IdBim = ?
        """;
        jdbcTemplate.update(sql, status, idBim);
    }

    @Override
    public void updateStatusDosen2(int idBim, String status) {
        String sql = """
            UPDATE TopikBimbingan
            SET StatusDosen2 = ?
            WHERE IdBim = ?
        """;
        jdbcTemplate.update(sql, status, idBim);
    }

    @Override
    public BimbinganDetailStatus getDetailStatusBimbingan(int idBim) {
        // Ambil SEMUA records untuk idBim ini
        String sql = """
        SELECT StatusMhs, StatusDosen1, StatusDosen2, StatusBimbingan
        FROM TopikBimbingan
        WHERE IdBim = ?
    """;

        List<BimbinganDetailStatus> allRecords = jdbcTemplate.query(sql, (rs, rowNum) -> {
            BimbinganDetailStatus status = new BimbinganDetailStatus();
            status.setIdBim(idBim);
            status.setStatusMhs(rs.getString("StatusMhs"));
            status.setStatusDosen1(rs.getString("StatusDosen1"));
            status.setStatusDosen2(rs.getString("StatusDosen2"));
            status.setStatusBimbingan(rs.getString("StatusBimbingan"));
            return status;
        }, idBim);

        if (allRecords.isEmpty()) {
            throw new RuntimeException("Bimbingan dengan ID " + idBim + " tidak ditemukan");
        }

        // Return record pertama (status dosen seharusnya sama untuk semua record dengan IdBim sama)
        return allRecords.get(0);
    }

    @Override
    public List<String> getIdMahasiswaBimbingan(int idBim) {
        String sql = """
        SELECT DISTINCT ta.IdMahasiswa
        FROM TopikBimbingan tb
        JOIN TugasAkhir ta ON tb.IdTA = ta.IdTa
        WHERE tb.IdBim = ?
    """;
        return jdbcTemplate.queryForList(sql, String.class, idBim);
    }

    @Override
    public List<Integer> getIdTaByIdBim(int idBim) {
        String sql = "SELECT IdTA FROM TopikBimbingan WHERE IdBim = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("IdTA"), idBim);
    }

    @Override
    public List<String> getListDosenByIdBim(int idBim) {
        String sql = """
        SELECT dp.IdDosen
        FROM TopikBimbingan tb
        JOIN Dosen_Pembimbing dp ON dp.idTA = tb.IdTA
        WHERE tb.IdBim = ?
        ORDER BY dp.IdDosen
    """;
        return jdbcTemplate.query(sql, (rs, i) -> rs.getString("IdDosen"), idBim);
    }

    @Override
    public void updateStatusMahasiswa(int idBim, String status) {
        String sql = """
            UPDATE TopikBimbingan
            SET StatusMhs = ?
            WHERE IdBim = ?
        """;
        jdbcTemplate.update(sql, status, idBim);
    }

    @Override
    public void updateStatusBimbingan(int idBim, String status) {
        String sql = """
            UPDATE TopikBimbingan
            SET statusbimbingan = ?
            WHERE IdBim = ?
        """;
        jdbcTemplate.update(sql, status, idBim);
    }

    @Override
    public void updateCatatanBimbingan(int idBim, String catatan) {
        String sql = """
            UPDATE bimbingan
            SET catatan = ?
            WHERE IdBim = ?
        """;
        jdbcTemplate.update(sql, catatan, idBim);
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

    private PilihanPengguna mapRowToPilihanPengguna(ResultSet rs, int rowNum) throws SQLException {
        return new PilihanPengguna(
                rs.getString("IdPengguna"),
                rs.getString("nama")
        );
    }

    @Override
    public void updateRuanganBimbingan(int idBim, Integer idRuangan) {
        String sql = """
        UPDATE Bimbingan
        SET idRuangan = ?
        WHERE IdBim = ?
    """;
        jdbcTemplate.update(sql, idRuangan, idBim);
    }

    @Override
    public List<String> getMahasiswaBimbingan(Integer idBim) {
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
                    LEFT JOIN Ruangan r ON b.idRuangan = r.idRuangan
                    JOIN TopikBimbingan tb ON b.IdBim = tb.IdBim
                    JOIN TugasAkhir ta ON tb.IdTA = ta.IdTA
                    JOIN Dosen_Pembimbing dp ON ta.IdTa = dp.idTA
                    JOIN Pengguna p_dosen ON dp.IdDosen = p_dosen.IdPengguna
                    WHERE ta.IdMahasiswa = ?
                        AND (j.tanggal > CURRENT_DATE OR (j.tanggal = CURRENT_DATE AND j.WaktuMulai > CURRENT_TIME))
                        ORDER BY j.tanggal ASC, j.WaktuMulai ASC
                        LIMIT 1
                """;
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

        String mulai = rs.getString("WaktuMulai").substring(0, 5);
        String selesai = rs.getString("WaktuSelesai").substring(0, 5);
        bimbingan.setJam(mulai + " - " + selesai);

        try {
            int angkamulai = Integer.parseInt(mulai.substring(0, 2));
            int angkaselesai = Integer.parseInt(selesai.substring(0, 2));
            bimbingan.setDurasi((angkaselesai - angkamulai) + " Jam");
        } catch (NumberFormatException e) {
            bimbingan.setDurasi("-");
        }

        return bimbingan;
    }

    public int insertBimbingan(String deskripsi, String topik, int jumlahPeserta, Integer idRuangan) {
        String sql = """
                    INSERT INTO Bimbingan(DeskripsiBim, TopikBim, JumlahPeserta, idRuangan)
                    VALUES (?, ?, ?, ?)
                    RETURNING IdBim
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class, deskripsi, topik, jumlahPeserta, idRuangan);
    }

    public void insertPenjadwalanBimbingan(int idJadwal, int idBim) {
        String sql = """
                    INSERT INTO PenjadwalanBimbingan(IdJadwal, IdBim)
                    VALUES (?, ?)
                """;
        jdbcTemplate.update(sql, idJadwal, idBim);
    }
}