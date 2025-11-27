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
}
