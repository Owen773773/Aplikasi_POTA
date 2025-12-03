package com.application.pota;

import com.application.pota.bimbingan.BimbinganRepository;
import com.application.pota.bimbingan.BimbinganService;
import com.application.pota.bimbingan.BimbinganSiapKirim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Bimbingan Service Test")
class BimbinganServiceTest {

    @Mock
    private BimbinganRepository bimbinganRepository;

    @InjectMocks
    private BimbinganService bimbinganService;

    private BimbinganSiapKirim bimbinganTerjadwal;
    private BimbinganSiapKirim bimbinganSelesai;

    @BeforeEach
    void setUp() {
        bimbinganTerjadwal = BimbinganSiapKirim.builder()
                .idBimbingan(1)
                .topikBimbingan("Topik A")
                .deskripsiBimbingan("Diskusi awal")
                .namaRuangan("Ruang A")
                .DosenBimbingan1("Dosen Satu")
                .TanggalBimbingan(java.sql.Date.valueOf("2025-11-24"))
                .waktuMulai(Time.valueOf("08:00:00"))
                .waktuSelesai(Time.valueOf("10:00:00"))
                .listNamaMahasiswa(Arrays.asList("Mahasiswa Satu"))
                .statusBimbingan("Terjadwalkan")
                .build();

        bimbinganSelesai = BimbinganSiapKirim.builder()
                .idBimbingan(5)
                .topikBimbingan("Topik E")
                .deskripsiBimbingan("Bimbingan Akhir")
                .namaRuangan("Ruang E")
                .DosenBimbingan1("Dosen Lima")
                .TanggalBimbingan(java.sql.Date.valueOf("2025-11-27"))
                .waktuMulai(Time.valueOf("09:00:00"))
                .waktuSelesai(Time.valueOf("11:00:00"))
                .listNamaMahasiswa(Arrays.asList("Mahasiswa Lima"))
                .statusBimbingan("Selesai")
                .build();
    }

    // FITUR 1: dapatkanBimbinganTerjadwal()
    @Test
    @DisplayName("Test 1.1: Mahasiswa dapatkan bimbingan terjadwalkan - Success")
    void testDapatkanBimbinganTerjadwal_Mahasiswa_Success() {
        // Arrange
        String tipeAkun = "Mahasiswa";
        String idPengguna = "U001";

        // Mock: ketika repository dipanggil, return data dummy
        when(bimbinganRepository.getBimbinganUserBertipe(tipeAkun, "Terjadwalkan", idPengguna))
                .thenReturn(Arrays.asList(bimbinganTerjadwal));

        // Act: panggil method yang mau ditest
        List<BimbinganSiapKirim> result = bimbinganService.dapatkanBimbinganTerjadwal(tipeAkun, idPengguna);

        // Periksa hasil
        assertNotNull(result, "Result tidak boleh null");
        assertEquals(1, result.size(), "Harus ada 1 bimbingan");
        assertEquals("Terjadwalkan", result.get(0).getStatusBimbingan(), "Status harus Terjadwalkan");
        assertEquals("Topik A", result.get(0).getTopikBimbingan(), "Topik harus sesuai");
        assertEquals("Ruang A", result.get(0).getNamaRuangan(), "Ruangan harus sesuai");

        // verifikasi pemanggilan: pastikan repository dipanggil 1 kali dengan parameter yang benar
        verify(bimbinganRepository, times(1))
                .getBimbinganUserBertipe(tipeAkun, "Terjadwalkan", idPengguna);
    }

    @Test
    @DisplayName("Test 1.2: Dosen dapatkan bimbingan terjadwalkan - Success")
    void testDapatkanBimbinganTerjadwal_Dosen_Success() {
        // Arrange
        String tipeAkun = "Dosen";
        String idPengguna = "U003";

        when(bimbinganRepository.getBimbinganUserBertipe(tipeAkun, "Terjadwalkan", idPengguna))
                .thenReturn(Arrays.asList(bimbinganTerjadwal));

        // Act
        List<BimbinganSiapKirim> result = bimbinganService.dapatkanBimbinganTerjadwal(tipeAkun, idPengguna);

        // Periksa hasil
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Terjadwalkan", result.get(0).getStatusBimbingan());
        assertEquals("Dosen Satu", result.get(0).getDosen());
        assertTrue(result.get(0).getListNamaMahasiswa().contains("Mahasiswa Satu"));

        verify(bimbinganRepository, times(1))
                .getBimbinganUserBertipe(tipeAkun, "Terjadwalkan", idPengguna);
    }

