-- 1. INSERT PENGGUNA
INSERT INTO Pengguna (IdPengguna, username, password, nama, statusAktif, tipeAkun, lastLogin) VALUES
('ADM001', 'admin1', 'pass123', 'Admin Utama', TRUE, 'Admin', '2025-11-23 10:00:00'),
('DSN001', 'dosen1', 'pass123', 'Dr. Budi Santoso', TRUE, 'Dosen', '2025-11-23 09:00:00'),
('DSN002', 'dosen2', 'pass123', 'Prof. Siti Rahayu', TRUE, 'Dosen', '2025-11-23 08:30:00'),
('MHS001', 'mhs001', 'pass123', 'Ahmad Wijaya', TRUE, 'Mahasiswa', '2025-11-23 11:00:00'),
('MHS002', 'mhs002', 'pass123', 'Sari Kusuma', TRUE, 'Mahasiswa', '2025-11-23 11:30:00');

-- 2. INSERT ROLE SPECIFIC
INSERT INTO Admin (IdPengguna) VALUES ('ADM001');
INSERT INTO Dosen (IdPengguna) VALUES ('DSN001'), ('DSN002');
INSERT INTO Mahasiswa (IdPengguna, TahapTA) VALUES ('MHS001', 1), ('MHS002', 2);

-- 3. INSERT RUANGAN
INSERT INTO Ruangan (idRuangan, namaRuangan) VALUES
(1, 'Ruang Tugas Akhir A'),
(2, 'Ruang Tugas Akhir B'),
(3, 'Ruang Meeting');

-- 4. INSERT TUGAS AKHIR
INSERT INTO TugasAkhir (IdTa, TopikTA, TanggalUTS, TanggalUas, IdMahasiswa) VALUES
(1, 'Sistem Informasi Manajemen Bimbingan', '2025-12-15', '2026-01-20', 'MHS001'),
(2, 'Aplikasi Mobile Learning', '2025-12-16', '2026-01-21', 'MHS002');

-- 5. INSERT BIMBINGAN
-- Bimbingan dengan berbagai status
INSERT INTO Bimbingan (IdBim, DeskripsiBim, Catatan, TopikBim, Status, JumlahPeserta, idRuangan) VALUES
(1, 'Bimbingan BAB 1', 'Pembahasan Latar Belakang', 'Pendahuluan', 'CONFIRMED', 1, 1),
(2, 'Bimbingan BAB 2', 'Review Literatur', 'Tinjauan Pustaka', 'PENDING', 1, 1),
(3, 'Bimbingan BAB 3', 'Diskusi Metodologi', 'Metodologi Penelitian', 'CONFIRMED', 2, 2),
(4, 'Bimbingan Progress', 'Laporan Progress', 'Semua BAB', 'PENDING', 1, 1);

-- 6. INSERT JADWAL
-- Minggu 24-28 November 2025

-- SENIN (24 Nov 2025)
INSERT INTO Jadwal (IdJadwal, tanggal, WaktuMulai, WaktuSelesai, berulang) VALUES
(1, '2025-11-24', '08:00:00', '10:00:00', 0),  -- Pemblokiran Ruang 1
(2, '2025-11-24', '13:00:00', '15:00:00', 0),  -- Bimbingan CONFIRMED (Ruang 1)
(3, '2025-11-24', '10:00:00', '12:00:00', 0);  -- Bimbingan PENDING (Ruang 2)

-- SELASA (25 Nov 2025)
INSERT INTO Jadwal (IdJadwal, tanggal, WaktuMulai, WaktuSelesai, berulang) VALUES
(4, '2025-11-25', '09:00:00', '11:00:00', 0),  -- Bimbingan CONFIRMED (Ruang 1)
(5, '2025-11-25', '14:00:00', '16:00:00', 0);  -- Pemblokiran Ruang 2

-- RABU (26 Nov 2025)
INSERT INTO Jadwal (IdJadwal, tanggal, WaktuMulai, WaktuSelesai, berulang) VALUES
(6, '2025-11-26', '08:00:00', '09:00:00', 0),  -- Pemblokiran Ruang 1
(7, '2025-11-26', '10:00:00', '12:00:00', 0),  -- Bimbingan PENDING (Ruang 1)
(8, '2025-11-26', '13:00:00', '15:00:00', 0);  -- Bimbingan CONFIRMED (Ruang 2)

