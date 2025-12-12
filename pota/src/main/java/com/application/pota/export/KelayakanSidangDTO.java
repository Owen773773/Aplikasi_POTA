package com.application.pota.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KelayakanSidangDTO {
    private String nama;
    private String topik;
    private int praUts;
    private int pascaUts;
    private String kelayakan;
}