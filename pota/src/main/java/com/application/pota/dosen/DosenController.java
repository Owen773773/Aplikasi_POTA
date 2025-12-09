package com.application.pota.dosen;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

import com.application.pota.bimbingan.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.application.pota.jadwal.JadwalService;
import com.application.pota.jadwal.SlotWaktu;
import com.application.pota.notifikasi.Notifikasi;
import com.application.pota.notifikasi.NotifikasiService;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dosen")
class DosenController {
    @Autowired
    private final JadwalService jadwalService;
    @Autowired
    private final DosenService dosenService;
    @Autowired
    private  final BimbinganService bimbinganService;
    @Autowired
    private final NotifikasiService notifikasiService;
    
    @GetMapping({"/", ""})
    public String berandaDefault() {
        return beranda();
    }
    @GetMapping({"/bimbingan", "/bimbinganProses"})
    public String bimbinganDefault(HttpSession session, Model model) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        String tipeAkun = (String) session.getAttribute("tipeAkun");

        List<BimbinganSiapKirim> listBimbingan =
                bimbinganService.dapatkanBimbinganProses(tipeAkun, idPengguna);

        model.addAttribute("listBimbingan", listBimbingan);
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
    public String beranda() {
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

        // Ambil data jadwal dengan tipe (PEMBLOKIRAN atau BIMBINGAN)
        JadwalService.DataJadwalMingguan temp = jadwalService.dapatkanJadwalMingguan(week, idPengguna, false);
        Map<DayOfWeek, String> HariTanggal = temp.getTanggalHeader();
        List<List<SlotWaktu>> timetableGrid = temp.getGridJadwal();

        model.addAttribute("listHari", HariTanggal);
        model.addAttribute("timetable", timetableGrid);

        List<PilihanPengguna> pilihanMahasiwa = bimbinganService.getMahasiswaPilihan(idPengguna);
        model.addAttribute("listMahasiswa", pilihanMahasiwa);


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

        // Panggil service (gabungkan semua mahasiswa)
        return jadwalService.cariSlotGabungan(mhsFiltered, idPengguna, tanggal);
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

        bimbinganService.ajukanBimbinganDosen(idPengguna, request.getMahasiswaIds(), request.getTopik(), request.getDeskripsi(), tanggal, mulai, selesai);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Pengajuan bimbingan berhasil!");

        return response;
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
        return "dosen/ProfileDosen";
    }

    @GetMapping("/notifikasi")
    public String notifkasi(Model model, HttpSession session) {
        String idPengguna = (String) session.getAttribute("idPengguna");
        List<Notifikasi> listNotif = notifikasiService.getNotifikasiInAppByIdUser(idPengguna);
        model.addAttribute("daftarNotifikasi", listNotif);
        return "dosen/NotifikasiDosen";
    }
}
