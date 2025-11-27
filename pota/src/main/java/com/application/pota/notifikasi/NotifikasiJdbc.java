package com.application.pota.notifikasi;

import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class NotifikasiJdbc implements NotifikasiRepository {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    
    public Notifikasi getById(int id){
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
            SELECT 
                n.idNotifikasi, n.tipeNotif, n.waktuAcara, b.catatan,
                j.tanggal, j.WaktuMulai, j.WaktuSelesai,
                r.namaRuangan
            FROM Bimbingan b
            JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
            JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
            JOIN Ruangan r ON b.idRuangan = r.idRuangan

            JOIN BimbinganNotifikasi bn ON b.IdBim = bn.IdBim
            JOIN Notifikasi n ON bn.IdNotifikasi = n.idNotifikasi

            JOIN MahasiswaNotifikasi mn ON n.idNotifikasi = mn.IdNotifikasi
            JOIN DosenNotifikasi dn ON n.idNotifikasi = dn.IdNotifikasi
            JOIN Pengguna p ON (
                p.IdPengguna = mn.IdPengguna
                OR 
                p.IdPengguna = dn.IdPengguna
            )
            WHERE p.username = ?
            """
        ;
        return jdbcTemplate.query(querySql, this::mapRowToNotifikasi, username);
    }
    
    @Override
    public List<Notifikasi> getNotifikasiLengkapByIdPengguna(String idPengguna) {
        String querySql = """
            SELECT 
                n.idNotifikasi, n.tipeNotif, n.waktuAcara, b.catatan,
                j.tanggal, j.WaktuMulai, j.WaktuSelesai,
                r.namaRuangan
            FROM Bimbingan b
            JOIN PenjadwalanBimbingan pb ON b.IdBim = pb.IdBim
            JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
            JOIN Ruangan r ON b.idRuangan = r.idRuangan

            JOIN BimbinganNotifikasi bn ON b.IdBim = bn.IdBim
            JOIN Notifikasi n ON bn.IdNotifikasi = n.idNotifikasi

            JOIN MahasiswaNotifikasi mn ON n.idNotifikasi = mn.IdNotifikasi
            JOIN DosenNotifikasi dn ON n.idNotifikasi = dn.IdNotifikasi
            JOIN Pengguna p ON (
                p.IdPengguna = mn.IdPengguna
                OR 
                p.IdPengguna = dn.IdPengguna
            )
            WHERE p.idPengguna = ?
            """
        ;
        return jdbcTemplate.query(querySql, this::mapRowToNotifikasi, idPengguna);
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
    public void buatNotifikasiBaru(Notifikasi notif, String username) {
        String sqlNotifikasi = "INSERT INTO Notifikasi (tipeNotif, waktuAcara) VALUES (?, ?) RETURNING idNotifikasi";//returning untuk mengambil index berdasarkan yg baru di insert ataupun delete
        int newId = jdbcTemplate.queryForObject(sqlNotifikasi, Integer.class, notif.getTipeNotif(), notif.getWaktuAcara());

        String sqlCekRole = "SELECT tipeAkun FROM Pengguna WHERE username = ?";
        String role = jdbcTemplate.queryForObject(sqlCekRole, String.class, username);
        
        String sqlAmbilIdPengguna = "SELECT IdPengguna FROM Pengguna WHERE username = ?";
        String idPengguna = jdbcTemplate.queryForObject(sqlAmbilIdPengguna, String.class, username);

        if ("Mahasiswa".equalsIgnoreCase(role)) {
            String sqlRelasi = "INSERT INTO MahasiswaNotifikasi (IdPengguna, IdNotifikasi) VALUES (?, ?)";
            jdbcTemplate.update(sqlRelasi, idPengguna, newId);
        } else if ("Dosen".equalsIgnoreCase(role)) {
            String sqlRelasi = "INSERT INTO DosenNotifikasi (IdPengguna, IdNotifikasi) VALUES (?, ?)";
            jdbcTemplate.update(sqlRelasi, idPengguna, newId);
        }
    }
}