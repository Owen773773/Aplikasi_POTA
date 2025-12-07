package com.application.pota.notifikasi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/notifikasi")
public class NotifikasiController {
    @Autowired
    private NotifikasiService notifikasiService;

    @GetMapping("/username") //
    public String showNotificationByUsername(@RequestParam String username, Model model){
        List<Notifikasi> listNotif = notifikasiService.getNotifikasiInAppByUsername(username);
        model.addAttribute("daftarNotifikasi", listNotif); //th each
        return "notifikasi"; 
    }
    @GetMapping("/id") //
    public String showNotificationByIdUser(@RequestParam String idUser, Model model){
        List<Notifikasi> listNotif = notifikasiService.getNotifikasiInAppByIdUser(idUser);
        model.addAttribute("daftarNotifikasi", listNotif); //th each
        return "notifikasi"; 
    }
    
}
