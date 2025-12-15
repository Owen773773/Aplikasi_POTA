package com.application.pota.notifikasi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifikasi")
public class NotifikasiController {
    @Autowired
    private NotifikasiService notifikasiService;
}
