package com.application.pota.dosen;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DosenService {
    private final DosenRepository dosenRepository;

    public ProfilDosen ambilProfil(String idPengguna) {
        ProfilDosen profilDosen = dosenRepository.makeProfileByIdPengguna(idPengguna);

        return profilDosen;
    }

    public int getSemesterAktif(String idPengguna) {
        return dosenRepository.getTahunAktifByIdPengguna(idPengguna);
    }
}
