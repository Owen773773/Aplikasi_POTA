package com.application.pota.bimbingan;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BimbinganRepository {
    Bimbingan bimbinganTerdekat(String idMahasiswa);
    List<BimbinganSiapKirim> getBimbinganUserBertipe(String tipeAkun, String tipeStatus, String idPengguna);

}
