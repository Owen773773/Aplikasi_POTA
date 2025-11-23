package com.application.pota.akademik;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Akademik {
    private int idSemester;
    private  int minimumPra;
    private  int minimumPasca;
}