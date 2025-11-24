package com.application.pota.jadwal;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

public interface JadwalRepository {

    //Mencari Jadwal berdasarkan idPengguna dan tanggal
    List<Jadwal> findByIdPengguna(String IdPengguna, Date date);

    //mencari Jadwal Berdasarkan idRuangan
    List<Jadwal> findByDateByidRuangan(int idRuangan, Date date);

    //mencari Jadwal Berdasarkan idBimbingan
    List<Jadwal> findByIdbimbingan(String idBim, Date date);

    //Add Jadwal
    void addJadwal(Jadwal targetDate);

    //remove jadwal
    void removeJadwal(int idJadwal);

    //cari jadwal dalam 1 minggu by Pengguna
    List<Jadwal> findByWeekRangePengguna(LocalDate startOfWeek, LocalDate endOfWeek, String IdPengguna);

    //cari jadwal dalam 1 minggu by Ruangan (pemblokiran)
    List<Jadwal> findByWeekRangeRuangan(LocalDate startOfWeek, LocalDate endOfWeek, int idRuangan);

    //cari jadwal bimbingan dalam 1 minggu by Ruangan dengan status
    List<JadwalJdbc.JadwalWithStatus> findBimbinganByWeekRangeRuangan(LocalDate startOfWeek, LocalDate endOfWeek, int idRuangan);
}