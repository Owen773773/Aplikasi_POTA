package com.application.pota.jadwal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
//@RequestMapping("/")
public class JadwalController {
    @Autowired
    private  JadwalService jadwalService;

}
