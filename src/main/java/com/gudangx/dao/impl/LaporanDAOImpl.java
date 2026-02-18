package com.gudangx.dao.impl;

import com.gudangx.dao.LaporanDAO;
import com.gudangx.model.LaporanItem;
import com.gudangx.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LaporanDAOImpl implements LaporanDAO {

    @Override
    public List<LaporanItem> getLaporanPersediaan(LocalDate startDate, LocalDate endDate, String kategori,
            String search) throws SQLException {
        List<LaporanItem> list = new ArrayList<>();

        // Complex Query Explained:
        // 1. Get Base Item info (b.*) and Current Stock (s.qty as stock_now).
        // 2. Calculate In/Out quantity strictly WITHIN the period (in_period,
        // out_period).
        // 3. Calculate In/Out quantity strictly AFTER the period end date (in_after,
        // out_after).
        // 4. Use these to derive:
        // stok_akhir_at_date = stock_now - in_after + out_after
        // stok_awal_at_date = stok_akhir_at_date - in_period + out_period

        // Note: DATE(tgl_masuk) is used to ignore time component for daily reports.
        // Assuming boundaries are inclusive.

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  b.kode_barang, b.nama, b.kategori, b.harga_beli, s.qty as stock_now, ");

        // Subquery: Masuk in Period
        sql.append(
                "  (SELECT COALESCE(SUM(qty),0) FROM barang_masuk WHERE id_barang=b.id_barang AND DATE(tgl_masuk) >= ? AND DATE(tgl_masuk) <= ?) as in_period, ");

        // Subquery: Keluar in Period
        sql.append(
                "  (SELECT COALESCE(SUM(qty),0) FROM barang_keluar WHERE id_barang=b.id_barang AND DATE(tgl_keluar) >= ? AND DATE(tgl_keluar) <= ?) as out_period, ");

        // Subquery: Masuk After Period
        sql.append(
                "  (SELECT COALESCE(SUM(qty),0) FROM barang_masuk WHERE id_barang=b.id_barang AND DATE(tgl_masuk) > ?) as in_after, ");

        // Subquery: Keluar After Period
        sql.append(
                "  (SELECT COALESCE(SUM(qty),0) FROM barang_keluar WHERE id_barang=b.id_barang AND DATE(tgl_keluar) > ?) as out_after ");

        sql.append("FROM barang b ");
        sql.append("JOIN stok s ON b.id_barang = s.id_barang ");
        sql.append("WHERE 1=1 ");

        if (kategori != null && !kategori.isEmpty()) {
            sql.append("AND b.kategori = ? ");
        }

        if (search != null && !search.isEmpty()) {
            sql.append("AND (b.nama LIKE ? OR b.kode_barang LIKE ?) ");
        }

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // Set Parameters
            int idx = 1;

            // Period Params (Start, End) - used twice for In and Out
            pstmt.setDate(idx++, Date.valueOf(startDate));
            pstmt.setDate(idx++, Date.valueOf(endDate));
            pstmt.setDate(idx++, Date.valueOf(startDate));
            pstmt.setDate(idx++, Date.valueOf(endDate));

            // After Params (End) - used twice
            pstmt.setDate(idx++, Date.valueOf(endDate));
            pstmt.setDate(idx++, Date.valueOf(endDate));

            // Filters
            if (kategori != null && !kategori.isEmpty()) {
                pstmt.setString(idx++, kategori);
            }

            if (search != null && !search.isEmpty()) {
                String likeSearch = "%" + search + "%";
                pstmt.setString(idx++, likeSearch);
                pstmt.setString(idx++, likeSearch);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LaporanItem item = new LaporanItem();
                    item.setKodeBarang(rs.getString("kode_barang"));
                    item.setNamaBarang(rs.getString("nama"));
                    item.setKategori(rs.getString("kategori"));
                    item.setHargaBeli(rs.getBigDecimal("harga_beli"));

                    int stockNow = rs.getInt("stock_now");
                    int inPeriod = rs.getInt("in_period");
                    int outPeriod = rs.getInt("out_period");
                    int inAfter = rs.getInt("in_after");
                    int outAfter = rs.getInt("out_after");

                    // Logic:
                    // Stok Akhir (at Report End Date) = Now - InAfter + OutAfter
                    int stokAkhir = stockNow - inAfter + outAfter;

                    // Stok Awal (at Report Start Date) = Stok Akhir - InPeriod + OutPeriod
                    int stokAwal = stokAkhir - inPeriod + outPeriod;

                    item.setStokAwal(stokAwal);
                    item.setBarangMasuk(inPeriod);
                    item.setBarangKeluar(outPeriod);
                    item.setStokAkhir(stokAkhir);
                    // Total Nilai is automatically calculated in setter of StokAkhir/HargaBeli

                    list.add(item);
                }
            }
        }
        return list;
    }

    @Override
    public List<String> getAllCategories() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT kategori FROM barang WHERE kategori IS NOT NULL AND kategori != '' ORDER BY kategori";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("kategori"));
            }
        }
        return list;
    }
}
