package com.application.pota.jadwal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface JadwalRepository {

    /**
     * Mencari jadwal berdasarkan username
     */
    List<Jadwal> findByUsername(String username);

    /**
     * Mencari jadwal berdasarkan tanggal
     */
    List<Jadwal> findByDate(LocalDateTime date);

    /**
     * Mencari jadwal berdasarkan hari tertentu dengan mempertimbangkan pengulangan
     * @param targetDate tanggal yang dicari
     * @return List jadwal yang aktif pada tanggal tersebut
     */
    List<Jadwal> findByDayWithRecurrence(LocalDate targetDate);

    /**
     * Mencari jadwal dalam rentang minggu tertentu
     * @param startOfWeek tanggal mulai minggu (Senin)
     * @param endOfWeek tanggal akhir minggu (Minggu)
     * @return List jadwal dalam minggu tersebut
     */
    List<Jadwal> findByWeekRange(LocalDate startOfWeek, LocalDate endOfWeek);

    /**
     * Mencari semua jadwal
     */
    List<Jadwal> findAll();
}