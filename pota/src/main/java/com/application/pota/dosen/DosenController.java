package com.application.pota.dosen;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.application.pota.jadwal.JadwalService;
import com.application.pota.jadwal.SlotWaktu;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dosen")
class DosenController {
    @Autowired
    private final JadwalService jadwalService;
    private final DosenService dosenService;
    
    @GetMapping({"/", ""})
    public String berandaDefault() {
        return beranda();
    }
    
    @GetMapping("/beranda")
    public String beranda() {
        return "DashboardDosen";
    }
    
    @GetMapping("/jadwal")
    public String dosenjadwal(@RequestParam(required = false) String week, HttpSession session,
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


        return "DosenJadwal";
    }
    
    private LocalDate hitungTanggalMulaiMinggu(int tahun, int minggu) {
        return LocalDate.of(tahun, 1, 1)
        .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, minggu)
        .with(DayOfWeek.MONDAY);
    }

    @GetMapping("/profil")
    public String profil(Model model, HttpSession session) {
        String idPengguna = (String)session.getAttribute("idPengguna");
        ProfilDosen profilDosen = dosenService.ambilProfil(idPengguna);

        model.addAttribute("namaDosen", profilDosen.getNama());
        model.addAttribute("npmDosen", profilDosen.getNpm());
        model.addAttribute("peranPengguna", profilDosen.getPeran());
        model.addAttribute("usernameDosen", profilDosen.getUsername()); 
        return "ProfileDosen";
    }
}
