package com.application.pota.mahasiswa;

import com.application.pota.bimbingan.*;
import com.application.pota.jadwal.JadwalService;
import com.application.pota.jadwal.SlotWaktu;
import com.application.pota.notifikasi.Notifikasi;
import com.application.pota.notifikasi.NotifikasiService;
import com.application.pota.tugasakhir.TugasAkhirService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.HashMap;
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
    @Autowired
    private NotifikasiService notifikasiService;
    @Autowired
    private TugasAkhirService tugasAkhirService;

    @GetMapping("/beranda")
    public String beranda(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        if (idPengguna == null) {
            return "redirect:/login";
        }

        DashboardDataMhs dashboardData = mahasiswaService.getDashboardData(idPengguna);
        model.addAttribute("DashboardDataMhs", dashboardData);

        BimbinganSiapKirim bimbinganMendatang = mahasiswaService.getBimbinganMendatang(idPengguna);
        model.addAttribute("bimbinganMendatang", bimbinganMendatang);
        return "mahasiswa/DashboardMahasiswa";
    }

    @GetMapping({"", "/"})
    public String redirectToBeranda() {
        return "redirect:/mahasiswa/beranda";
    }


    @GetMapping("/bimbinganProses")
    public String bimbinganProses(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        String tipeAkun = (String) session.getAttribute("tipeAkun");

        List<BimbinganSiapKirim> listBimbingan =
                bimbinganService.dapatkanBimbinganProses(tipeAkun, idPengguna);

        model.addAttribute("listBimbingan", listBimbingan);
        return "mahasiswa/bimbingan/MahasiswaBimbinganProses";
    }

    @GetMapping("/bimbingan")
    public String redirectToBimbinganProses() {
        return "redirect:/mahasiswa/bimbinganProses";
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


    @PostMapping("/bimbingan/terima")
    @ResponseBody
    public Map<String, Object> terimaBimbingan(
            @RequestParam int idBim,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String idPengguna = (String) session.getAttribute("idPengguna");

            if (!bimbinganService.bisaTerima(idBim, idPengguna)) {
                response.put("success", false);
                response.put("message", "Anda tidak dapat menerima bimbingan ini.");
                return response;
            }

            BimbinganDetailStatus status = bimbinganService.getDetailStatusBimbingan(idBim, idPengguna);
            bimbinganService.terimaBimbingan(idBim, status.getPeranPengguna());

            response.put("success", true);
            response.put("message", "Bimbingan berhasil diterima!");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/bimbingan/tolak")
    @ResponseBody
    public Map<String, Object> tolakBimbingan(
            @RequestParam int idBim,
            @RequestParam(required = false) String catatan,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String idPengguna = (String) session.getAttribute("idPengguna");

            if (!bimbinganService.bisaTolak(idBim, idPengguna)) {
                response.put("success", false);
                response.put("message", "Anda tidak dapat menolak bimbingan ini.");
                return response;
            }

            BimbinganDetailStatus status = bimbinganService.getDetailStatusBimbingan(idBim, idPengguna);
            bimbinganService.tolakBimbingan(idBim, status.getPeranPengguna(), catatan != null ? catatan : "-");

            response.put("success", true);
            response.put("message", "Bimbingan berhasil ditolak.");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/bimbingan/validasi")
    @ResponseBody
    public Map<String, Object> validasiBimbingan(
            @RequestParam int idBim,
            @RequestParam(required = false) String catatan,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String idPengguna = (String) session.getAttribute("idPengguna");

            if (!bimbinganService.bisaValidasi(idBim, idPengguna)) {
                response.put("success", false);
                response.put("message", "Anda belum dapat memvalidasi bimbingan ini. Pastikan minimal satu dosen sudah memvalidasi.");
                return response;
            }

            BimbinganDetailStatus status = bimbinganService.getDetailStatusBimbingan(idBim, idPengguna);
            bimbinganService.validasiBimbingan(idBim, status.getPeranPengguna(), catatan != null ? catatan : "-");

            response.put("success", true);
            response.put("message", "Bimbingan berhasil divalidasi!");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/bimbingan/batalkan")
    @ResponseBody
    public Map<String, Object> batalkanBimbingan(
            @RequestParam int idBim,
            @RequestParam(required = false) String catatan,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String idPengguna = (String) session.getAttribute("idPengguna");

            if (!bimbinganService.bisaBatalkan(idBim, idPengguna)) {
                response.put("success", false);
                response.put("message", "Anda tidak dapat membatalkan bimbingan ini.");
                return response;
            }

            BimbinganDetailStatus status = bimbinganService.getDetailStatusBimbingan(idBim, idPengguna);
            bimbinganService.batalkanBimbingan(idBim, status.getPeranPengguna(), catatan != null ? catatan : "-");

            response.put("success", true);
            response.put("message", "Bimbingan berhasil dibatalkan.");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/bimbingan/cek-aksi")
    @ResponseBody
    public Map<String, Object> cekAksiTersedia(
            @RequestParam int idBim,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        String idPengguna = (String) session.getAttribute("idPengguna");

        BimbinganDetailStatus status = bimbinganService.getDetailStatusBimbingan(idBim, idPengguna);

        response.put("bisaTerima", bimbinganService.bisaTerima(idBim, idPengguna));
        response.put("bisaTolak", bimbinganService.bisaTolak(idBim, idPengguna));
        response.put("bisaValidasi", bimbinganService.bisaValidasi(idBim, idPengguna));
        response.put("bisaBatalkan", bimbinganService.bisaBatalkan(idBim, idPengguna));
        response.put("sudahTerima", status.isSudahTerima());
        response.put("peran", status.getPeranPengguna());

        return response;
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
        model.addAttribute("today", LocalDate.now());
        LocalDate tanggalMulai = hitungTanggalMulaiMinggu(Integer.parseInt(tahun), Integer.parseInt(minggu));
        String tanggalMulaiFormatted = tanggalMulai.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        model.addAttribute("tanggalMulaiMinggu", tanggalMulaiFormatted);

        JadwalService.DataJadwalMingguan temp = jadwalService.dapatkanJadwalMingguan(week, idPengguna, false);
        Map<DayOfWeek, String> HariTanggal = temp.getTanggalHeader();
        List<List<SlotWaktu>> timetableGrid = temp.getGridJadwal();

        model.addAttribute("listHari", HariTanggal);
        model.addAttribute("timetable", timetableGrid);

        int idTA = tugasAkhirService.getIdTugasAkhir(idPengguna);
        List<PilihanPengguna> pilihanDosen = bimbinganService.getDosenPembimbingPilihan(idTA);
        model.addAttribute("dosenSatu", pilihanDosen.get(0));
        model.addAttribute("dosenDua", pilihanDosen.size() > 1 ? pilihanDosen.get(1) : null);

        return "mahasiswa/MahasiswaJadwal";
    }

    @GetMapping("/cek-slot-tersedia")
    @ResponseBody
    public List<String> getAvailableSlots(
            @RequestParam("ids") List<String> listIdDosen,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tanggal,
            HttpSession session) {

        String idPengguna = (String) session.getAttribute("idPengguna");

        if (idPengguna == null) {
            return java.util.Collections.emptyList();
        }

        List<String> dosenFixed = listIdDosen.stream()
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        return jadwalService.cariSlotGabungan(dosenFixed, idPengguna, tanggal);
    }

    @PostMapping("/ajukan-bimbingan")
    @ResponseBody
    public Map<String, Object> ajukanBimbingan(
            @RequestBody DTOBimbinganMahasiswa request,
            HttpSession session) {

        String idPengguna = (String) session.getAttribute("idPengguna");

        LocalDate tanggal = LocalDate.parse(request.getTanggal());
        LocalTime mulai = LocalTime.parse(request.getWaktuMulai());
        LocalTime selesai = LocalTime.parse(request.getWaktuSelesai());

        bimbinganService.ajukanBimbinganMahasiswa(idPengguna, request.getDosenIds(), request.getTopik(), request.getDeskripsi(), tanggal, mulai, selesai);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Pengajuan bimbingan berhasil!");

        return response;
    }


    @GetMapping("/profil")
    public String profil(Model model, HttpSession session) {
        String id = (String) session.getAttribute("idPengguna");
        ProfilMahasiswa profilMahasiswa = mahasiswaService.makeProfile(id);

        model.addAttribute("nama", profilMahasiswa.getNama());
        model.addAttribute("npm", profilMahasiswa.getNpm());
        model.addAttribute("peran", profilMahasiswa.getPeran());
        model.addAttribute("dospem1", profilMahasiswa.getDosen1());
        model.addAttribute("dospem2", profilMahasiswa.getDosen2() == null ? "-" : profilMahasiswa.getDosen2());
        model.addAttribute("pra", profilMahasiswa.getTotBimPra());
        model.addAttribute("pasca", profilMahasiswa.getTotBimPas());
        model.addAttribute("syarat", profilMahasiswa.getSyaratKelayakan());

        return "mahasiswa/ProfileMahasiswa";
    }

    @GetMapping("/notifikasi")
    public String notifikasi(Model model, HttpSession session) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        List<Notifikasi> listNotif = notifikasiService.getNotifikasiInAppByIdUser(idPengguna);
        model.addAttribute("daftarNotifikasi", listNotif);
        return "mahasiswa/NotifikasiMahasiswa";
    }


    private LocalDate hitungTanggalMulaiMinggu(int tahun, int minggu) {
        return LocalDate.of(tahun, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, minggu)
                .with(DayOfWeek.MONDAY);
    }
}