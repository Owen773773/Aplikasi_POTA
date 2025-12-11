DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

DROP TABLE IF EXISTS BimbinganNotifikasi CASCADE;
DROP TABLE IF EXISTS DosenNotifikasi CASCADE;
DROP TABLE IF EXISTS MahasiswaNotifikasi CASCADE;
DROP TABLE IF EXISTS Notifikasi CASCADE;
DROP TABLE IF EXISTS PemblokiranRuangan CASCADE;
DROP TABLE IF EXISTS PenjadwalanBimbingan CASCADE;
DROP TABLE IF EXISTS Jadwal_Bimbingan CASCADE;
DROP TABLE IF EXISTS Jadwal_Ruangan CASCADE;
DROP TABLE IF EXISTS Jadwal_Pribadi CASCADE;
DROP TABLE IF EXISTS Jadwal CASCADE;
DROP TABLE IF EXISTS TopikBimbingan CASCADE;
DROP TABLE IF EXISTS Bimbingan CASCADE;
DROP TABLE IF EXISTS Dosen_Pembimbing CASCADE;
DROP TABLE IF EXISTS TAtermasukAkademik CASCADE;
DROP TABLE IF EXISTS TugasAkhir CASCADE;
DROP TABLE IF EXISTS Ruangan CASCADE;
DROP TABLE IF EXISTS Akademik CASCADE;
DROP TABLE IF EXISTS Admin CASCADE;
DROP TABLE IF EXISTS Dosen CASCADE;
DROP TABLE IF EXISTS Mahasiswa CASCADE;
DROP TABLE IF EXISTS Pengguna CASCADE;

CREATE TABLE Pengguna
(
    IdPengguna  VARCHAR(50) PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nama        VARCHAR(70)  NOT NULL,
    statusAktif BOOLEAN DEFAULT TRUE,
    tipeAkun    VARCHAR(10) CHECK (tipeAkun IN ('Mahasiswa', 'Dosen', 'Admin')),
    lastLogin   TIMESTAMP
);

CREATE TABLE Mahasiswa
(
    IdPengguna VARCHAR(50) PRIMARY KEY,
    TahapTA    INT,
    FOREIGN KEY (IdPengguna) REFERENCES Pengguna (IdPengguna)
);

CREATE TABLE Dosen
(
    IdPengguna VARCHAR(50) PRIMARY KEY,
    FOREIGN KEY (IdPengguna) REFERENCES Pengguna (IdPengguna)
);

CREATE TABLE Admin
(
    IdPengguna VARCHAR(50) PRIMARY KEY,
    FOREIGN KEY (IdPengguna) REFERENCES Pengguna (IdPengguna)
);

CREATE TABLE Akademik
(
    idSemester   INT PRIMARY KEY,
    minimumPra   INT,
    minimumPasca INT
);

CREATE TABLE Ruangan
(
    idRuangan   INT PRIMARY KEY,
    namaRuangan VARCHAR(50)
);

CREATE TABLE TugasAkhir
(
    IdTa        INT PRIMARY KEY,
    TopikTA     VARCHAR(150),
    TanggalUTS  DATE,
    TanggalUas  DATE,
    IdMahasiswa VARCHAR(50) NOT NULL,
    FOREIGN KEY (IdMahasiswa) REFERENCES Mahasiswa (IdPengguna)
);

CREATE TABLE TAtermasukAkademik
(
    IdTA       INT,
    idAkademik INT,
    PRIMARY KEY (IdTA, idAkademik),
    FOREIGN KEY (IdTA) REFERENCES TugasAkhir (IdTa),
    FOREIGN KEY (idAkademik) REFERENCES Akademik (idSemester)
);

CREATE TABLE Dosen_Pembimbing
(
    IdDosen VARCHAR(50),
    idTA    INT,
    PRIMARY KEY (IdDosen, idTA),
    FOREIGN KEY (IdDosen) REFERENCES Dosen (IdPengguna),
    FOREIGN KEY (idTA) REFERENCES TugasAkhir (IdTa)
);

CREATE TABLE Bimbingan
(
    IdBim         serial PRIMARY KEY,
    DeskripsiBim  TEXT,
    Catatan       TEXT,
    TopikBim      VARCHAR(150),
    JumlahPeserta INT,
    idRuangan     INT,
    FOREIGN KEY (idRuangan) REFERENCES Ruangan (idRuangan)
);

