package com.application.pota.tugasakhir;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class TugasAkhirJdbc implements TugasAkhirRepository {

    private final JdbcTemplate jdbcTemplate;
    private TugasAkhir mapRowToTugasAkhir(ResultSet rs, int rowNum) throws SQLException {
        TugasAkhir  ta = new TugasAkhir();
        ta.setIdTa(rs.getInt("Idta"));
        ta.setTopikTA(rs.getString("tanggal"));
        ta.setTanggalUTS(rs.getDate("WaktuMulai"));
        ta.setTanggalUas(rs.getDate("WaktuSelesai"));
        return ta;
    }
//    @Override
//    public TugasAkhir getTugasAkhir(String idPengguna) {
//        String sql = "SELECT j.* FROM tugasakhir t " +
//                "JOIN Pengguna pr ON j.IdJadwal = pr.IdJadwal " +
//                "WHERE pr.IdRuangan = ? AND j.tanggal = ?";
//        return jdbcTemplate.query(sql, this::mapRowToJadwal, idRuangan, date);
//        return null;
//    }
}