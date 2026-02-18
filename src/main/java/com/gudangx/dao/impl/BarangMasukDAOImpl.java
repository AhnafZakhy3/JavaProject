package com.gudangx.dao.impl;

import com.gudangx.dao.BarangMasukDAO;
import com.gudangx.model.BarangMasuk;
import com.gudangx.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BarangMasukDAO.
 * NOTE: Stock update is handled in the Service layer to ensure transaction
 * integrity
 * across multiple DAO calls if needed, or we can handle it here.
 * For this project, we will handle the INSERT here, and the Service will
 * coordinate the stock update.
 */
public class BarangMasukDAOImpl implements BarangMasukDAO {

    @Override
    public void addBarangMasuk(BarangMasuk bm) throws SQLException {
        String sql = "INSERT INTO barang_masuk (id_barang, qty, tgl_masuk, supplier, no_dokumen, keterangan, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, bm.getIdBarang());
            pstmt.setInt(2, bm.getQty());
            pstmt.setTimestamp(3, bm.getTglMasuk() != null ? Timestamp.valueOf(bm.getTglMasuk())
                    : new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, bm.getSupplier());
            pstmt.setString(5, bm.getNoDokumen());
            pstmt.setString(6, bm.getKeterangan());
            pstmt.setInt(7, bm.getCreatedBy());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bm.setIdMasuk(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<BarangMasuk> getAllBarangMasuk() throws SQLException {
        List<BarangMasuk> list = new ArrayList<>();
        String sql = "SELECT bm.*, b.nama as nama_barang, p.nama as nama_user " +
                "FROM barang_masuk bm " +
                "JOIN barang b ON bm.id_barang = b.id_barang " +
                "LEFT JOIN pengguna p ON bm.created_by = p.id_pengguna " +
                "ORDER BY bm.tgl_masuk DESC";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BarangMasuk bm = new BarangMasuk();
                bm.setIdMasuk(rs.getInt("id_masuk"));
                bm.setIdBarang(rs.getInt("id_barang"));
                bm.setQty(rs.getInt("qty"));
                bm.setTglMasuk(rs.getTimestamp("tgl_masuk").toLocalDateTime());
                bm.setSupplier(rs.getString("supplier"));
                bm.setNoDokumen(rs.getString("no_dokumen"));
                bm.setKeterangan(rs.getString("keterangan"));
                bm.setCreatedBy(rs.getInt("created_by"));

                bm.setNamaBarang(rs.getString("nama_barang"));
                bm.setNamaUser(rs.getString("nama_user"));

                list.add(bm);
            }
        }
        return list;
    }
}