CREATE TABLE TopikBimbingan
(
    IdBim           INT,
    IdTA            INT,
    StatusMhs       VARCHAR(20),
    StatusDosen1    VARCHAR(20),
    StatusDosen2    VARCHAR(20),
    StatusBimbingan VARCHAR(20),
    PRIMARY KEY (IdBim, IdTA),
    FOREIGN KEY (IdBim) REFERENCES Bimbingan (IdBim),
    FOREIGN KEY (IdTA) REFERENCES TugasAkhir (IdTa)
);

CREATE TABLE Jadwal
(
    IdJadwal     SERIAL PRIMARY KEY,
    tanggal      DATE,
    WaktuMulai   TIME,
    WaktuSelesai TIME,
    berulang     INT
);

CREATE TABLE Jadwal_Pribadi
(
    IdJadwal   INT,
    IdPengguna VARCHAR(50),
    PRIMARY KEY (IdJadwal, IdPengguna),
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal (IdJadwal),
    FOREIGN KEY (IdPengguna) REFERENCES Pengguna (IdPengguna)
);

CREATE TABLE Jadwal_Ruangan
(
    IdJadwal INT PRIMARY KEY,
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal (IdJadwal)
);

CREATE TABLE Jadwal_Bimbingan
(
    IdJadwal INT PRIMARY KEY,
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal (IdJadwal)
);

CREATE TABLE PenjadwalanBimbingan
(
    IdJadwal INT,
    IdBim    INT,
    PRIMARY KEY (IdJadwal, IdBim),
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal_Bimbingan (IdJadwal),
    FOREIGN KEY (IdBim) REFERENCES Bimbingan (IdBim)
);

CREATE TABLE PemblokiranRuangan
(
    IdRuangan INT,
    idAdmin   VARCHAR(50),
    IdJadwal  INT,
    Waktu     TIMESTAMP,
    Alasan    TEXT,
    PRIMARY KEY (IdRuangan, idAdmin, IdJadwal),
    FOREIGN KEY (IdRuangan) REFERENCES Ruangan (idRuangan),
    FOREIGN KEY (idAdmin) REFERENCES Admin (IdPengguna),
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal (IdJadwal)
);

CREATE TABLE Notifikasi
(
    idNotifikasi SERIAL PRIMARY KEY,
    tipeNotif    VARCHAR(50),
    waktuAcara   TIMESTAMP
);

CREATE TABLE MahasiswaNotifikasi
(
    IdPengguna   VARCHAR(50),
    IdNotifikasi INT,
    PRIMARY KEY (IdPengguna, IdNotifikasi),
    FOREIGN KEY (IdPengguna) REFERENCES Mahasiswa (IdPengguna),
    FOREIGN KEY (IdNotifikasi) REFERENCES Notifikasi (idNotifikasi)
);

CREATE TABLE DosenNotifikasi
(
    IdPengguna   VARCHAR(50),
    IdNotifikasi INT,
    PRIMARY KEY (IdPengguna, IdNotifikasi),
    FOREIGN KEY (IdPengguna) REFERENCES Dosen (IdPengguna),
    FOREIGN KEY (IdNotifikasi) REFERENCES Notifikasi (idNotifikasi)
);

CREATE TABLE BimbinganNotifikasi
(
    IdNotifikasi INT,
    IdBim        INT,
    PRIMARY KEY (IdNotifikasi, IdBim),
    FOREIGN KEY (IdNotifikasi) REFERENCES Notifikasi (idNotifikasi),
    FOREIGN KEY (IdBim) REFERENCES Bimbingan (IdBim)
);

-- ========== INSERT DATA ==========

INSERT INTO Pengguna VALUES
                         ('U001', 'mhs1', 'pass', 'Mahasiswa Satu', TRUE, 'Mahasiswa', NOW()),
                         ('U002', 'mhs2', 'pass', 'Mahasiswa Dua', TRUE, 'Mahasiswa', NOW()),
                         ('U003', 'dsn1', 'pass', 'Dosen Satu', TRUE, 'Dosen', NOW()),
                         ('U004', 'adm1', 'pass', 'Admin Satu', TRUE, 'Admin', NOW()),
                         ('U005', 'dsn2', 'pass', 'Dosen Dua', TRUE, 'Dosen', NOW()),
                         ('U006', 'mhs3', 'pass', 'Mahasiswa Tiga', TRUE, 'Mahasiswa', NOW()),
                         ('U007', 'mhs4', 'pass', 'Mahasiswa Empat', TRUE, 'Mahasiswa', NOW()),
                         ('U008', 'mhs5', 'pass', 'Mahasiswa Lima', TRUE, 'Mahasiswa', NOW()),
                         ('U009', 'dsn3', 'pass', 'Dosen Tiga', TRUE, 'Dosen', NOW()),
                         ('U010', 'dsn4', 'pass', 'Dosen Empat', TRUE, 'Dosen', NOW()),
                         ('U011', 'dsn5', 'pass', 'Dosen Lima', TRUE, 'Dosen', NOW()),
                         ('U012', 'adm2', 'pass', 'Admin Dua', TRUE, 'Admin', NOW()),
                         ('U013', 'adm3', 'pass', 'Admin Tiga', TRUE, 'Admin', NOW()),
                         ('U014', 'adm4', 'pass', 'Admin Empat', TRUE, 'Admin', NOW()),
                         ('U015', 'adm5', 'pass', 'Admin Lima', TRUE, 'Admin', NOW());

