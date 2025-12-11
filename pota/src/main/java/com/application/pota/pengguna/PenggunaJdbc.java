package com.application.pota.pengguna;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor  // Lombok untuk constructor injection
public class PenggunaJdbc implements PenggunaRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper untuk mapping ResultSet ke Pengguna
    private Pengguna mapRowToPengguna(ResultSet rs, int rowNum) throws SQLException {
        Pengguna pengguna = new Pengguna();
        pengguna.setIdPengguna(rs.getString("IdPengguna"));
        pengguna.setUsername(rs.getString("username"));
        pengguna.setPassword(rs.getString("password"));
        pengguna.setNama(rs.getString("nama"));
        pengguna.setStatusAktif(rs.getBoolean("statusAktif"));
        pengguna.setTipeAkun(rs.getString("tipeAkun"));
        pengguna.setLastLogin(rs.getTimestamp("lastLogin").toLocalDateTime());
        return pengguna;
    }

    @Override
    public Pengguna getById(String id) {
        String sql = "SELECT * FROM Pengguna WHERE IdPengguna = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToPengguna, id);
    }

    @Override
    public Pengguna getByUsername(String username) {
        String sql = "SELECT * FROM Pengguna WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToPengguna, username);
    }

    @Override
    public Pengguna authenticate(String username, String password) {
        try {
            String sql = "SELECT * FROM Pengguna WHERE username = ? AND password = ? AND statusAktif = TRUE";
            Pengguna temp = jdbcTemplate.queryForObject(sql, this::mapRowToPengguna, username, password);
            return temp;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Pengguna> getByType(String tipeAkun) {
        String sql = "SELECT * FROM Pengguna WHERE tipeAkun = ?";
        return jdbcTemplate.query(sql, this::mapRowToPengguna, tipeAkun);
    }

    @Override
    public void add(Pengguna pengguna) {
        String sql = "INSERT INTO Pengguna (IdPengguna, username, password, nama, statusAktif, tipeAkun, lastLogin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                pengguna.getIdPengguna(),
                pengguna.getUsername(),
                pengguna.getPassword(),
                pengguna.getNama(),
                pengguna.isStatusAktif(),
                pengguna.getTipeAkun(),
                pengguna.getLastLogin()
        );
    }

    @Override
    public void edit(Pengguna pengguna) {
        String sql = "UPDATE Pengguna SET username = ?, password = ?, nama = ?, " +
                "statusAktif = ?, tipeAkun = ?, lastLogin = ? WHERE IdPengguna = ?";
        jdbcTemplate.update(sql,
                pengguna.getUsername(),
                pengguna.getPassword(),
                pengguna.getNama(),
                pengguna.isStatusAktif(),
                pengguna.getTipeAkun(),
                pengguna.getLastLogin(),
                pengguna.getIdPengguna()
        );
    }

    @Override
    public void delete(String id) {

        String sqlDelJadwalPribadi = "DELETE FROM Jadwal_Pribadi WHERE IdPengguna = ?";
        jdbcTemplate.update(sqlDelJadwalPribadi, id);
        
        String sqlDelNotifMhs = "DELETE FROM MahasiswaNotifikasi WHERE IdPengguna = ?";
        jdbcTemplate.update(sqlDelNotifMhs, id);

        String sqlDelNotifDsn = "DELETE FROM DosenNotifikasi WHERE IdPengguna = ?";
        jdbcTemplate.update(sqlDelNotifDsn, id);

        String sqlDelBlokRuangan = "DELETE FROM PemblokiranRuangan WHERE idAdmin = ?";
        jdbcTemplate.update(sqlDelBlokRuangan, id);

        String sqlDelDosBingUDosen = "DELETE FROM Dosen_Pembimbing WHERE IdDosen = ?";
        jdbcTemplate.update(sqlDelDosBingUDosen, id);

        String sqlDelTAAKademikMhs = "DELETE FROM TAtermasukAkademik WHERE IdTA IN (SELECT IdTa FROM TugasAkhir WHERE IdMahasiswa = ?)";
        jdbcTemplate.update(sqlDelTAAKademikMhs, id);
        
        String sqlDelTopikBim = "DELETE FROM TopikBimbingan WHERE IdTA IN (SELECT IdTa FROM TugasAkhir WHERE IdMahasiswa = ?)";
        jdbcTemplate.update(sqlDelTopikBim, id);
        
        String sqlDelDosBingMhs = "DELETE FROM Dosen_Pembimbing WHERE idTA IN (SELECT IdTa FROM TugasAkhir WHERE IdMahasiswa = ?)";
        jdbcTemplate.update(sqlDelDosBingMhs, id);
        
        String sqlDelTAMhs = "DELETE FROM TugasAkhir WHERE IdMahasiswa = ?"; 
        jdbcTemplate.update(sqlDelTAMhs, id);

        String sqlDelAdmin = "DELETE FROM Admin WHERE IdPengguna = ?";
        jdbcTemplate.update(sqlDelAdmin, id);
        
        String sqlDelMhs = "DELETE FROM Mahasiswa WHERE IdPengguna = ?";
        jdbcTemplate.update(sqlDelMhs, id);

        String sqlDelDosen = "DELETE FROM Dosen WHERE IdPengguna = ?";
        jdbcTemplate.update(sqlDelDosen, id);

        String sqlDelPengguna = "DELETE FROM Pengguna WHERE IdPengguna = ?";
        jdbcTemplate.update(sqlDelPengguna, id); 
    }

    @Override
    public List<Pengguna> findAll() {
        String sql = "SELECT * FROM Pengguna";
        return jdbcTemplate.query(sql, this::mapRowToPengguna);
    }

    @Override
    public boolean getStatus(String idPengguna) {
        String sql = "SELECT statusAktif FROM Pengguna WHERE IdPengguna = ?";
        Boolean statusPengguna = jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                idPengguna
        );
        return statusPengguna != null ? statusPengguna : false;
    }
    @Override
    public void ubahStatus(String idPengguna) {
        boolean statusPost = !getStatus(idPengguna);
        String sql = "UPDATE Pengguna set statusAktif = ? where IdPengguna = ?";
        jdbcTemplate.update(sql, statusPost, idPengguna);
    }
}