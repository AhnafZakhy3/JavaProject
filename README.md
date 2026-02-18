# Sistem Gudang Inventory (GudangX) - JavaFX Project

Aplikasi sistem informasi gudang sederhana yang dibangun menggunakan Java (JavaFX) dan MySQL.
Project ini dibuat untuk memenuhi tugas mata pelajaran Pemrograman Berorientasi Objek (PBO) / Basis Data.

## 📋 Fitur Utama
- **Login Admin/Staff**: Autentikasi pengguna berbasis database.
- **Dashboard**: Ringkasan stok, total barang, dan notifikasi stok menipis.
- **Master Barang**: CRUD (Create, Read, Update, Delete) data barang dengan validasi.
- **Barang Masuk**: Pencatatan stok masuk dari supplier.
- **Barang Keluar**: Pencatatan stok keluar ke tujuan/customer (dengan validasi stok).
- **Laporan**: Riwayat transaksi masuk dan keluar (via tabel view).

## 🛠 Teknologi
- **Bahasa**: Java 17+
- **GUI**: JavaFX 17
- **Database**: MySQL 9.4.0 (Compatible with 8.0+)
- **Build Tool**: Maven

## 🚀 Cara Menjalankan

### 1. Persiapan Database
1. Pastikan **MySQL Server** sudah berjalan.
2. Buat database baru bernama `gudangx_db`.
3. Jalankan script SQL yang ada di folder `src/main/resources/sql/`:
   - Jalankan `schema.sql` untuk membuat tabel-tabel.
   - Jalankan `seed.sql` untuk mengisi data awal (user admin).

Atau jalankan query berikut manual:
```sql
CREATE DATABASE gudangx_db;
USE gudangx_db;
-- (Copy paste isi schema.sql)
-- (Copy paste isi seed.sql)
```

### 2. Konfigurasi Koneksi
Buka file `src/main/resources/application.properties` dan sesuaikan dengan konfigurasi MySQL Anda:
```properties
db.url=jdbc:mysql://localhost:3306/gudangx_db?useSSL=false
db.user=root
db.password=password_mysql_anda
```

### 3. Build & Run (Menggunakan Maven)
Buka terminal/cmd di folder root project (folder yang ada `pom.xml`):

**Compile & Run:**
```bash
mvn clean javafx:run
```

## 👤 Akun Login Default
- **Username**: `admin`
- **Password**: `admin123`

## 📂 Struktur Project
- `com.gudangx.app`: Kelas Main (`Main.java`) dan konfigurasi.
- `com.gudangx.controller`: Logika UI (JavaFX Controllers).
- `com.gudangx.dao`: Interface dan Implementasi akses database.
- `com.gudangx.model`: Representasi data (POJO).
- `com.gudangx.service`: Logika bisnis dan transaksi.
- `com.gudangx.util`: Utilitas koneksi database.
- `src/main/resources/com/gudangx/ui`: File layout FXML.

## 📝 Catatan Implementasi
- Aplikasi menggunakan pola arsitektur **MVC/Service-DAO** untuk memisahkan logika bisnis, data akses, dan tampilan.
- Password disimpan dalam plain-text sesuai spesifikasi tugas (untuk produksi, gunakan hashing seperti BCrypt).
- Transaksi stok masuk/keluar otomatis mengupdate jumlah stok di tabel master barang/stok.

---
*Dibuat oleh Ahnaf Zakhy*
