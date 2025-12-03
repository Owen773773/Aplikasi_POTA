
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

CREATE TABLE Pengguna (
    IdPengguna VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nama VARCHAR(70) NOT NULL,
    statusAktif BOOLEAN DEFAULT TRUE,
    tipeAkun VARCHAR(10) CHECK (tipeAkun IN ('Mahasiswa', 'Dosen', 'Admin')),
    lastLogin TIMESTAMP
);

CREATE TABLE Mahasiswa (
    IdPengguna VARCHAR(50) PRIMARY KEY,
    TahapTA INT,
    FOREIGN KEY (IdPengguna) REFERENCES Pengguna(IdPengguna)
);

CREATE TABLE Dosen (
    IdPengguna VARCHAR(50) PRIMARY KEY,
    FOREIGN KEY (IdPengguna) REFERENCES Pengguna(IdPengguna)
);

CREATE TABLE Admin (
    IdPengguna VARCHAR(50) PRIMARY KEY,
    FOREIGN KEY (IdPengguna) REFERENCES Pengguna(IdPengguna)
);

CREATE TABLE Akademik (
    idSemester INT PRIMARY KEY,
    minimumPra INT,
    minimumPasca INT
);

CREATE TABLE Ruangan (
    idRuangan INT PRIMARY KEY,
    namaRuangan VARCHAR(50)
);

CREATE TABLE TugasAkhir (
    IdTa INT PRIMARY KEY,
    TopikTA VARCHAR(150),
    TanggalUTS DATE,
    TanggalUas DATE,
    IdMahasiswa VARCHAR(50) NOT NULL,
    FOREIGN KEY (IdMahasiswa) REFERENCES Mahasiswa(IdPengguna)
);

CREATE TABLE TAtermasukAkademik (
    IdTA INT,
    idAkademik INT,
    PRIMARY KEY (IdTA, idAkademik),
    FOREIGN KEY (IdTA) REFERENCES TugasAkhir(IdTa),
    FOREIGN KEY (idAkademik) REFERENCES Akademik(idSemester)
);

CREATE TABLE Dosen_Pembimbing (
    IdDosen VARCHAR(50),
    idTA INT,
    PRIMARY KEY (IdDosen, idTA),
    FOREIGN KEY (IdDosen) REFERENCES Dosen(IdPengguna),
    FOREIGN KEY (idTA) REFERENCES TugasAkhir(IdTa)
);

CREATE TABLE Bimbingan (
    IdBim INT PRIMARY KEY,
    DeskripsiBim TEXT,
    Catatan TEXT,
    TopikBim VARCHAR(150),
    JumlahPeserta INT,
    idRuangan INT,
    FOREIGN KEY (idRuangan) REFERENCES Ruangan(idRuangan)
);

CREATE TABLE TopikBimbingan (
	IdBim INT,
    IdTA INT,
    StatusMhs VARCHAR(20),
    StatusDosen1 VARCHAR(20),
    StatusDosen2 VARCHAR(20),
	StatusBimbingan VARCHAR (20),
    PRIMARY KEY (IdBim, IdTA),
    FOREIGN KEY (IdBim) REFERENCES Bimbingan(IdBim),
    FOREIGN KEY (IdTA) REFERENCES TugasAkhir(IdTa)
);

CREATE TABLE Jadwal (
    IdJadwal SERIAL PRIMARY KEY,
    tanggal DATE,
    WaktuMulai TIME,
    WaktuSelesai TIME,
    berulang INT
);

CREATE TABLE Jadwal_Pribadi (
    IdJadwal INT,
    IdPengguna VARCHAR(50),
    PRIMARY KEY (IdJadwal, IdPengguna),
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal(IdJadwal),
    FOREIGN KEY (IdPengguna) REFERENCES Pengguna(IdPengguna)
);

CREATE TABLE Jadwal_Ruangan (
    IdJadwal INT PRIMARY KEY,
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal(IdJadwal)
    -- Catatan: Tabel ini kurang informatif. Harusnya ada kolom idRuangan.
);

CREATE TABLE Jadwal_Bimbingan (
    IdJadwal INT PRIMARY KEY,
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal(IdJadwal)
);

CREATE TABLE PenjadwalanBimbingan (
    IdJadwal INT,
    IdBim INT,
    PRIMARY KEY (IdJadwal, IdBim),
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal_Bimbingan(IdJadwal),
    FOREIGN KEY (IdBim) REFERENCES Bimbingan(IdBim)
);

CREATE TABLE PemblokiranRuangan (
    IdRuangan INT,
    idAdmin VARCHAR(50),
    IdJadwal INT,
    Waktu TIMESTAMP,
    Alasan TEXT,
    PRIMARY KEY (IdRuangan, idAdmin, IdJadwal),
    FOREIGN KEY (IdRuangan) REFERENCES Ruangan(idRuangan),
    FOREIGN KEY (idAdmin) REFERENCES Admin(IdPengguna),
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal(IdJadwal)
);

