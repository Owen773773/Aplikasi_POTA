package com.application.pota.bimbingan;

import com.application.pota.mahasiswa.Mahasiswa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

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
