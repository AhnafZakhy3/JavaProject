package com.gudangx.model;

import java.time.LocalDateTime;

/**
 * Model class representing a Pengguna (User).
 */
public class Pengguna {
    private int idPengguna;
    private String username;
    private String password;
    private String nama;
    private String role; // ADMIN or STAFF
    private LocalDateTime createdAt;

    public Pengguna() {
    }

    public Pengguna(String username, String password, String nama, String role) {
        this.username = username;
        this.password = password;
        this.nama = nama;
        this.role = role;
    }

    // Getters and Setters
    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
