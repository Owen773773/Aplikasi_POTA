package com.application.pota.notifikasi;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifikasi")
public class NotifikasiController {
    @Autowired
    private NotifikasiService notifikasiService;

    @GetMapping("/")
    public String showNotificationByUsername(@RequestParam String username, Model model){
        List<Notifikasi> listNotif = notifikasiService.kirimNotifikasiInAppByUsername(username);
        model.addAttribute("daftarNotifikasi", listNotif); //th each
        return "notifikasi"; 
    }
    @GetMapping("/")
    public String showNotificationByIdUser(@RequestParam String idUser, Model model){
        List<Notifikasi> listNotif = notifikasiService.kirimNotifikasiInAppByUsername(idPengguna);
        model.addAttribute("daftarNotifikasi", listNotif); //th each
        return "notifikasi"; 
    }

}
