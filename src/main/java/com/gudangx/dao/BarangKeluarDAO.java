package com.gudangx.dao;

import com.gudangx.model.BarangKeluar;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for BarangKeluar Data Access Object.
 */
public interface BarangKeluarDAO {
    void addBarangKeluar(BarangKeluar barangKeluar) throws SQLException;

    List<BarangKeluar> getAllBarangKeluar() throws SQLException;
}
