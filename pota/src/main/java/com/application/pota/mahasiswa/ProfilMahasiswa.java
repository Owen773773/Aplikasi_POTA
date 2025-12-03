package com.application.pota.mahasiswa;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfilMahasiswa {
    private String nama, peran, npm;
    private String syaratKelayakan;
    private int totBimPra, totBimPas;
    private String dosen1, dosen2;
}
