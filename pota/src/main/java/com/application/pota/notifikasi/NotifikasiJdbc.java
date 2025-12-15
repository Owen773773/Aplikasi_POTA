package com.application.pota.notifikasi;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotifikasiJdbc implements NotifikasiRepository {

    private final JdbcTemplate jdbcTemplate;
    
    public Notifikasi getById(int id){ //bisi perlu
        String querySql = """
                        SELECT *
                        FROM Notifikasi
                        WHERE idNotifikasi = ?
                        """
        ;
        return jdbcTemplate.queryForObject(querySql, this::mapRowToNotifikasi, id);
    }

    @Override
    public List<Notifikasi> getNotifikasiLengkapByUsername(String username) {
        String querySql = """
            SELECT DISTINCT
            n.idNotifikasi, n.tipeNotif, n.waktuAcara, b.catatan,
            j.tanggal, j.WaktuMulai, j.WaktuSelesai,
            r.namaRuangan
        FROM Notifikasi n
        JOIN BimbinganNotifikasi bn ON n.idNotifikasi = bn.IdNotifikasi
        JOIN Bimbingan b ON bn.IdBim = b.IdBim
        LEFT JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
        LEFT JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
        LEFT JOIN Ruangan r ON b.idRuangan = r.idRuangan
        LEFT JOIN MahasiswaNotifikasi mn ON n.idNotifikasi = mn.IdNotifikasi
        LEFT JOIN DosenNotifikasi dn ON n.idNotifikasi = dn.IdNotifikasi
        WHERE EXISTS (
            SELECT 1 FROM Pengguna p 
            WHERE p.username = ? 
            AND (p.IdPengguna = mn.IdPengguna OR p.IdPengguna = dn.IdPengguna)
        )
        ORDER BY n.waktuAcara DESC
            """
        ;
        return jdbcTemplate.query(querySql, this::mapRowToNotifikasi, username);
    }
    
    @Override
    public List<Notifikasi> getNotifikasiLengkapByIdPengguna(String idPengguna) {
        String querySql = """
            SELECT DISTINCT
            n.idNotifikasi, n.tipeNotif, n.waktuAcara, b.catatan,
            j.tanggal, j.WaktuMulai, j.WaktuSelesai,
            r.namaRuangan
        FROM Notifikasi n
        JOIN BimbinganNotifikasi bn ON n.idNotifikasi = bn.IdNotifikasi
        JOIN Bimbingan b ON bn.IdBim = b.IdBim
        LEFT JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
        LEFT JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
        LEFT JOIN Ruangan r ON b.idRuangan = r.idRuangan
        WHERE n.idNotifikasi IN (
            SELECT IdNotifikasi FROM MahasiswaNotifikasi WHERE IdPengguna = ?
            UNION
            SELECT IdNotifikasi FROM DosenNotifikasi WHERE IdPengguna = ?
        )
        ORDER BY n.waktuAcara DESC
            """
        ;
        return jdbcTemplate.query(querySql, this::mapRowToNotifikasi, idPengguna, idPengguna);
    }

    private Notifikasi mapRowToNotifikasi(ResultSet rs, int rowNum) throws SQLException {
        Notifikasi notif = new Notifikasi();
        try {
            notif.setIdNotifikasi(rs.getInt("idNotifikasi"));
            notif.setTipeNotif(rs.getString("tipeNotif"));
            notif.setWaktuAcara(rs.getTimestamp("waktuAcara").toLocalDateTime());
            notif.setCatatan(rs.getString("Catatan"));
            String tgl = rs.getString("tanggal"); 
            if (tgl != null) {
                notif.setInfoTanggal(tgl);
                notif.setInfoRuangan(rs.getString("namaRuangan"));
                notif.setInfoWaktuMulai(rs.getString("WaktuMulai"));
                notif.setInfoWaktuSelesai(rs.getString("WaktuSelesai"));
            }
        } catch (SQLException e) {

        }

        return notif;
    }

    @Override
    public Integer insertNotifikasi(String tipe) {
        String sql = """
            INSERT INTO Notifikasi(tipeNotif,waktuacara)
            VALUES (?, now())
            RETURNING idNotifikasi
        """;

        return jdbcTemplate.queryForObject(sql, Integer.class, tipe);
    }

    @Override
    public void insertMahasiswaNotifikasi(String idMhs, int idNotif) {
        String sql = """
            INSERT INTO mahasiswanotifikasi(idpengguna, idnotifikasi)
            VALUES (?, ?)
        """;

        jdbcTemplate.update(sql, idMhs, idNotif);
    }
    @Override
    public void insertBimbinganNotifikasi(int idNotif, int idBim) {
        String sql = """
            INSERT INTO bimbingannotifikasi(idnotifikasi, idbim)
            VALUES (?, ?)
        """;

        jdbcTemplate.update(sql, idNotif, idBim);
    }
    @Override
    public void insertDosenNotifikasi(String idDosen, int idNotif) {
        String sql = """
        INSERT INTO dosennotifikasi(IdPengguna, IdNotifikasi)
        VALUES (?, ?)
    """;

        jdbcTemplate.update(sql, idDosen, idNotif);
    }

}