INSERT INTO Mahasiswa VALUES
                          ('U001', 1),
                          ('U002', 2),
                          ('U006', 1),
                          ('U007', 2),
                          ('U008', 3);

INSERT INTO Dosen VALUES
                      ('U003'),
                      ('U005'),
                      ('U009'),
                      ('U010'),
                      ('U011');

INSERT INTO Admin VALUES
                      ('U004'),
                      ('U012'),
                      ('U013'),
                      ('U014'),
                      ('U015');

INSERT INTO Akademik VALUES
                         (20241, 2, 3),
                         (20242, 2, 3),
                         (20243, 2, 3),
                         (20244, 2, 3),
                         (20245, 2, 3);

INSERT INTO Ruangan VALUES
                        (1, 'Ruang A'),
                        (2, 'Ruang B'),
                        (3, 'Ruang C'),
                        (4, 'Ruang D'),
                        (5, 'Ruang E');

-- PERBAIKAN: Tanggal UTS dan UAS yang realistis
-- Current date: 10 Desember 2025
-- UTS: Pertengahan Januari 2026 (sekitar 5-6 minggu dari sekarang)
-- UAS: Akhir Februari 2026 (3-4 minggu setelah UTS)
INSERT INTO TugasAkhir VALUES
                           (101, 'Sistem Informasi Akademik Berbasis Web', '2026-01-15', '2026-02-20', 'U001'),
                           (102, 'Aplikasi Mobile untuk E-Commerce', '2026-01-15', '2026-02-20', 'U002'),
                           (103, 'Analisis Data Mining untuk Prediksi Penjualan', '2026-01-20', '2026-02-25', 'U006'),
                           (104, 'Sistem Rekomendasi Film Menggunakan Machine Learning', '2026-01-20', '2026-02-25', 'U007'),
                           (105, 'Dashboard Monitoring IoT Real-time', '2026-01-22', '2026-02-27', 'U008');

INSERT INTO TAtermasukAkademik VALUES
                                   (101, 20241),
                                   (102, 20241),
                                   (103, 20242),
                                   (104, 20242),
                                   (105, 20243);

INSERT INTO Dosen_Pembimbing VALUES
                                 ('U003', 101),  -- Dosen 1 membimbing TA 101 (U001)
                                 ('U005', 101),  -- Dosen 2 co-pembimbing TA 101
                                 ('U003', 102),  -- Dosen 1 membimbing TA 102 (U002)
                                 ('U009', 103),  -- Dosen 3 membimbing TA 103 (U006)
                                 ('U010', 104),  -- Dosen 4 membimbing TA 104 (U007)
                                 ('U011', 105);  -- Dosen 5 membimbing TA 105 (U008)

INSERT INTO Bimbingan (DeskripsiBim, Catatan, TopikBim, JumlahPeserta, idRuangan) VALUES
                                                                                      ('Diskusi progress Bab 1', 'Revisi metodologi', 'Progress Bab 1', 1, 1),
                                                                                      ('Review literatur dan teori', 'Tambah referensi', 'Review Bab 2', 1, 2),
                                                                                      ('Konsultasi implementasi sistem', 'Perbaiki database design', 'Implementasi', 1, 3),
                                                                                      ('Diskusi hasil testing', 'Ada beberapa bug', 'Testing & Debugging', 1, 4),
                                                                                      ('Persiapan presentasi', 'Slide sudah oke', 'Persiapan Sidang', 1, 5),
                                                                                      ('Pengajuan topik baru', '-', 'Pengajuan Judul', 1, 1),
                                                                                      ('Konsultasi metodologi', '-', 'Metodologi Penelitian', 1, 2);

