package com.gudangx.model;

import java.time.LocalDateTime;

/**
 * Model class for BarangMasuk (Incoming Item Transaction).
 */
public class BarangMasuk {
    private int idMasuk;
    private int idBarang;
    private int qty;
    private LocalDateTime tglMasuk;
    private String supplier;
    private String noDokumen;
    private String keterangan;
    private int createdBy;

    // Helper fields for display
    private String namaBarang;
    private String namaUser;

    public BarangMasuk() {
    }

    public BarangMasuk(int idBarang, int qty, String supplier, String noDokumen, String keterangan, int createdBy) {
        this.idBarang = idBarang;
        this.qty = qty;
        this.supplier = supplier;
        this.noDokumen = noDokumen;
        this.keterangan = keterangan;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getIdMasuk() {
        return idMasuk;
    }

    public void setIdMasuk(int idMasuk) {
        this.idMasuk = idMasuk;
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

    public LocalDateTime getTglMasuk() {
        return tglMasuk;
    }

    public void setTglMasuk(LocalDateTime tglMasuk) {
        this.tglMasuk = tglMasuk;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getNoDokumen() {
        return noDokumen;
    }

    public void setNoDokumen(String noDokumen) {
        this.noDokumen = noDokumen;
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
