package com.application.pota.ruangan;
import org.springframework.stereotype.Repository;

@Repository
public interface RuanganRepository {
    List<Ruangan> getAllRuang();
}