package com.application.pota.mahasiswa;

import lombok.Data;
import java.util.List;

@Data
public class BimbinganRequest {
    private List<String> dosenIds;
    private String tanggal;
    private String waktu;
    private String topik;
    private String deskripsi;
}