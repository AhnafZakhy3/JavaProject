package com.gudangx.model;

import java.math.BigDecimal;

/**
 * DTO for Inventory Report Item.
 */
public class LaporanItem {
    private String kodeBarang;
    private String namaBarang;
    private String kategori;
    private int stokAwal;
    private int barangMasuk;
    private int barangKeluar;
    private int stokAkhir;
    private BigDecimal hargaBeli;
    private BigDecimal totalNilai;

    public LaporanItem() {
    }

    public LaporanItem(String kodeBarang, String namaBarang, String kategori, int stokAwal, int barangMasuk,
            int barangKeluar, int stokAkhir, BigDecimal hargaBeli) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.stokAwal = stokAwal;
        this.barangMasuk = barangMasuk;
        this.barangKeluar = barangKeluar;
        this.stokAkhir = stokAkhir;
        this.hargaBeli = hargaBeli;
        this.totalNilai = hargaBeli.multiply(BigDecimal.valueOf(stokAkhir)); // Calculate total value
    }

    // Getters and Setters
    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getStokAwal() {
        return stokAwal;
    }

    public void setStokAwal(int stokAwal) {
        this.stokAwal = stokAwal;
    }

    public int getBarangMasuk() {
        return barangMasuk;
    }

    public void setBarangMasuk(int barangMasuk) {
        this.barangMasuk = barangMasuk;
    }

    public int getBarangKeluar() {
        return barangKeluar;
    }

    public void setBarangKeluar(int barangKeluar) {
        this.barangKeluar = barangKeluar;
    }

    public int getStokAkhir() {
        return stokAkhir;
    }

    public void setStokAkhir(int stokAkhir) {
        this.stokAkhir = stokAkhir;
        if (this.hargaBeli != null) {
            this.totalNilai = this.hargaBeli.multiply(BigDecimal.valueOf(stokAkhir));
        }
    }

    public BigDecimal getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(BigDecimal hargaBeli) {
        this.hargaBeli = hargaBeli;
        if (this.stokAkhir != 0) { // Recalculate if stock is set
            this.totalNilai = this.hargaBeli.multiply(BigDecimal.valueOf(stokAkhir));
        }
    }

    public BigDecimal getTotalNilai() {
        return totalNilai;
    }

    public void setTotalNilai(BigDecimal totalNilai) {
        this.totalNilai = totalNilai;
    }
}
