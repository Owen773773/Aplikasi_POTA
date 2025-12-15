package com.application.pota.akademik;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Akademik {
    private int idSemester;
    private  int minimumPra;
    private  int minimumPasca;
}