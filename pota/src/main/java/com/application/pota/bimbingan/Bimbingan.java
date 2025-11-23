package com.application.pota.bimbingan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bimbingan {
    private String idBim;
    private String DeskripsiBim;
    private String Catatan;
    private String TopikBim;
    private String Status;
    private int JumlahPeserta;
    private int idRuangan;
}