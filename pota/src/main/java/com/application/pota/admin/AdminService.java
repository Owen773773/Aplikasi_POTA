package com.application.pota.admin;

import com.application.pota.jadwal.Jadwal;
import com.application.pota.jadwal.JadwalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;

}