    @Test
    @DisplayName("Test 1.3: Bimbingan terjadwalkan tidak ditemukan - Empty List")
    void testDapatkanBimbinganTerjadwal_EmptyList() {
        // Arrange
        String tipeAkun = "Mahasiswa";
        String idPengguna = "U999";

        when(bimbinganRepository.getBimbinganUserBertipe(tipeAkun, "Terjadwalkan", idPengguna))
                .thenReturn(new ArrayList<>());

        // Act
        List<BimbinganSiapKirim> result = bimbinganService.dapatkanBimbinganTerjadwal(tipeAkun, idPengguna);

        // Periksa hasil
        assertNotNull(result, "Result tidak boleh null");
        assertTrue(result.isEmpty(), "Result harus list kosong");

        verify(bimbinganRepository, times(1))
                .getBimbinganUserBertipe(tipeAkun, "Terjadwalkan", idPengguna);
    }

    // FITUR 2: dapatkanBimbinganSelesai()

    @Test
    @DisplayName("Test 2.1: Mahasiswa dapatkan bimbingan selesai - Success")
    void testDapatkanBimbinganSelesai_Mahasiswa_Success() {
        // Arrange
        String tipeAkun = "Mahasiswa";
        String idPengguna = "U008";

        // Mock: ketika repository dipanggil, return data dummy
        when(bimbinganRepository.getBimbinganUserBertipe(tipeAkun, "Selesai", idPengguna))
                .thenReturn(Arrays.asList(bimbinganSelesai));

        // Act: panggil method yang mau ditest
        List<BimbinganSiapKirim> result = bimbinganService.dapatkanBimbinganSelesai(tipeAkun, idPengguna);

        // Assert: verifikasi hasilnya
        assertNotNull(result, "Result tidak boleh null");
        assertEquals(1, result.size(), "Harus ada 1 bimbingan");
        assertEquals("Selesai", result.get(0).getStatusBimbingan(), "Status harus Selesai");
        assertEquals("Topik E", result.get(0).getTopikBimbingan(), "Topik harus sesuai");
        assertEquals("Ruang E", result.get(0).getNamaRuangan(), "Ruangan harus sesuai");

        // verifikasi pemanggilan: pastikan repository dipanggil 1 kali dengan parameter yang benar
        verify(bimbinganRepository, times(1))
                .getBimbinganUserBertipe(tipeAkun, "Selesai", idPengguna);
    }

    @Test
    @DisplayName("Test 2.2: Dosen dapatkan bimbingan selesai - Success")
    void testDapatkanBimbinganSelesai_Dosen_Success() {
        // Arrange
        String tipeAkun = "Dosen";
        String idPengguna = "U011";

        when(bimbinganRepository.getBimbinganUserBertipe(tipeAkun, "Selesai", idPengguna))
                .thenReturn(Arrays.asList(bimbinganSelesai));

        // Act
        List<BimbinganSiapKirim> result = bimbinganService.dapatkanBimbinganSelesai(tipeAkun, idPengguna);

        // Periksa hasil
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Selesai", result.get(0).getStatusBimbingan());
        assertEquals("Dosen Lima", result.get(0).getDosen());

        verify(bimbinganRepository, times(1))
                .getBimbinganUserBertipe(tipeAkun, "Selesai", idPengguna);
    }

    @Test
    @DisplayName("Test 2.3: Bimbingan selesai tidak ditemukan - Empty List")
    void testDapatkanBimbinganSelesai_EmptyList() {
        // Arrange
        String tipeAkun = "Mahasiswa";
        String idPengguna = "U001";

        when(bimbinganRepository.getBimbinganUserBertipe(tipeAkun, "Selesai", idPengguna))
                .thenReturn(new ArrayList<>());

        // Act
        List<BimbinganSiapKirim> result = bimbinganService.dapatkanBimbinganSelesai(tipeAkun, idPengguna);

// Periksa hasil
        assertNotNull(result, "Result tidak boleh null");
        assertTrue(result.isEmpty(), "Result harus list kosong karena belum ada bimbingan selesai");

        verify(bimbinganRepository, times(1))
                .getBimbinganUserBertipe(tipeAkun, "Selesai", idPengguna);
    }
}