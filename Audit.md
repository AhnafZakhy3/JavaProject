# Laporan Audit Keamanan Aplikasi Java Desktop (GudangX)
## Ringkasan Eksekutif
Aplikasi GudangX adalah aplikasi desktop berbasis JavaFX yang berinteraksi langsung dengan database MySQL (2-tier architecture). Hasil audit menemukan sejumlah kerentanan dengan tingkat risiko **High** hingga **Critical**, terutama terkait dengan *Hardcoded Credentials*, *Broken Authentication & Authorization*, *Plaintext Data Storage*, dan *Business Logic/Race Condition*.

Disarankan untuk merombak arsitektur dengan mengimplementasikan REST API (3-tier) untuk menghindari terseksposnya sistem kredensial database dan memindahkan logika bisnis dari sisi klien.

## Detail Temuan Kerentanan

### 1. Hardcoded Database Credentials (CWE-798)
- **Severity**: Critical (CVSS: 9.8)
- **File**: `src/main/resources/application.properties`, Baris 2-4 & `com/gudangx/util/DBConnection.java`
- **Penjelasan**: Username dan password untuk koneksi database (*root:*) disimpan dalam bentuk teks biasa (plain text) di dalam file properti konfigurasi bawaan aplikasi. Mengingat bahwa ini merupakan aplikasi *Fat Client*, file JAR/eksekutabel akan dengan mudah di-*decompile* oleh seorang penyerang untuk mengekstraksi informasi kredensial.
- **Cara Eksploitasi**: Attacker dapat mengekstrak berkas `.properties` maupun *resources* lainnya dari program yang didistribusikan, kemudian login ke database dan mendapatkan akses penuh terhadap seluruh tabel dan privilese tanpa filter.
- **Rekomendasi Perbaikan**:
  Ubah arsitektur pengaksesan data dari model *Client-Server Direct DB* ke model arsitektur sistem Terdistribusi (3-tier). Aplikasi tidak boleh mengetahui otentikasi database. Jika 2-tier tetap harus digunakan, gunakan Windows Authentication/Kerberos SSO atau enkripsi properti eksternal yang di-decrypt *in-memory* dengan *Key Management System* terpisah.

### 2. Password Database dalam Bentuk Teks Biasa (Plaintext Password) (CWE-256 / CWE-319)
- **Severity**: High (CVSS: 8.1)
- **File**: `src/main/java/com/gudangx/dao/impl/PenggunaDAOImpl.java`, Baris 28 & `Pengguna.java`
- **Penjelasan**: Password dari sisi pengguna tidak di *hash*, tetapi ditransfer dan dicari menggunakan `SELECT * FROM pengguna WHERE username = ? AND password = ?`. Jika sewaktu-waktu DB berhasil ditembus, semua sandi milik *user* akan terlihat jelas.
- **Cara Eksploitasi**: Attacker yang mendapatkan akses basis data bisa mengeksploitasi password plain-text untuk disalahgunakan, sehingga meningkatkan risiko *Dictionary Attack* atau *Credential Stuffing* silang aplikasi lainnya.
- **Secure Code Example (Java with BCrypt)**:
  ```java
  // Gunakan dependensi seperti org.mindrot.jbcrypt.BCrypt
  
  // Method untuk mendaftar / insert pass:
  String hashedPw = BCrypt.hashpw(plainTextPass, BCrypt.gensalt(12));
  
  // Method login di DAO:
  String sql = "SELECT * FROM pengguna WHERE username = ?";
  // ...ambil objek ->
  if (BCrypt.checkpw(inputPassword, userFromDb.getPassword())) {
      // Login sukses
  }
  ```
- **Rekomendasi Perbaikan**: Terapkan PBKDF2, Argon2, atau algoritma hashing modern, jangan memakai MD5 atau plaintext. Pastikan menggunakan *salt* yang digenerate *random* (contoh fungsi *SecureRandom*).