CREATE TABLE Notifikasi (
    idNotifikasi SERIAL PRIMARY KEY,
    tipeNotif VARCHAR(50),
    waktuAcara TIMESTAMP
);

CREATE TABLE MahasiswaNotifikasi (
    IdPengguna VARCHAR(50),
    IdNotifikasi INT,
    PRIMARY KEY (IdPengguna, IdNotifikasi),
    FOREIGN KEY (IdPengguna) REFERENCES Mahasiswa(IdPengguna),
    FOREIGN KEY (IdNotifikasi) REFERENCES Notifikasi(idNotifikasi)
);

CREATE TABLE DosenNotifikasi (
    IdPengguna VARCHAR(50),
    IdNotifikasi INT,
    PRIMARY KEY (IdPengguna, IdNotifikasi),
    FOREIGN KEY (IdPengguna) REFERENCES Dosen(IdPengguna),
    FOREIGN KEY (IdNotifikasi) REFERENCES Notifikasi(idNotifikasi)
);

CREATE TABLE BimbinganNotifikasi (
    IdNotifikasi INT,
    IdBim INT,
    PRIMARY KEY (IdNotifikasi, IdBim),
    FOREIGN KEY (IdNotifikasi) REFERENCES Notifikasi(idNotifikasi),
    FOREIGN KEY (IdBim) REFERENCES Bimbingan(IdBim)
);

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

INSERT INTO TugasAkhir VALUES
(101, 'Topik A', '2025-03-01', '2025-06-01', 'U001'),
(102, 'Topik B', '2025-03-01', '2025-06-01', 'U002'),
(103, 'Topik C', '2025-03-01', '2025-06-01', 'U006'),
(104, 'Topik D', '2025-03-01', '2025-06-01', 'U007'),
(105, 'Topik E', '2025-03-01', '2025-06-01', 'U008');

INSERT INTO TAtermasukAkademik VALUES
(101, 20241),
(102, 20241),
(103, 20242),
(104, 20242),
(105, 20243);

INSERT INTO Dosen_Pembimbing VALUES
('U003', 101),
('U005', 102),
('U009', 103),
('U010', 104),
('U011', 105);

INSERT INTO Bimbingan VALUES
(1, 'Diskusi awal', 'Catatan 1', 'Topik A', 1, 1),
(2, 'Review Bab 1', 'Catatan 2', 'Topik B', 2, 2),
(3, 'Review Bab 2', 'Catatan 3', 'Topik C', 3, 3),
(4, 'Review Bab 3', 'Catatan 4', 'Topik D',  4, 4),
(5, 'Akhir', 'Catatan 5', 'Topik E', 5, 5);

INSERT INTO TopikBimbingan VALUES
(1, 101, 'Menunggu', 'Menunggu', NULL, 'Terjadwalkan'),
(2, 102, 'Menerima', 'Menerima', 'Menunggu', 'Terjadwalkan'),
(3, 103, 'Menerima', 'Menolak', NULL, 'Gagal'),
(4, 104, 'Dibatalkan', NULL, NULL, 'Gagal'),
(5, 105, 'Menerima', 'Menerima', 'Menerima', 'Selesai');

INSERT INTO Jadwal VALUES
(1, '2025-11-24', '08:00', '10:00', 0), 
(2, '2025-11-25', '08:00', '10:00', 0), 
(3, '2025-11-26', '10:00', '12:00', 1), 
(4, '2025-11-26', '13:00', '15:00', 0), 
(5, '2025-11-27', '09:00', '11:00', 1), 
(6, '2025-11-28', '14:00', '16:00', 0);

INSERT INTO Jadwal_Pribadi VALUES
(1, 'U001'), 
(2, 'U002'), 
(3, 'U003'), 
(4, 'U004'), 
(5, 'U005'),
(6, 'U006');

INSERT INTO Jadwal_Ruangan VALUES
(1),
(2),
(3),
(4),
(5);

INSERT INTO Jadwal_Bimbingan VALUES
(1),
(2),
(3),
(4),
(5);

INSERT INTO PenjadwalanBimbingan VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

INSERT INTO PemblokiranRuangan VALUES
(1, 'U004', 1, NOW(), 'Perbaikan'),
(2, 'U012', 2, NOW(), 'Pembersihan'),
(3, 'U013', 3, NOW(), 'Acara Khusus'),
(4, 'U014', 4, NOW(), 'Maintenance'),
(5, 'U015', 5, NOW(), 'Kerusakan');

INSERT INTO Notifikasi VALUES
(1, 'Diterima', NOW()),
(2, 'Menunggu', NOW()),
(3, 'Ditolak', NOW()),
(4, 'Dibatalkan', NOW()),
(5, 'Ditolak', NOW());

INSERT INTO MahasiswaNotifikasi VALUES
('U001', 1),
('U002', 2),
('U006', 3),
('U007', 4),
('U008', 5);

INSERT INTO DosenNotifikasi VALUES
('U003', 1),
('U005', 2),
('U009', 3),
('U010', 4),
('U011', 5);

INSERT INTO BimbinganNotifikasi VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

