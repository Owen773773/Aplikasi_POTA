package com.application.pota.export;

import com.application.pota.export.ExportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExportJdbc implements ExportRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<KelayakanSidangDTO> getDataKelayakan() {
        String sql = """
            SELECT
                p.nama AS nama_mahasiswa,
                ta.TopikTA AS topik,
                (
                    SELECT COUNT(*)
                    FROM TopikBimbingan tb
                    JOIN PenjadwalanBimbingan pb ON tb.IdBim = pb.IdBim
                    JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
                    WHERE tb.IdTA = ta.IdTA
                      AND j.tanggal < ta.TanggalUTS
                ) AS pra_uts,
                (
                    SELECT COUNT(*)
                    FROM TopikBimbingan tb
                    JOIN PenjadwalanBimbingan pb ON tb.IdBim = pb.IdBim
                    JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
                    WHERE tb.IdTA = ta.IdTA
                      AND j.tanggal >= ta.TanggalUTS
                ) AS pasca_uts,
                CASE
                    WHEN
                        (
                            SELECT COUNT(*)
                            FROM TopikBimbingan tb
                            JOIN PenjadwalanBimbingan pb ON tb.IdBim = pb.IdBim
                            JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
                            WHERE tb.IdTA = ta.IdTA
                              AND j.tanggal < ta.TanggalUTS
                        ) >= ak.minimumPra
                    AND
                        (
                            SELECT COUNT(*)
                            FROM TopikBimbingan tb
                            JOIN PenjadwalanBimbingan pb ON tb.IdBim = pb.IdBim
                            JOIN Jadwal j ON pb.IdJadwal = j.IdJadwal
                            WHERE tb.IdTA = ta.IdTA
                              AND j.tanggal >= ta.TanggalUTS
                        ) >= ak.minimumPasca
                    THEN 'Layak' ELSE 'Tidak Layak'
                END AS kelayakan
            FROM TugasAkhir ta
            JOIN Pengguna p ON ta.IdMahasiswa = p.IdPengguna
            JOIN TAtermasukAkademik tak ON ta.IdTA = tak.IdTA
            JOIN Akademik ak ON tak.idAkademik = ak.idSemester;
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            KelayakanSidangDTO dto = new KelayakanSidangDTO();
            dto.setNama(rs.getString("nama_mahasiswa"));
            dto.setTopik(rs.getString("topik"));
            dto.setPraUts(rs.getInt("pra_uts"));
            dto.setPascaUts(rs.getInt("pasca_uts"));
            dto.setKelayakan(rs.getString("kelayakan"));
            return dto;
        });
    }
}
