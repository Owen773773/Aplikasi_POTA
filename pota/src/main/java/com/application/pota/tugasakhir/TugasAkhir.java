package com.application.pota.tugasakhir;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TugasAkhir {
    private int IdTa;
    private String TopikTA ;
    private Date TanggalUTS;
    private Date TanggalUas;

    private String semesterAktif;
    private String tahapSkripsi;
    private String namaDosen1;
    private String namaDosen2;
    private int jumlahSesiPraUts;
    private int jumlahSesiPascaUts;
}
