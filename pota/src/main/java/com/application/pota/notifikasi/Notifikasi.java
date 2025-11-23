package com.application.pota.notifikasi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notifikasi {
    private int idNotifikasi;
    private String tipeNotif;
    private Timestamp waktuAcara;
}
