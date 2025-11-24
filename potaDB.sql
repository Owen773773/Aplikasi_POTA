CREATE TABLE Pengguna (
    IdPengguna VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE, -- Ditambahkan UNIQUE
    password VARCHAR(255) NOT NULL, -- Diperpanjang untuk keamanan (disarankan)
    nama VARCHAR(70) NOT NULL,
    statusAktif BOOLEAN DEFAULT TRUE,
    tipeAkun VARCHAR(10) CHECK (tipeAkun IN ('Mahasiswa', 'Dosen', 'Admin')), -- Ditambahkan CHECK
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
    IdMahasiswa VARCHAR(50) NOT NULL, -- Diperbaiki: VARCHAR(50)
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
    IdDosen VARCHAR(50), -- Diperbaiki: VARCHAR(50)
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
    Status VARCHAR(20),
    JumlahPeserta INT,
    idRuangan INT,
    FOREIGN KEY (idRuangan) REFERENCES Ruangan(idRuangan)
);

CREATE TABLE TopikBimbingan (
    IdBim INT,
    IdTA INT,
    PRIMARY KEY (IdBim, IdTA),
    FOREIGN KEY (IdBim) REFERENCES Bimbingan(IdBim),
    FOREIGN KEY (IdTA) REFERENCES TugasAkhir(IdTa)
);

CREATE TABLE MahasiswaProsesBimbingan (
    IdMahasiswa VARCHAR(50), -- Diperbaiki: VARCHAR(50)
    IdBimbingan INT,
    tipe VARCHAR(50),
    PRIMARY KEY (IdMahasiswa, IdBimbingan),
    FOREIGN KEY (IdMahasiswa) REFERENCES Mahasiswa(IdPengguna),
    FOREIGN KEY (IdBimbingan) REFERENCES Bimbingan(IdBim)
);

CREATE TABLE DosenProsesBimbingan (
    IdDosen VARCHAR(50), -- Diperbaiki: VARCHAR(50)
    IdBimbingan INT,
    tipe VARCHAR(50),
    PRIMARY KEY (IdDosen, IdBimbingan),
    FOREIGN KEY (IdDosen) REFERENCES Dosen(IdPengguna),
    FOREIGN KEY (IdBimbingan) REFERENCES Bimbingan(IdBim)
);

CREATE TABLE Jadwal (
    IdJadwal INT PRIMARY KEY,
    tanggal DATE,
    WaktuMulai TIME,
    WaktuSelesai TIME,
    berulang INT
);

CREATE TABLE Jadwal_Pribadi (
    IdJadwal INT, -- Tidak perlu PK tunggal, PK komposit lebih baik
    IdPengguna VARCHAR(50), -- Diperbaiki: VARCHAR(50)
    PRIMARY KEY (IdJadwal, IdPengguna), -- Disarankan PK komposit
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
    idAdmin VARCHAR(50), -- Diperbaiki: VARCHAR(50)
    IdJadwal INT,
    Waktu TIMESTAMP,
    Alasan TEXT,
    PRIMARY KEY (IdRuangan, idAdmin, IdJadwal),
    FOREIGN KEY (IdRuangan) REFERENCES Ruangan(idRuangan),
    FOREIGN KEY (idAdmin) REFERENCES Admin(IdPengguna),
    FOREIGN KEY (IdJadwal) REFERENCES Jadwal(IdJadwal)
);

CREATE TABLE Notifikasi (
    idNotifikasi INT PRIMARY KEY,
    tipeNotif VARCHAR(50),
    waktuAcara TIMESTAMP
);

CREATE TABLE MahasiswaNotifikasi (
    IdPengguna VARCHAR(50), -- Diperbaiki: VARCHAR(50)
    IdNotifikasi INT,
    PRIMARY KEY (IdPengguna, IdNotifikasi),
    FOREIGN KEY (IdPengguna) REFERENCES Mahasiswa(IdPengguna),
    FOREIGN KEY (IdNotifikasi) REFERENCES Notifikasi(idNotifikasi)
);

CREATE TABLE DosenNotifikasi (
    IdPengguna VARCHAR(50), -- Diperbaiki: VARCHAR(50)
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