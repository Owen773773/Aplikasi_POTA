package com.application.pota.tugasakhir;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TugasAkhir {
    private int IdTa;
    private String TopikTA ;
    private LocalDateTime TanggalUTS;
    private LocalDateTime TanggalUas;
    private String IdMahasiswa;
}
