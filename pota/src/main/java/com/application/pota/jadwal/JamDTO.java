package com.application.pota.jadwal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JamDTO {
    private String jam;
    private String tampilan;

    public JamDTO(String jam, String tampilan) {
        this.jam = jam;
        this.tampilan = tampilan;
    }
}
