package com.application.pota.jadwal;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface JadwalRepository {
    List<Jadwal> findByIdPengguna(String IdPengguna, Date date);
    int insertJadwal(LocalDate tanggal, LocalTime mulai, LocalTime selesai);
    List<Jadwal> findByIdbimbingan(String idBim, Date date);
    void addJadwal(Jadwal targetDate);
    void removeJadwal(int idJadwal);
    List<Jadwal> findByWeekRangePengguna(LocalDate startOfWeek, LocalDate endOfWeek, String IdPengguna);
    List<JadwalJdbc.JadwalWithStatus> findBimbinganByWeekRangeRuangan(LocalDate startOfWeek, LocalDate endOfWeek, int idRuangan);
    List<JadwalJdbc.JadwalWithStatus> findBimbinganByWeekRangePengguna(LocalDate startOfWeek, LocalDate endOfWeek, String idPengguna);
}