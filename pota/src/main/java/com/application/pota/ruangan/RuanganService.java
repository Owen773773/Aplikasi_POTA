package com.application.pota.ruangan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuanganService {
    private final RuanganRepository ruanganRepository;

    public List<Ruangan> getAllRuang() {
       return ruanganRepository.getAllRuang();
    }

}
