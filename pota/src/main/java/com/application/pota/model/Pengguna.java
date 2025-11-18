package com.application.pota.model;

import java.util.Date;

// Mengikuti diagram kelas, ini adalah interface.
// Namun, kita akan buat class PenggunaImpl yang mengimplementasikannya untuk data.
public interface Pengguna {
    String getIdPengguna();
    void setIdPengguna(String id);
    String getUsername();
    void setUsername(String username);
    String getPassword();
    void setPassword(String password);
    String getNama();
    void setNama(String nama);
    boolean isStatusAktif();
    void setStatusAktif(boolean status);
    String getTipeAkun();
    void setTipeAkun(String tipe);
    Date getLastLogin();
    void setLastLogin(Date lastLogin);
}