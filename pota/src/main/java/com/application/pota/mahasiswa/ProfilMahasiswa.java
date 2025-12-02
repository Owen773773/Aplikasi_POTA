package com.application.pota.mahasiswa;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.application.pota.dosen.Dosen;

@Data
@NoArgsConstructor
public class ProfilMahasiswa {
    private Mahasiswa mahasiswa;
    private Dosen dosen1, dosen2;
}
