package com.application.pota.mahasiswa;

import com.application.pota.bimbingan.BimbinganSiapKirim;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataMhs {
    private String semesterAktif;
    private String tahapSkripsi;
    private String judulSkripsi;
    private String dosenPembimbing1;
    private String dosenPembimbing2;
    private int sesiPraUTS;
    private int targetPraUTS;
    private int sesiPascaUTS;
    private int targetPascaUTS;
    
    // Bimbingan Mendatang
    private BimbinganSiapKirim bimbinganMendatang;
}
