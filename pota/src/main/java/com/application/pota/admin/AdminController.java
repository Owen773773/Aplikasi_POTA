package com.application.pota.admin;

import com.application.pota.dosen.DosenService;
import com.application.pota.jadwal.JadwalService;
import com.application.pota.jadwal.SlotWaktu;
import com.application.pota.pengguna.Pengguna;
import com.application.pota.pengguna.PenggunaService;
import com.application.pota.ruangan.Ruangan;
import com.application.pota.ruangan.RuanganService;
import com.application.pota.tugasakhir.TugasAkhir;
import com.application.pota.tugasakhir.TugasAkhirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PenggunaService penggunaService;
    @Autowired
    private RuanganService ruanganService;
    @Autowired
    private JadwalService jadwalService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private TugasAkhirService tugasAkhirService;
    @Autowired
    private DosenService dosenService;


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

        return "admin/Admin_Akun";
    }

    @GetMapping("/toggleStatus")
    public String toggleStatus(Model model, @RequestParam(required = true) String idPengguna) {
        penggunaService.ubaStatus(idPengguna);
        return "redirect:/admin/akun";
    }

    @GetMapping("/ruangan")
    public String halamanRuangan(@RequestParam(required = false) Integer ruanganId,
                                 @RequestParam(required = false) String week,
                                 Model model) {

        List<Ruangan> listRuangan = ruanganService.getAllRuang();
        model.addAttribute("listRuangan", listRuangan);

        if (ruanganId == null && !listRuangan.isEmpty()) {
            ruanganId = listRuangan.get(0).getIdRuangan();
        }
        model.addAttribute("selectedRuanganId", ruanganId);

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
        JadwalService.DataJadwalMingguan temp = jadwalService.dapatkanJadwalMingguan(week, "" + ruanganId, true);
        Map<DayOfWeek, String> HariTanggal = temp.getTanggalHeader();
        List<List<SlotWaktu>> timetableGrid = temp.getGridJadwal();

        model.addAttribute("listHari", HariTanggal);
        model.addAttribute("timetable", timetableGrid);

        return "admin/Admin_Ruangan";
    }

    private LocalDate hitungTanggalMulaiMinggu(int tahun, int minggu) {
        return LocalDate.of(tahun, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, minggu)
                .with(DayOfWeek.MONDAY);
    }

    @GetMapping("/pengaturan")
    public String halamanPengaturan() {
        return "admin/Admin_Pengaturan";
    }

//    // 1. Get User Data
//    @GetMapping("/getUser")
//    @ResponseBody
//    public Pengguna getUser(@RequestParam String idPengguna) {
//        return penggunaService.findById(idPengguna);
//    }
//
//    // 2. Get TA Data untuk Mahasiswa
//    @GetMapping("/getTAData")
//    @ResponseBody
//    public TugasAkhir getTAData(@RequestParam String idPengguna) {
//        return tugasAkhirService.findByIdPengguna(idPengguna);
//    }
//
//    // 3. Get Topik Bimbingan untuk Dosen
//    @GetMapping("/getTopikBimbingan")
//    @ResponseBody
//    public Map<String, Object> getTopikBimbingan(@RequestParam String idPengguna) {
//        List<String> topikList = dosenService.getTopikBimbingan(idPengguna);
//        response.put("topikBimbingan", topikList);
//        return response;
//    }

}