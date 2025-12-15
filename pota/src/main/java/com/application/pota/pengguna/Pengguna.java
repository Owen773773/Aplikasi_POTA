package com.application.pota.pengguna;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pengguna {
    private String IdPengguna;
    private String username;
    private String password;
    private String nama;
    private boolean statusAktif;
    private String tipeAkun;
    private LocalDateTime lastLogin;
}