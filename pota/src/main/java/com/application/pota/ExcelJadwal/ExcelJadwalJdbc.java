// package com.application.pota.ExcelJadwal;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.support.GeneratedKeyHolder;
// import org.springframework.jdbc.support.KeyHolder;
// import org.springframework.stereotype.Repository;

// import java.io.FileInputStream;
// import java.io.IOException;
// import java.sql.Date;
// import java.sql.PreparedStatement;
// import java.sql.Statement;
// import java.sql.Time;
// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.ArrayList;
// import java.util.List;

// @Repository
// public class ExcelJadwalJdbc implements ExcelJadwalRepository {
//     @Autowired
//     private JdbcTemplate jdbcTemplate;

//     public List<ExcelJadwal>










//     // 1. Simpan ke tabel Jadwal dan kembalikan ID yang baru dibuat (SERIAL)
//     public int saveJadwal(LocalDate tanggal, LocalTime waktuMulai, LocalTime waktuSelesai) {
//         String sql = "INSERT INTO Jadwal (tanggal, WaktuMulai, WaktuSelesai, berulang) VALUES (?, ?, ?, 0)";
//         KeyHolder keyHolder = new GeneratedKeyHolder();

//         jdbcTemplate.update(connection -> {
//             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//             ps.setDate(1, Date.valueOf(tanggal));
//             ps.setTime(2, Time.valueOf(waktuMulai));
//             ps.setTime(3, Time.valueOf(waktuSelesai));
//             return ps;
//         }, keyHolder);

//         return keyHolder.getKey().intValue();
//     }

//     // 2. Simpan relasi ke tabel Jadwal_Pribadi
//     public void saveJadwalPribadi(int idJadwal, String idPengguna) {
//         String sql = "INSERT INTO Jadwal_Pribadi (IdJadwal, IdPengguna) VALUES (?, ?)";
//         jdbcTemplate.update(sql, idJadwal, idPengguna);
//     }
// }
