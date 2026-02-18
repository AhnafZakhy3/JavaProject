package com.gudangx.model;

import java.time.LocalDateTime;

/**
 * Model class for BarangKeluar (Outgoing Item Transaction).
 */
public class BarangKeluar {
    private int idKeluar;
    private int idBarang;
    private int qty;
    private LocalDateTime tglKeluar;
    private String tujuan;
    private String keterangan;
    private int createdBy;

    // Helper fields for display
    private String namaBarang;
    private String namaUser;

    public BarangKeluar() {
    }

    public BarangKeluar(int idBarang, int qty, String tujuan, String keterangan, int createdBy) {
        this.idBarang = idBarang;
        this.qty = qty;
        this.tujuan = tujuan;
        this.keterangan = keterangan;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getIdKeluar() {
        return idKeluar;
    }

    public void setIdKeluar(int idKeluar) {
        this.idKeluar = idKeluar;
    }

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public LocalDateTime getTglKeluar() {
        return tglKeluar;
    }

    public void setTglKeluar(LocalDateTime tglKeluar) {
        this.tglKeluar = tglKeluar;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }
}
