package com.application.pota.bimbingan;

import lombok.Data;
import java.util.List;

@Data
public class DTOBimbinganDosen {
    private List<String> mahasiswaIds;
    private String tanggal;
    private String waktuMulai;
    private String waktuSelesai;
    private String topik;
    private String deskripsi;
    private Integer idRuangan;
}