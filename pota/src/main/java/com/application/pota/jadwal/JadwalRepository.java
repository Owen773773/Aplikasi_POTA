package com.application.pota.jadwal;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

public interface JadwalRepository {

    // Mencari Jadwal berdasarkan idPengguna dan tanggal
    List<Jadwal> findByIdPengguna(String IdPengguna, Date date);

    // Mencari Jadwal Berdasarkan idRuangan
    List<Jadwal> findByDateByidRuangan(int idRuangan, Date date);

    // Mencari Jadwal Berdasarkan idBimbingan
    List<Jadwal> findByIdbimbingan(String idBim, Date date);

    // Add Jadwal
    void addJadwal(Jadwal targetDate);

    // Remove jadwal
    void removeJadwal(int idJadwal);

    // Cari jadwal dalam 1 minggu by Pengguna (hanya jadwal pribadi)
    List<Jadwal> findByWeekRangePengguna(LocalDate startOfWeek, LocalDate endOfWeek, String IdPengguna);

    // Cari jadwal dalam 1 minggu by Ruangan (pemblokiran)
    List<Jadwal> findByWeekRangeRuangan(LocalDate startOfWeek, LocalDate endOfWeek, int idRuangan);

    // Cari jadwal bimbingan dalam 1 minggu by Ruangan dengan status
    List<JadwalJdbc.JadwalWithStatus> findBimbinganByWeekRangeRuangan(LocalDate startOfWeek, LocalDate endOfWeek, int idRuangan);

    // Cari jadwal bimbingan dalam 1 minggu by Pengguna dengan status
    List<JadwalJdbc.JadwalWithStatus> findBimbinganByWeekRangePengguna(LocalDate startOfWeek, LocalDate endOfWeek, String idPengguna);
}