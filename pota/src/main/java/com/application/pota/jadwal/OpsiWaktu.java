package com.application.pota.jadwal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OpsiWaktu {
    private LocalDate tanggal;
    private String hari;
    private int jamMulai;
    private int jamSelesai;
    private boolean bisa;
}