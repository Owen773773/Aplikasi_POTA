package com.application.pota.jadwal;

import java.time.DayOfWeek;

public class SlotWaktu {
    private int jam;
    private DayOfWeek hari;
    private int indeksHari;
    private String status;
    private Integer idBooking;
    private String tipeBooking;
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
    public String getTipeBooking() {
        return tipeBooking;
    }
    public void setTipeBooking(String tipeBooking) {
        this.tipeBooking = tipeBooking;
    }
}