package com.application.pota.bimbingan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BimbinganDetailStatus {
    private Integer idBim;
    private String statusMhs;
    private String statusDosen1;
    private String statusDosen2;
    private String statusBimbingan;
    private String peranPengguna;
    private String idPengguna;

    public boolean isSudahTerima() {
        switch (peranPengguna) {
            case "mahasiswa":
                return "Menyetujui".equals(statusMhs);
            case "dosen1":
                return "Menyetujui".equals(statusDosen1);
            case "dosen2":
                return "Menyetujui".equals(statusDosen2);
            default:
                return false;
        }
    }
}