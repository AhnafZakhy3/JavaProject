-- DDL untuk Sistem Gudang Inventory (GudangX)
-- Pastikan database 'gudangx_db' sudah dibuat sebelum menjalankan script ini.
-- CREATE DATABASE IF NOT EXISTS gudangx_db;
-- USE gudangx_db;

-- Tabel Pengguna (Admin/Staff)
CREATE TABLE IF NOT EXISTS pengguna (
    id_pengguna INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Disimpan dalam plain text sesuai requirement
    nama VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'STAFF') NOT NULL DEFAULT 'STAFF',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Barang
CREATE TABLE IF NOT EXISTS barang (
    id_barang INT AUTO_INCREMENT PRIMARY KEY,
    kode_barang VARCHAR(50) NOT NULL UNIQUE,
    nama VARCHAR(100) NOT NULL,
    kategori VARCHAR(50),
    satuan VARCHAR(20),
    harga_beli DECIMAL(15, 2) NOT NULL,
    harga_jual DECIMAL(15, 2) NOT NULL,
    minimal_stok INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Stok (One-to-One dengan Barang)
CREATE TABLE IF NOT EXISTS stok (
    id_stok INT AUTO_INCREMENT PRIMARY KEY,
    id_barang INT NOT NULL,
    qty INT NOT NULL DEFAULT 0,
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_barang) REFERENCES barang(id_barang) ON DELETE CASCADE,
    CONSTRAINT uq_stok_barang UNIQUE (id_barang)
);

-- Tabel Barang Masuk
CREATE TABLE IF NOT EXISTS barang_masuk (
    id_masuk INT AUTO_INCREMENT PRIMARY KEY,
    id_barang INT NOT NULL,
    qty INT NOT NULL,
    tgl_masuk DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    supplier VARCHAR(100),
    no_dokumen VARCHAR(50),
    keterangan TEXT,
    created_by INT,
    FOREIGN KEY (id_barang) REFERENCES barang(id_barang),
    FOREIGN KEY (created_by) REFERENCES pengguna(id_pengguna)
);

-- Tabel Barang Keluar
CREATE TABLE IF NOT EXISTS barang_keluar (
    id_keluar INT AUTO_INCREMENT PRIMARY KEY,
    id_barang INT NOT NULL,
    qty INT NOT NULL,
    tgl_keluar DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tujuan VARCHAR(100),
    keterangan TEXT,
    created_by INT,
    FOREIGN KEY (id_barang) REFERENCES barang(id_barang),
    FOREIGN KEY (created_by) REFERENCES pengguna(id_pengguna)
);

-- Optional: Tabel Transaksi Persediaan (Audit Log)
CREATE TABLE IF NOT EXISTS transaksi_persediaan (
    id_trans INT AUTO_INCREMENT PRIMARY KEY,
    id_barang INT NOT NULL,
    jenis_transaksi ENUM('MASUK', 'KELUAR', 'ADJUSTMENT') NOT NULL,
    qty INT NOT NULL,
    sisa_stok INT NOT NULL, -- Stok setelah transaksi
    tgl_transaksi DATETIME DEFAULT CURRENT_TIMESTAMP,
    keterangan TEXT,
    ref_id INT, -- ID dari barang_masuk atau barang_keluar
    FOREIGN KEY (id_barang) REFERENCES barang(id_barang)
);

-- Indeks untuk optimasi pencarian
CREATE INDEX idx_barang_nama ON barang(nama);
CREATE INDEX idx_barang_kategori ON barang(kategori);
CREATE INDEX idx_masuk_tgl ON barang_masuk(tgl_masuk);
CREATE INDEX idx_keluar_tgl ON barang_keluar(tgl_keluar);
