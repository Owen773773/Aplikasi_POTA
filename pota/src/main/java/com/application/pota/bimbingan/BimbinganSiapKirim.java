package com.application.pota.bimbingan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BimbinganSiapKirim {
    private Integer idBimbingan;
    private String topikBimbingan;
    private String deskripsiBimbingan;
    private String namaRuangan;
    private String DosenBimbingan1;
    private String DosenBimbingan2;
    private Date TanggalBimbingan;
    private Time waktuMulai;
    private Time waktuSelesai;
    private List<String> listNamaMahasiswa;
    private String statusBimbingan;

    // Helper methods untuk Thymeleaf

    /**
     * Format tanggal untuk display (dd/MM/yyyy)
     */
    public String getTanggal() {
        if (TanggalBimbingan == null) return "-";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(TanggalBimbingan);
    }

    /**
     * Format jam untuk display (HH:mm - HH:mm)
     */
    public String getJam() {
        if (waktuMulai == null || waktuSelesai == null) return "-";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(waktuMulai) + " - " + sdf.format(waktuSelesai);
    }

    /**
     * Hitung durasi dalam format yang readable (misal: "1 Jam", "2 Jam 30 Menit")
     */
    public String getDurasi() {
        if (waktuMulai == null || waktuSelesai == null) return "-";

        long diffInMillies = waktuSelesai.getTime() - waktuMulai.getTime();
        long minutes = diffInMillies / (60 * 1000);

        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (hours > 0 && remainingMinutes > 0) {
            return hours + " Jam " + remainingMinutes + " Menit";
        } else if (hours > 0) {
            return hours + " Jam";
        } else {
            return remainingMinutes + " Menit";
        }
    }

    /**
     * Nama lokasi untuk display
     */
    public String getNamaLokasi() {
        return namaRuangan != null ? namaRuangan : "Belum ditentukan";
    }

    /**
     * Format dosen untuk display (bisa 1 atau 2 dosen)
     */
    public String getDosen() {
        if (DosenBimbingan1 == null && DosenBimbingan2 == null) {
            return "-";
        } else if (DosenBimbingan2 == null) {
            return DosenBimbingan1;
        } else {
            return DosenBimbingan1 + ", " + DosenBimbingan2;
        }
    }

    /**
     * List mahasiswa untuk display
     */
    public List<String> getMahasiswa() {
        return listNamaMahasiswa != null ? listNamaMahasiswa : List.of("-");
    }

    /**
     * Topik untuk display
     */
    public String getTopik() {
        return topikBimbingan != null ? topikBimbingan : "-";
    }

    /**
     * Deskripsi untuk display
     */
    public String getDeskripsi() {
        return deskripsiBimbingan != null ? deskripsiBimbingan : "-";
    }
}