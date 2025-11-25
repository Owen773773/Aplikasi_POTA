package com.application.pota.jadwal;

import java.time.DayOfWeek;

/**
 * Representasi satu slot waktu dalam timetable
 *
 * Setiap slot mewakili 1 jam pada 1 hari tertentu
 * Contoh: Senin jam 08:00, Selasa jam 14:00, dst
 */
public class SlotWaktu {
    private int jam;                  // Jam berapa (0-23)
    private DayOfWeek hari;          // Hari apa (MONDAY, TUESDAY, dst)
    private int indeksHari;          // Index hari (0=Senin, 1=Selasa, dst)
    private String status;           // Status slot: "available", "blocked", "scheduled", "pending", "occupied"
    private Integer idBooking;       // ID jadwal jika slot terisi
    private String tipeBooking;      // Tipe booking: "PEMBLOKIRAN", "BIMBINGAN", "PRIBADI"

    // Getters and Setters
    public int getJam() {
        return jam;
    }

    public void setJam(int jam) {
        this.jam = jam;
    }

    public DayOfWeek getHari() {
        return hari;
    }

    public void setHari(DayOfWeek hari) {
        this.hari = hari;
    }

    public int getIndeksHari() {
        return indeksHari;
    }

    public void setIndeksHari(int indeksHari) {
        this.indeksHari = indeksHari;
    }

    /**
     * Status slot waktu:
     * - "available": Slot kosong, bisa dibooking
     * - "blocked": Slot diblokir/tidak tersedia
     * - "scheduled": Bimbingan yang sudah terkonfirmasi
     * - "pending": Bimbingan yang masih menunggu konfirmasi
     * - "occupied": Slot terisi (jadwal pribadi)
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(Integer idBooking) {
        this.idBooking = idBooking;
    }

    /**
     * Tipe booking:
     * - "PEMBLOKIRAN": Ruangan diblokir
     * - "BIMBINGAN": Jadwal bimbingan
     * - "PRIBADI": Jadwal pribadi pengguna
     */
    public String getTipeBooking() {
        return tipeBooking;
    }

    public void setTipeBooking(String tipeBooking) {
        this.tipeBooking = tipeBooking;
    }
}