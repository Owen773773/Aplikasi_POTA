package com.application.pota.bimbingan;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BimbinganRepository {
    List<BimbinganSiapKirim> getBimbinganUserBertipe(String tipeAkun, String tipeStatus, String idPengguna);}