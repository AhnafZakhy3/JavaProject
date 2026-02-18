package com.gudangx.dao.impl;

import com.gudangx.dao.BarangDAO;
import com.gudangx.model.Barang;
import com.gudangx.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BarangDAO using JDBC.
 */
public class BarangDAOImpl implements BarangDAO {

    /**
     * Retrieves all barang from the database, including current stock quantity.
     * Uses a LEFT JOIN with the stok table to get the current quantity.
     * 
     * @return List of Barang objects.
     * @throws SQLException If a database error occurs.
     */
    @Override
    public List<Barang> getAllBarang() throws SQLException {
        List<Barang> listBarang = new ArrayList<>();
        String sql = "SELECT b.*, s.qty FROM barang b LEFT JOIN stok s ON b.id_barang = s.id_barang";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Barang b = mapResultSetToBarang(rs);
                listBarang.add(b);
            }
        }
        return listBarang;
    }

    @Override
    public Barang getBarangById(int id) throws SQLException {
        String sql = "SELECT b.*, s.qty FROM barang b LEFT JOIN stok s ON b.id_barang = s.id_barang WHERE b.id_barang = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBarang(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Barang getBarangByKode(String kode) throws SQLException {
        String sql = "SELECT b.*, s.qty FROM barang b LEFT JOIN stok s ON b.id_barang = s.id_barang WHERE b.kode_barang = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBarang(rs);
                }
            }
        }
        return null;
    }

    /**
     * Adds a new barang to the database.
     * Also initializes the stock entry with 0 quantity.
     * Uses transaction to ensure atomicity.
     * 
     * @param barang The Barang object to add.
     * @throws SQLException If a database error occurs.
     */
    @Override
    public void addBarang(Barang barang) throws SQLException {
        String sqlBarang = "INSERT INTO barang (kode_barang, nama, kategori, satuan, harga_beli, harga_jual, minimal_stok) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlStok = "INSERT INTO stok (id_barang, qty) VALUES (?, 0)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert Barang
            try (PreparedStatement pstmt = conn.prepareStatement(sqlBarang, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, barang.getKodeBarang());
                pstmt.setString(2, barang.getNama());
                pstmt.setString(3, barang.getKategori());
                pstmt.setString(4, barang.getSatuan());
                pstmt.setBigDecimal(5, barang.getHargaBeli());
                pstmt.setBigDecimal(6, barang.getHargaJual());
                pstmt.setInt(7, barang.getMinimalStok());
                pstmt.executeUpdate();

                // Get Generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        barang.setIdBarang(generatedKeys.getInt(1));
                    }
                }
            }

            // Insert Initial Stok (0)
            try (PreparedStatement pstmtStok = conn.prepareStatement(sqlStok)) {
                pstmtStok.setInt(1, barang.getIdBarang());
                pstmtStok.executeUpdate();
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback(); // Rollback on error
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public void updateBarang(Barang barang) throws SQLException {
        String sql = "UPDATE barang SET kode_barang=?, nama=?, kategori=?, satuan=?, harga_beli=?, harga_jual=?, minimal_stok=? WHERE id_barang=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barang.getKodeBarang());
            pstmt.setString(2, barang.getNama());
            pstmt.setString(3, barang.getKategori());
            pstmt.setString(4, barang.getSatuan());
            pstmt.setBigDecimal(5, barang.getHargaBeli());
            pstmt.setBigDecimal(6, barang.getHargaJual());
            pstmt.setInt(7, barang.getMinimalStok());
            pstmt.setInt(8, barang.getIdBarang());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteBarang(int id) throws SQLException {
        String sql = "DELETE FROM barang WHERE id_barang = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateStok(int idBarang, int deltaQty) throws SQLException {
        String sql = "UPDATE stok SET qty = qty + ? WHERE id_barang = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deltaQty);
            pstmt.setInt(2, idBarang);
            pstmt.executeUpdate();
        }
    }

    private Barang mapResultSetToBarang(ResultSet rs) throws SQLException {
        Barang b = new Barang();
        b.setIdBarang(rs.getInt("id_barang"));
        b.setKodeBarang(rs.getString("kode_barang"));
        b.setNama(rs.getString("nama"));
        b.setKategori(rs.getString("kategori"));
        b.setSatuan(rs.getString("satuan"));
        b.setHargaBeli(rs.getBigDecimal("harga_beli"));
        b.setHargaJual(rs.getBigDecimal("harga_jual"));
        b.setMinimalStok(rs.getInt("minimal_stok"));
        b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        // Handle Stok if available in query result (from join)
        try {
            b.setStokSekarang(rs.getInt("qty"));
        } catch (SQLException e) {
            // column might not exist if simple select
            b.setStokSekarang(0);
        }
        return b;
    }
}