### 3. Client-Side Authorization Bypass / Missing Function Level Access Control (CWE-602 / CWE-862)
- **Severity**: High (CVSS: 8.1)
- **File**: `src/main/java/com/gudangx/controller/DashboardController.java` (Baris 58) dan `MasterBarangController.java`
- **Penjelasan**: Pembatasan *role* dilakukan murni pada sisi klien, semata-mata dengan melakukan `btnMasterBarang.setDisable(true)` jika yang *login* adalah user ber*role* STAFF. *Controller* maupun kelas servis `MasterBarang` tidak melakukan pengecekan otoritas di awal fungsi sama sekali.
- **Cara Eksploitasi**: Seorang staf curang dapat mengubah *state UI* di *memory*, atau mengirim langsung permintaan inisiasi navigasi dengan *patching* atau meubah parameter JavaFX saat operasional untuk dapat mengedit harga/aset pada master barang, melompati perlindungan Disable Button klien.
- **Rekomendasi Perbaikan**: Tambahkan pengecekan Role di tiap Controller terkait dan batasan fungsi layanan. Sebagai contoh:
  ```java
  public void initialize() {
      if (!"ADMIN".equalsIgnoreCase(AppConfig.getCurrentUser().getRole())) {
          throw new SecurityException("Unauthorized Access!");
      }
      // Load tables ...
  }
  ```

### 4. Database Connection via Insecure Protocol without TLS/SSL (CWE-319)
- **Severity**: Medium (CVSS: 6.5)
- **File**: `src/main/resources/application.properties`, Baris 2
- **Penjelasan**: JDBC Connection String menggunakan parameter eksplisit `useSSL=false&allowPublicKeyRetrieval=true`. Parameter ini akan membuat Java mengirim *network payload* menuju ke basis data dalam jaringan tanpa enkripsi TLS (Transport Layer Security).
- **Cara Eksploitasi**: Kredensial, identitas pengguna yang *login*, serta aliran data perbelanjaan barang diintersep oleh pihak ketiga yang berada dalam satu jaringan (contoh: *Wi-Fi Umum*) melalui *Packet Sniffer / Man in The Middle (MiTM) Attack*.
- **Rekomendasi Perbaikan**: Terapkan enkripsi end-to-end `useSSL=true&requireSSL=true`, dan siapkan sertifikat terpercaya dengan SSL TrustStore pada lingkungan OS atau *runtime* Java.

### 5. Race Condition (ToCToU) pada Pengurangan Logika Stok (CWE-362)
- **Severity**: High (CVSS: 7.5)
- **File**: `src/main/java/com/gudangx/service/GudangService.java`, Metode `processBarangKeluar`
- **Penjelasan**: Sistem mengambil data stok `stokSekarang`, memeriksa `>= qty`, mencatat *insert* keluar, lalu meng-*update* nilai *stok*. Namun semua aksi itu tidak dijalankan dengan *Database Transaction Block*, ataupun prosedur komit *atomic*.
- **Cara Eksploitasi**: Dua akses atau koneksi sinkronisasi dalam hitungan milidetik yang sama pada satu *node* dapat sukses mem-validasi `if (stokSekarang < qty)` padahal jumlah tersebut telah dikurangi oleh pihak lain, mengakibatkan stok minus, manipulasi limit transaksi, dan korupnya integritas hitungan aset.
- **Perbaikan Kode via Atomic Query Database**:
  ```sql
  -- DAO Update logic lebih baik diamati dengan transaction
  UPDATE barang SET stok_sekarang = stok_sekarang - ? WHERE id_barang = ? AND stok_sekarang >= ?;
  ```
  Kemudian periksa nilai return `executeUpdate() > 0`; bila tidak berjalan / return 0 berarti periksa apakah terjadi limitasi *race data stock*.

### 6. Reverse Engineering & Code Obfuscation Risks (CWE-693)
- **Severity**: Low (CVSS: 3.9)
- **File**: Semua Package (`com.gudangx.*`)
- **Penjelasan**: Tidak ada penanda *obfuscator* (ProGuard/R8) pada mekanisme _build_. Logika maupun nama *class* dan variabel akan terekspos langsung.

## Kesimpulan Evaluasi Area Audit Lainnya 
Sesuai cakupan fokus penilitian:
- **Deserialization Risk**: Aman. Aplikasi tidak memanggil `ObjectInputStream.readObject()` baik secara inherens mapun objek serial. Penggunaan *RMI* atau *Gadget* rantai objek tidak digunakan.
- **File & OS Interaction**: Aman. Aplikasi tidak rentan Command Injection, Path Traversal, `Runtime.exec()`, atau Arbitrary file access.
- **Dependency Security**: Relatif Aman. Berdasarkan analisa Maven/Gradle versi `pom.xml`, paket komponen antarmuka GUI, driver JDBC (`8.2.0`), & Unit Testing menggunakan pustaka modern tanpa _Known CVE_ kritis bawaan yang tak dapat diatasi pengembang.
