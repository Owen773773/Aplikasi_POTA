package com.application.pota.bimbingan;

import com.application.pota.jadwal.JadwalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BimbinganService {

    private final BimbinganRepository bimbinganRepository;


}