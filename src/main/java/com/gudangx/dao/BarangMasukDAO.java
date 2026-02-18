package com.gudangx.dao;

import com.gudangx.model.BarangMasuk;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for BarangMasuk Data Access Object.
 */
public interface BarangMasukDAO {
    void addBarangMasuk(BarangMasuk barangMasuk) throws SQLException;

    List<BarangMasuk> getAllBarangMasuk() throws SQLException;
}
