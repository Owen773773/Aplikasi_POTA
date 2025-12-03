package com.application.pota.mahasiswa;

import com.application.pota.bimbingan.BimbinganService;
import com.application.pota.bimbingan.BimbinganSiapKirim;
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
    private BimbinganService bimbinganService;

    @Autowired
    private MahasiswaService mahasiswaService;

    @GetMapping({"/", ""})
    public String berandaDefault() {
        return beranda();
    }

    @GetMapping("/beranda")
    public String beranda() {
        return "mahasiswa/DashboardMahasiswa";
    }

    @GetMapping({"/bimbingan", "/bimbinganProses"})
    public String bimbinganDefault(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        String tipeAkun = (String) session.getAttribute("tipeAkun");

        List<BimbinganSiapKirim> listBimbingan =
                bimbinganService.dapatkanBimbinganProses(tipeAkun, idPengguna);

        model.addAttribute("listBimbingan", listBimbingan);
        return "mahasiswa/bimbingan/MahasiswaBimbinganProses";
    }

    @GetMapping("/bimbinganTerjadwal")
    public String bimbinganTerjadwal(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        String tipeAkun = (String) session.getAttribute("tipeAkun");

        List<BimbinganSiapKirim> listBimbingan =
                bimbinganService.dapatkanBimbinganTerjadwal(tipeAkun, idPengguna);

        model.addAttribute("listBimbingan", listBimbingan);
        return "mahasiswa/bimbingan/MahasiswaBimbinganTerjadwalkan";
    }

    @GetMapping("/bimbinganSelesai")
    public String bimbinganSelesai(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        String tipeAkun = (String) session.getAttribute("tipeAkun");

        List<BimbinganSiapKirim> listBimbingan =
                bimbinganService.dapatkanBimbinganSelesai(tipeAkun, idPengguna);

        model.addAttribute("listBimbingan", listBimbingan);
        return "mahasiswa/bimbingan/MahasiswaBimbinganSelesai";
    }

    @GetMapping("/bimbinganGagal")
    public String bimbinganGagal(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        String tipeAkun = (String) session.getAttribute("tipeAkun");

        List<BimbinganSiapKirim> listBimbingan =
                bimbinganService.dapatkanBimbinganGagal(tipeAkun, idPengguna);

        model.addAttribute("listBimbingan", listBimbingan);
        return "mahasiswa/bimbingan/MahasiswaBimbinganGagal";
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

    @GetMapping("/profil")
    public String profil(Model model, HttpSession session) {
        String id = (String)session.getAttribute("idPengguna");
        ProfilMahasiswa profilMahasiswa = mahasiswaService.makeProfile(id);

        model.addAttribute("nama", profilMahasiswa.getNama());
        model.addAttribute("npm", profilMahasiswa.getNpm());
        model.addAttribute("peran", profilMahasiswa.getPeran());
        model.addAttribute("dospem1", profilMahasiswa.getDosen1());
        model.addAttribute("dospem2 ", profilMahasiswa.getDosen2() == null? "-" : profilMahasiswa.getDosen2());
        model.addAttribute("pra", profilMahasiswa.getTotBimPra());
        model.addAttribute("pasca", profilMahasiswa.getTotBimPas());
        model.addAttribute("syarat", profilMahasiswa.getSyaratKelayakan());

        return "mahasiswa/ProfileMahasiswa";
    }
}