package com.application.pota.dosen;

import com.application.pota.pengguna.Pengguna;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
class Dosen extends Pengguna {
    public Dosen(String idPengguna, String username, String password,
                 String nama, boolean statusAktif, String tipeAkun,
                 LocalDateTime lastLogin) {
        super(idPengguna, username, password, nama, statusAktif, tipeAkun, lastLogin);
    }
}