-- KAMIS (27 Nov 2025)
INSERT INTO Jadwal (IdJadwal, tanggal, WaktuMulai, WaktuSelesai, berulang) VALUES
(9, '2025-11-27', '09:00:00', '10:00:00', 0),   -- Bimbingan PENDING (Ruang 1)
(10, '2025-11-27', '11:00:00', '13:00:00', 0),  -- Pemblokiran Ruang 2
(11, '2025-11-27', '14:00:00', '16:00:00', 0);  -- Bimbingan CONFIRMED (Ruang 1)

-- JUMAT (28 Nov 2025)
INSERT INTO Jadwal (IdJadwal, tanggal, WaktuMulai, WaktuSelesai, berulang) VALUES
(12, '2025-11-28', '08:00:00', '10:00:00', 0),  -- Bimbingan CONFIRMED (Ruang 2)
(13, '2025-11-28', '13:00:00', '15:00:00', 0);  -- Pemblokiran Ruang 1

-- 7. INSERT JADWAL_BIMBINGAN (untuk jadwal yang merupakan bimbingan)
INSERT INTO Jadwal_Bimbingan (IdJadwal) VALUES
(2),   -- Senin 13:00-15:00 CONFIRMED
(3),   -- Senin 10:00-12:00 PENDING
(4),   -- Selasa 09:00-11:00 CONFIRMED
(7),   -- Rabu 10:00-12:00 PENDING
(8),   -- Rabu 13:00-15:00 CONFIRMED
(9),   -- Kamis 09:00-10:00 PENDING
(11),  -- Kamis 14:00-16:00 CONFIRMED
(12);  -- Jumat 08:00-10:00 CONFIRMED

-- 8. INSERT PENJADWALAN_BIMBINGAN (link jadwal bimbingan ke bimbingan)
INSERT INTO PenjadwalanBimbingan (IdJadwal, IdBim) VALUES
(2, 1),   -- Senin 13:00 - Bimbingan BAB 1 (CONFIRMED)
(3, 2),   -- Senin 10:00 - Bimbingan BAB 2 (PENDING)
(4, 1),   -- Selasa 09:00 - Bimbingan BAB 1 (CONFIRMED)
(7, 4),   -- Rabu 10:00 - Bimbingan Progress (PENDING)
(8, 3),   -- Rabu 13:00 - Bimbingan BAB 3 (CONFIRMED)
(9, 2),   -- Kamis 09:00 - Bimbingan BAB 2 (PENDING)
(11, 1),  -- Kamis 14:00 - Bimbingan BAB 1 (CONFIRMED)
(12, 3);  -- Jumat 08:00 - Bimbingan BAB 3 (CONFIRMED)

-- 9. INSERT PEMBLOKIRAN_RUANGAN (untuk jadwal pemblokiran)
INSERT INTO PemblokiranRuangan (IdRuangan, idAdmin, IdJadwal, Waktu, Alasan) VALUES
(1, 'ADM001', 1, '2025-11-23 15:00:00', 'Maintenance ruangan'),
(2, 'ADM001', 5, '2025-11-23 15:30:00', 'Rapat koordinasi dosen'),
(1, 'ADM001', 6, '2025-11-23 16:00:00', 'Persiapan acara seminar'),
(2, 'ADM001', 10, '2025-11-23 16:30:00', 'Pembersihan ruangan'),
(1, 'ADM001', 13, '2025-11-23 17:00:00', 'Rapat komisi tugas akhir');

-- 10. INSERT MAHASISWA PROSES BIMBINGAN
INSERT INTO MahasiswaProsesBimbingan (IdMahasiswa, IdBimbingan, tipe) VALUES
('MHS001', 1, 'Peserta'),
('MHS002', 2, 'Peserta'),
('MHS001', 3, 'Peserta'),
('MHS002', 3, 'Peserta'),
('MHS001', 4, 'Peserta');

-- 11. INSERT DOSEN PROSES BIMBINGAN
INSERT INTO DosenProsesBimbingan (IdDosen, IdBimbingan, tipe) VALUES
('DSN001', 1, 'Pembimbing'),
('DSN001', 2, 'Pembimbing'),
('DSN002', 3, 'Pembimbing'),
('DSN001', 4, 'Pembimbing');
