package com.application.pota.dosen;

import com.application.pota.bimbingan.*;
import com.application.pota.jadwal.JadwalService;
import com.application.pota.jadwal.SlotWaktu;
import com.application.pota.notifikasi.Notifikasi;
import com.application.pota.notifikasi.NotifikasiService;
import com.application.pota.ruangan.Ruangan;
import com.application.pota.ruangan.RuanganService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dosen")
class DosenController {
    @Autowired
    private final JadwalService jadwalService;
    @Autowired
    private final DosenService dosenService;
    @Autowired
    private final BimbinganService bimbinganService;
    @Autowired
    private final NotifikasiService notifikasiService;
    @Autowired
    private final RuanganService ruanganService;

    @GetMapping({"/", ""})
    public String berandaDefault(Model model, HttpSession session) {
        return beranda(model, session);
    }

    @GetMapping({"/bimbingan", "/bimbinganProses"})
    public String bimbinganDefault(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        String tipeAkun = (String) session.getAttribute("tipeAkun");

        List<BimbinganSiapKirim> listBimbingan =
                bimbinganService.dapatkanBimbinganProses(tipeAkun, idPengguna);

        model.addAttribute("listBimbingan", listBimbingan);

        // Tambahkan list ruangan untuk popup pilih lokasi
        List<Ruangan> listRuangan = ruanganService.getAllRuang();
        model.addAttribute("listRuangan", listRuangan);

        return "dosen/bimbingan/DosenBimbinganProses";
    }

    @GetMapping("/bimbinganTerjadwal")
    public String bimbinganTerjadwal(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        String tipeAkun = (String) session.getAttribute("tipeAkun");

        List<BimbinganSiapKirim> listBimbingan =
                bimbinganService.dapatkanBimbinganTerjadwal(tipeAkun, idPengguna);

        model.addAttribute("listBimbingan", listBimbingan);
        return "dosen/bimbingan/DosenBimbinganTerjadwalkan";
    }

    @GetMapping("/bimbinganSelesai")
    public String bimbinganSelesai(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        String tipeAkun = (String) session.getAttribute("tipeAkun");

        List<BimbinganSiapKirim> listBimbingan =
                bimbinganService.dapatkanBimbinganSelesai(tipeAkun, idPengguna);

        model.addAttribute("listBimbingan", listBimbingan);
        return "dosen/bimbingan/DosenBimbinganSelesai";
    }

    @GetMapping("/bimbinganGagal")
    public String bimbinganGagal(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        String tipeAkun = (String) session.getAttribute("tipeAkun");

        List<BimbinganSiapKirim> listBimbingan =
                bimbinganService.dapatkanBimbinganGagal(tipeAkun, idPengguna);

        model.addAttribute("listBimbingan", listBimbingan);
        return "dosen/bimbingan/DosenBimbinganGagal";
    }

    @GetMapping("/beranda")
    public String beranda(Model model, HttpSession session) {
        String idPengguna = (String) session.getAttribute("idPengguna");

        //atas
        String semesterAktif = dosenService.getSemesterAktif(idPengguna);
        String tahapBimb = dosenService.getTahapBimbingan(idPengguna);
        int nMH = dosenService.getBanyakMHDibimbing(idPengguna);
        int jumlahMHMemenuhi = dosenService.getJumlahMahasiswaMemenuhiTarget(idPengguna, tahapBimb);

        //tengah
        int nPengajuan = dosenService.getBanyakPengajuan(idPengguna);
        Integer currentBimbingan = dosenService.getBanyakBimbinganHariIni(idPengguna);

        //bawah
        BimbinganDosenDashboard currentBimb = dosenService.getBimbinganSaatIni(idPengguna);

        // Cek null dulu sebelum mengakses method apapun
        if (currentBimb == null) {
            model.addAttribute("adaBimbingan", false);
        } else {
            model.addAttribute("adaBimbingan", true);
            model.addAttribute("waktuMulai", currentBimb.getWaktuMulai());
            model.addAttribute("waktuSelesai", currentBimb.getWaktuSelesai());
            LocalTime mulai = currentBimb.getWaktuMulai().toLocalTime();
            LocalTime selesai = currentBimb.getWaktuSelesai().toLocalTime();

            long durasiMenit = Duration.between(mulai, selesai).toMinutes();
            long durasiJam = durasiMenit / 60;
            model.addAttribute("durasiJam", durasiJam);

            //bawah
            model.addAttribute("currentBimb", currentBimb);
        }

        //atas
        model.addAttribute("semesterAktif", semesterAktif);
        model.addAttribute("tahapBimb", tahapBimb);
        model.addAttribute("nMH", nMH);
        model.addAttribute("jumlahMHMemenuhi", jumlahMHMemenuhi);

        //tengah
        model.addAttribute("nPengajuan", nPengajuan);
        model.addAttribute("currentBimbingan", currentBimbingan);

        return "dosen/DashboardDosen";
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

        // Ambil data jadwal dengan tipe pemblokiran atau bimbingan
        JadwalService.DataJadwalMingguan temp = jadwalService.dapatkanJadwalMingguan(week, idPengguna, false);
        Map<DayOfWeek, String> HariTanggal = temp.getTanggalHeader();
        List<List<SlotWaktu>> timetableGrid = temp.getGridJadwal();

        model.addAttribute("listHari", HariTanggal);
        model.addAttribute("timetable", timetableGrid);

        List<PilihanPengguna> pilihanMahasiwa = bimbinganService.getMahasiswaPilihan(idPengguna);
        model.addAttribute("listMahasiswa", pilihanMahasiwa);
        model.addAttribute("today", LocalDate.now());

        // Tambahkan list ruangan untuk form pengajuan
        List<Ruangan> listRuangan = ruanganService.getAllRuang();
        model.addAttribute("listRuangan", listRuangan);

        return "dosen/DosenJadwal";
    }

