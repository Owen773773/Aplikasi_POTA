package com.application.pota.bimbingan;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@NoArgsConstructor
public class DTOBimbinganMahasiswa {
    private List<String> dosenIds;
    private String tanggal;
    private String waktuMulai;
    private String waktuSelesai;
    private String topik;
    private String deskripsi;
}