INSERT INTO TopikBimbingan VALUES
                               (1, 101, 'Menyetujui', 'Menyetujui', 'Menyetujui', 'Terjadwalkan'),
                               (2, 102, 'Menyetujui', 'Menyetujui', NULL, 'Terjadwalkan'),
                               (3, 103, 'Menyetujui', 'Menolak', NULL, 'Gagal'),
                               (4, 104, 'Dibatalkan', 'Menunggu', NULL, 'Gagal'),
                               (5, 105, 'Menyetujui', 'Tervalidasi', NULL, 'Selesai'),
                               (6, 101, 'Menunggu', 'Menyetujui', 'Menunggu', 'Proses'),
                               (7, 102, 'Menunggu', 'Menyetujui', NULL, 'Proses');

-- PERBAIKAN: Jadwal bimbingan di rentang Desember 2025
INSERT INTO Jadwal(tanggal, WaktuMulai, WaktuSelesai, berulang) VALUES
                                                                    -- Bimbingan yang sudah lewat (minggu lalu)
                                                                    ('2025-12-02', '09:00', '11:00', 0),
                                                                    ('2025-12-03', '10:00', '12:00', 0),
                                                                    ('2025-12-04', '13:00', '15:00', 0),
                                                                    ('2025-12-05', '14:00', '16:00', 0),
                                                                    ('2025-12-06', '09:00', '11:00', 0),

                                                                    -- Bimbingan minggu ini (10 Des 2025 = Rabu)
                                                                    ('2025-12-11', '09:00', '11:00', 0),  -- Besok (Kamis)
                                                                    ('2025-12-12', '13:00', '15:00', 0),  -- Jumat

                                                                    -- Jadwal pribadi (kelas/acara pribadi)
                                                                    ('2025-12-11', '14:00', '16:00', 0),  -- U001 ada kelas
                                                                    ('2025-12-12', '09:00', '11:00', 0),  -- U002 ada kelas
                                                                    ('2025-12-16', '10:00', '12:00', 0),  -- U003 ada rapat
                                                                    ('2025-12-17', '13:00', '15:00', 0);  -- U006 ada kelas

INSERT INTO Jadwal_Pribadi VALUES
                               (8, 'U001'),   -- Mahasiswa 1 ada kelas Kamis 14.00
                               (9, 'U002'),   -- Mahasiswa 2 ada kelas Jumat 09.00
                               (10, 'U003'),  -- Dosen 1 ada rapat Senin 10.00
                               (11, 'U006');  -- Mahasiswa 3 ada kelas Selasa 13.00

INSERT INTO Jadwal_Ruangan VALUES
                               (1), (2), (3), (4), (5);

INSERT INTO Jadwal_Bimbingan VALUES
                                 (1), (2), (3), (4), (5), (6), (7);

INSERT INTO PenjadwalanBimbingan VALUES
                                     (1, 1),  -- Jadwal 1 untuk Bimbingan 1
                                     (2, 2),  -- Jadwal 2 untuk Bimbingan 2
                                     (3, 3),  -- dst...
                                     (4, 4),
                                     (5, 5),
                                     (6, 6),
                                     (7, 7);

INSERT INTO PemblokiranRuangan VALUES
                                   (1, 'U004', 1, NOW(), 'Perbaikan AC dan proyektor'),
                                   (2, 'U012', 2, NOW(), 'Pembersihan rutin semester'),
                                   (3, 'U013', 3, NOW(), 'Acara seminar departemen'),
                                   (4, 'U014', 4, NOW(), 'Maintenance jaringan'),
                                   (5, 'U015', 5, NOW(), 'Kerusakan kursi dan meja');

INSERT INTO Notifikasi (tipeNotif, waktuAcara) VALUES
                                                   ('Diterima', NOW() - INTERVAL '5 days'),
                                                   ('Menunggu', NOW() - INTERVAL '3 days'),
                                                   ('Ditolak', NOW() - INTERVAL '4 days'),
                                                   ('Dibatalkan', NOW() - INTERVAL '2 days'),
                                                   ('Ditolak', NOW() - INTERVAL '6 days'),
                                                   ('Menunggu', NOW() - INTERVAL '1 day'),
                                                   ('Menunggu', NOW());

INSERT INTO MahasiswaNotifikasi VALUES
                                    ('U001', 1),
                                    ('U002', 2),
                                    ('U006', 3),
                                    ('U007', 4),
                                    ('U008', 5),
                                    ('U001', 6),
                                    ('U002', 7);

INSERT INTO DosenNotifikasi VALUES
                                ('U003', 1),
                                ('U003', 2),
                                ('U009', 3),
                                ('U010', 4),
                                ('U011', 5),
                                ('U003', 6),
                                ('U003', 7);

INSERT INTO BimbinganNotifikasi VALUES
                                    (1, 1),
                                    (2, 2),
                                    (3, 3),
                                    (4, 4),
                                    (5, 5),
                                    (6, 6),
                                    (7, 7);
