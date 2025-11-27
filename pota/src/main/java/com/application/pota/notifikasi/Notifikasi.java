package com.application.pota.notifikasi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notifikasi {
    // private int idNotifikasi;
    // private String tipeNotif;
    // private Timestamp waktuAcara;

    private int idNotifikasi;
    private String tipeNotif;
    private LocalDateTime waktuAcara;
    
    private String infoTanggal;
    private String infoWaktuMulai;
    private String infoWaktuSelesai;
    private String infoRuangan;
    private String catatan;
    private String pesanDiFrontend;

}
