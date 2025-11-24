package com.application.pota.jadwal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jadwal {
    private int IdJadwal;
    private Date tanggal;
    private Time WaktuMulai;
    private Time WaktuSelesai;
    private int berulang;
}
