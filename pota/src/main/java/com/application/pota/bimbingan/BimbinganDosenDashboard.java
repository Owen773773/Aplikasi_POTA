package com.application.pota.bimbingan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BimbinganDosenDashboard {
    private LocalDate DateNow;
    private Time waktuMulai;
    private Time waktuSelesai;
    private String lokasi;
    private String dosen    ;
    private String mahasiswa;
    private int idbim;
}