    @GetMapping("/cek-slot-tersedia")
    @ResponseBody
    public List<String> getAvailableSlots(
            @RequestParam("idMhs") List<String> listMahasiswa,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tanggal,
            HttpSession session) {

        String idPengguna = (String) session.getAttribute("idPengguna");
        if (idPengguna == null) {
            return Collections.emptyList();
        }
        //filter
        List<String> mhsFiltered = listMahasiswa.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // Panggil service (gabungin semua mahasiswa)
        return jadwalService.cariSlotGabungan(mhsFiltered, idPengguna, tanggal);
    }

    private LocalDate hitungTanggalMulaiMinggu(int tahun, int minggu) {
        return LocalDate.of(tahun, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, minggu)
                .with(DayOfWeek.MONDAY);
    }

    @GetMapping("/profil")
    public String profil(Model model, HttpSession session) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        ProfilDosen profilDosen = dosenService.ambilProfil(idPengguna);

        model.addAttribute("namaDosen", profilDosen.getNama());
        model.addAttribute("npmDosen", profilDosen.getNpm());
        model.addAttribute("peranPengguna", profilDosen.getPeran());
        model.addAttribute("usernameDosen", profilDosen.getUsername());
        return "dosen/ProfileDosen";
    }

    @GetMapping("/notifikasi")
    public String notifkasi(Model model, HttpSession session) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        List<Notifikasi> listNotif = notifikasiService.getNotifikasiInAppByIdUser(idPengguna);
        model.addAttribute("daftarNotifikasi", listNotif);
        return "dosen/NotifikasiDosen";
    }

    @PostMapping("/bimbingan/terima")
    @ResponseBody
    public Map<String, Object> terimaBimbingan(
            @RequestParam int idBim,
            @RequestParam Integer idRuangan, // Tambahkan parameter idRuangan
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

            // Pass idRuangan ke service method
            bimbinganService.terimaBimbingan(idBim, status.getPeranPengguna(), idRuangan);

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

    @PostMapping("/ajukan-bimbingan")
    @ResponseBody
    public Map<String, Object> ajukanBimbingan(
            @RequestBody DTOBimbinganDosen request,
            HttpSession session) {

        String idPengguna = (String) session.getAttribute("idPengguna");

        // Parse input
        LocalDate tanggal = LocalDate.parse(request.getTanggal());
        LocalTime mulai = LocalTime.parse(request.getWaktuMulai());
        LocalTime selesai = LocalTime.parse(request.getWaktuSelesai());
        Integer idRuangan = request.getIdRuangan(); // Tambahkan parameter idRuangan
        Integer jumlahMinggu = request.getJumlahMinggu() != null ? request.getJumlahMinggu() : 0;

        bimbinganService.ajukanBimbinganDosen(
                idPengguna,
                request.getMahasiswaIds(),
                request.getTopik(),
                request.getDeskripsi(),
                tanggal,
                mulai,
                selesai,
                idRuangan,
                jumlahMinggu
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Pengajuan bimbingan berhasil!");

        return response;
    }

    @GetMapping("/cek-ruangan-tersedia")
    @ResponseBody
    public List<Ruangan> getRuanganTersedia(
            @RequestParam int idBim,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tanggal,
            @RequestParam String jamMulai,
            @RequestParam String jamSelesai,
            HttpSession session) {

        String idPengguna = (String) session.getAttribute("idPengguna");

        if (idPengguna == null) {
            return Collections.emptyList();
        }

        // Parse jam
        LocalTime mulai = LocalTime.parse(jamMulai.replace(".", ":"));
        LocalTime selesai = LocalTime.parse(jamSelesai.replace(".", ":"));

        // ambil ruangan yang tersedia untuk waktu tersebut
        return jadwalService.cariRuanganTersedia(tanggal, mulai, selesai);
    }
}
