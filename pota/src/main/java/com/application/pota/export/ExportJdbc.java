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
            WITH CurrentDate AS (
                SELECT
                DATE '2025-12-12' AS today_date
                ),
                MahasiswaTA AS (
                SELECT
                    P.nama AS nama_mahasiswa,
                    TA.IdTA,
                    TA.TopikTA,
                    TA.TanggalUTS,
                    TA.TanggalUas,
                    A.idSemester,
                    A.minimumPra,
                    A.minimumPasca
                FROM
                    Pengguna P
                JOIN
                    Mahasiswa M ON P.IdPengguna = M.IdPengguna
                JOIN
                    TugasAkhir TA ON M.IdPengguna = TA.IdMahasiswa
                JOIN
                    TAtermasukAkademik TAA ON TA.IdTa = TAA.IdTA
                JOIN
                    Akademik A ON TAA.idAkademik = A.idSemester
                ),
                BimbinganCount AS (
                SELECT
                    MTA.IdTA,
                    COUNT(CASE
                        WHEN J.tanggal < MTA.TanggalUTS THEN 1
                        ELSE NULL
                    END) AS jumlah_bim_pra_uts,
                    COUNT(CASE
                        WHEN J.tanggal >= MTA.TanggalUTS AND J.tanggal <= MTA.TanggalUas THEN 1
                        ELSE NULL
                    END) AS jumlah_bim_pasca_uts
                FROM
                    MahasiswaTA MTA
                JOIN
                    TopikBimbingan TB ON MTA.IdTA = TB.IdTA
                JOIN
                    PenjadwalanBimbingan PB ON TB.IdBim = PB.IdBim
                JOIN
                    Jadwal J ON PB.IdJadwal = J.IdJadwal
                WHERE
                    TB.StatusBimbingan = 'Selesai' -- Hanya hitung bimbingan yang sudah 'Selesai'
                GROUP BY
                    MTA.IdTA
                )
                SELECT
                MTA.nama_mahasiswa,
                MTA.TopikTA AS topik_tugas_akhir,
                COALESCE(BC.jumlah_bim_pra_uts, 0) AS jumlah_bimbingan_pra_uts,
                COALESCE(BC.jumlah_bim_pasca_uts, 0) AS jumlah_bimbingan_pasca_uts,
                CASE
                    WHEN CD.today_date < MTA.TanggalUTS THEN
                        'Pra-UTS'
                    WHEN CD.today_date >= MTA.TanggalUTS AND CD.today_date <= MTA.TanggalUas THEN
                        'Pasca-UTS'
                    ELSE
                        'Selesai Periode'
                END AS periode_saat_ini,
                CASE
                    WHEN CD.today_date < MTA.TanggalUTS THEN
                        CASE
                            WHEN COALESCE(BC.jumlah_bim_pra_uts, 0) >= MTA.minimumPra THEN 'Layak'
                            ELSE 'Tidak Layak'
                        END
                    WHEN CD.today_date >= MTA.TanggalUTS AND CD.today_date <= MTA.TanggalUas THEN
                        CASE
                            WHEN COALESCE(BC.jumlah_bim_pasca_uts, 0) >= MTA.minimumPasca THEN 'Layak'
                            ELSE 'Tidak Layak'
                        END
                    ELSE
                        'N/A (Periode berakhir)'
                END AS status_kelayakan,
                CASE
                    WHEN CD.today_date < MTA.TanggalUTS THEN MTA.minimumPra
                    WHEN CD.today_date >= MTA.TanggalUTS AND CD.today_date <= MTA.TanggalUas THEN MTA.minimumPasca
                    ELSE NULL
                END AS minimum_bimbingan_yang_diperlukan
                FROM
                    MahasiswaTA MTA
                CROSS JOIN
                    CurrentDate CD
                LEFT JOIN
                    BimbinganCount BC ON MTA.IdTA = BC.IdTA;
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            KelayakanSidangDTO dto = new KelayakanSidangDTO();
            dto.setNama(rs.getString("nama_mahasiswa"));
            dto.setTopik(rs.getString("topik_tugas_akhir")); // <-- KOREKSI
            dto.setPraUts(rs.getInt("jumlah_bimbingan_pra_uts")); // <-- KOREKSI
            dto.setPascaUts(rs.getInt("jumlah_bimbingan_pasca_uts")); // <-- KOREKSI
            dto.setKelayakan(rs.getString("status_kelayakan")); // <-- KOREKSI

            return dto;
        });
    }
}
