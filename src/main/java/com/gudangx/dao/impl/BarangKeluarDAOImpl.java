package com.gudangx.dao.impl;

import com.gudangx.dao.BarangKeluarDAO;
import com.gudangx.model.BarangKeluar;
import com.gudangx.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BarangKeluarDAO.
 */
public class BarangKeluarDAOImpl implements BarangKeluarDAO {

    @Override
    public void addBarangKeluar(BarangKeluar bk) throws SQLException {
        String sql = "INSERT INTO barang_keluar (id_barang, qty, tgl_keluar, tujuan, keterangan, created_by) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, bk.getIdBarang());
            pstmt.setInt(2, bk.getQty());
            pstmt.setTimestamp(3, bk.getTglKeluar() != null ? Timestamp.valueOf(bk.getTglKeluar())
                    : new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, bk.getTujuan());
            pstmt.setString(5, bk.getKeterangan());
            pstmt.setInt(6, bk.getCreatedBy());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bk.setIdKeluar(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<BarangKeluar> getAllBarangKeluar() throws SQLException {
        List<BarangKeluar> list = new ArrayList<>();
        String sql = "SELECT bk.*, b.nama as nama_barang, p.nama as nama_user " +
                "FROM barang_keluar bk " +
                "JOIN barang b ON bk.id_barang = b.id_barang " +
                "LEFT JOIN pengguna p ON bk.created_by = p.id_pengguna " +
                "ORDER BY bk.tgl_keluar DESC";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BarangKeluar bk = new BarangKeluar();
                bk.setIdKeluar(rs.getInt("id_keluar"));
                bk.setIdBarang(rs.getInt("id_barang"));
                bk.setQty(rs.getInt("qty"));
                bk.setTglKeluar(rs.getTimestamp("tgl_keluar").toLocalDateTime());
                bk.setTujuan(rs.getString("tujuan"));
                bk.setKeterangan(rs.getString("keterangan"));
                bk.setCreatedBy(rs.getInt("created_by"));

                bk.setNamaBarang(rs.getString("nama_barang"));
                bk.setNamaUser(rs.getString("nama_user"));

                list.add(bk);
            }
        }
        return list;
    }
}
