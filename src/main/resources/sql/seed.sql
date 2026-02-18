-- Seed Data untuk GudangX

-- 1. Insert Pengguna (Admin)
INSERT INTO pengguna (username, password, nama, role) VALUES 
('admin', 'admin123', 'Administrator Toko', 'ADMIN'),
('staff', 'staff123', 'Staff Gudang', 'STAFF');

-- 2. Insert Barang Dummy
INSERT INTO barang (kode_barang, nama, kategori, satuan, harga_beli, harga_jual, minimal_stok) VALUES 
('B001', 'Laptop Asus ROG', 'Elektronik', 'Unit', 15000000, 16500000, 5),
('B002', 'Mouse Logitech Wireless', 'Aksesoris', 'Pcs', 150000, 200000, 10),
('B003', 'Keyboard Mechanical', 'Aksesoris', 'Unit', 500000, 750000, 5),
('B004', 'Monitor LG 24 Inch', 'Elektronik', 'Unit', 1800000, 2100000, 3);

-- 3. Insert Stok Awal (Inisialisasi)
INSERT INTO stok (id_barang, qty) VALUES 
(1, 10), -- Laptop
(2, 50), -- Mouse
(3, 20), -- Keyboard
(4, 8);  -- Monitor
