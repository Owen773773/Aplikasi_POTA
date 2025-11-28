package com.application.pota.akademik;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AkademikService {
    @Autowired
    private AkademikRepository akademikRepository;

    public int getAkademikMinimumPra(int idSemester){
        return akademikRepository.getMinimumPra(idSemester);
    }

    public int getAkademikMinimumPasca(int idSemester){
        return akademikRepository.getMinimumPra(idSemester);
    }

    
}