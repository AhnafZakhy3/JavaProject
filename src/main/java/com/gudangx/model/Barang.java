package com.gudangx.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class representing a Barang (Item).
 */
public class Barang {
    private int idBarang;
    private String kodeBarang;
    private String nama;
    private String kategori;
    private String satuan;
    private BigDecimal hargaBeli;
    private BigDecimal hargaJual;
    private int minimalStok;
    private LocalDateTime createdAt;

    // Additional field for convenience (joined from Stok table)
    private int stokSekarang;

    public Barang() {
    }

    public Barang(String kodeBarang, String nama, String kategori, String satuan, BigDecimal hargaBeli,
            BigDecimal hargaJual, int minimalStok) {
        this.kodeBarang = kodeBarang;
        this.nama = nama;
        this.kategori = kategori;
        this.satuan = satuan;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.minimalStok = minimalStok;
    }

    // Getters and Setters
    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public BigDecimal getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(BigDecimal hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    public BigDecimal getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(BigDecimal hargaJual) {
        this.hargaJual = hargaJual;
    }

    public int getMinimalStok() {
        return minimalStok;
    }

    public void setMinimalStok(int minimalStok) {
        this.minimalStok = minimalStok;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getStokSekarang() {
        return stokSekarang;
    }

    public void setStokSekarang(int stokSekarang) {
        this.stokSekarang = stokSekarang;
    }

    @Override
    public String toString() {
        return nama + " (" + kodeBarang + ")"; // Useful for ComboBox
    }
}
