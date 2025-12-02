package com.application.pota.bimbingan;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class BimbinganJdbc implements BimbinganRepository {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public Bimbingan bimbinganTerdekat(String idMahasiswa){
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
                """
        ; //waktu durasi dikurang di java aj
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
}