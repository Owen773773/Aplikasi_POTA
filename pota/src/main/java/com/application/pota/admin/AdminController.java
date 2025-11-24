package com.application.pota.admin;

import com.application.pota.pengguna.Pengguna;
import com.application.pota.pengguna.PenggunaService;
import com.application.pota.ruangan.Ruangan;
import com.application.pota.ruangan.RuanganService;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PenggunaService penggunaService;
    @Autowired
    private RuanganService ruanganService;

    @GetMapping({"", "/"})
    public String halamanAdmin(Model model) {
        return halamanAkun(model);
    }

    @GetMapping("/akun")
    public String halamanAkun(Model model) {
        List<Pengguna> listUser = penggunaService.getAllPengguna();

        if (listUser == null) {
            listUser = new ArrayList<>();
        }

        model.addAttribute("listUser", listUser);

        model.addAttribute("user", new Pengguna());
        model.addAttribute("thesis", new Object());

        return "Admin_Akun";
    }

    @GetMapping("/ruangan")
    public String halamanRuangan(@RequestParam(required = false) Integer ruanganId,
                                 @RequestParam(required = false) String week,
                                 Model model) {
        // ✅ List Ruangan
        List<Ruangan> listRuangan = ruanganService.getAllRuang();
        model.addAttribute("listRuangan", listRuangan);

        // ✅ Set ruangan pertama sebagai default jika tidak ada parameter
        if (ruanganId == null && !listRuangan.isEmpty()) {
            ruanganId = listRuangan.get(0).getIdRuangan();
        }
        model.addAttribute("selectedRuanganId", ruanganId);

        // ✅ Minggu - bila kosong set minggu ini
        if (week == null || week.isEmpty()) {
            LocalDate hariIni = LocalDate.now();
            int weekNum = hariIni.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int year = hariIni.get(IsoFields.WEEK_BASED_YEAR);
            String weekNumFormatted = String.format("%02d", weekNum);
            week = year + "-W" + weekNumFormatted;
        }

        model.addAttribute("weekParam", week);

        // ✅ Hitung tanggal mulai minggu untuk ditampilkan
        String tahun = week.substring(0, 4);
        String minggu = week.substring(6);

        LocalDate tanggalMulai = hitungTanggalMulaiMinggu(Integer.parseInt(tahun), Integer.parseInt(minggu));
        String tanggalMulaiFormatted = tanggalMulai.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        model.addAttribute("tanggalMulaiMinggu", tanggalMulaiFormatted);

        // TODO: Load schedule data
        // model.addAttribute("listScheduleSlots", scheduleService.getSchedule(ruanganId, week));

        return "Admin_Ruangan";
    }

    // ✅ Helper method untuk hitung tanggal mulai minggu
    private LocalDate hitungTanggalMulaiMinggu(int tahun, int minggu) {
        return LocalDate.of(tahun, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, minggu)
                .with(DayOfWeek.MONDAY);
    }

    @GetMapping("/pengaturan")
    public String halamanPengaturan() {
        return "Admin_Pengaturan";
    }
}