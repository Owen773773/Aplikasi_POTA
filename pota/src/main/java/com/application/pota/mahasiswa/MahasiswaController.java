package com.application.pota.mahasiswa;

import com.application.pota.bimbingan.Bimbingan;
import com.application.pota.bimbingan.BimbinganService;
import com.application.pota.jadwal.JadwalService;
import com.application.pota.jadwal.SlotWaktu;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mahasiswa")
public class MahasiswaController {
    @Autowired
    private JadwalService jadwalService;

    @Autowired
    private MahasiswaService mahasiswaService;

    @Autowired
    private BimbinganService bimbinganService;

    @GetMapping({"/", ""})
    public String berandaDefault() {
        return "redirect:/mahasiswa/beranda";
    }

    // @GetMapping("/beranda")
    // public String beranda() {
    //     return "mahasiswa/DashboardMahasiswa";
    // }
      @GetMapping("/beranda")
    public String beranda(HttpSession session, Model model) {        
        String idPengguna = (String) session.getAttribute("idPengguna");
        if (idPengguna == null) {
            return "redirect:/login"; 
        }
        // Bimbingan bimbinganTerdekat = bimbinganService.getJadwalTerdekat(idPengguna);
        // model.addAttribute("bimbinganTerdekat", bimbinganTerdekat);
        return "mahasiswa/DashboardMahasiswa";    
    }
    

    @GetMapping({"/bimbingan", "/bimbinganProses"})
    public String bimbinganDefault() {
        return "mahasiswa/bimbingan/MahasiswaBimbinganProses";
    }

    @GetMapping("/bimbinganTerjadwal")
    public String bimbinganTerjadwal() {
        return "mahasiswa/bimbingan/MahasiswaBimbinganTerjadwalkan";
    }

    @GetMapping("/bimbinganSelesai")
    public String bimbinganSelesai() {
        return "mahasiswa/bimbingan/MahasiswaBimbinganSelesai";
    }

    @GetMapping("/bimbinganGagal")
    public String bimbinganGagal() {
        return "mahasiswa/bimbingan/MahasiswaBimbinganGagal";
    }

    @GetMapping("/profil")
    public String profil() {
        return "mahasiswa/ProfileMahasiswa";
    }

    @GetMapping("/jadwal")
    public String mahasiswajadwal(@RequestParam(required = false) String week, HttpSession session,
                                  Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        if (week == null || week.isEmpty()) {
            LocalDate hariIni = LocalDate.now();
            int weekNum = hariIni.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int year = hariIni.get(IsoFields.WEEK_BASED_YEAR);
            String weekNumFormatted = String.format("%02d", weekNum);
            week = year + "-W" + weekNumFormatted;
        }

        model.addAttribute("weekParam", week);

        String tahun = week.substring(0, 4);
        String minggu = week.substring(6);

        LocalDate tanggalMulai = hitungTanggalMulaiMinggu(Integer.parseInt(tahun), Integer.parseInt(minggu));
        String tanggalMulaiFormatted = tanggalMulai.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        model.addAttribute("tanggalMulaiMinggu", tanggalMulaiFormatted);

        // Ambil data jadwal dengan tipe (PEMBLOKIRAN atau BIMBINGAN)
        JadwalService.DataJadwalMingguan temp = jadwalService.dapatkanJadwalMingguan(week, idPengguna, false);
        Map<DayOfWeek, String> HariTanggal = temp.getTanggalHeader();
        List<List<SlotWaktu>> timetableGrid = temp.getGridJadwal();

        model.addAttribute("listHari", HariTanggal);
        model.addAttribute("timetable", timetableGrid);


        return "mahasiswa/MahasiswaJadwal";
    }

    private LocalDate hitungTanggalMulaiMinggu(int tahun, int minggu) {
        return LocalDate.of(tahun, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, minggu)
                .with(DayOfWeek.MONDAY);
    }
}
