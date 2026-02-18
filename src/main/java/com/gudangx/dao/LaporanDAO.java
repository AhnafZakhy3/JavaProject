package com.gudangx.dao;

import com.gudangx.model.LaporanItem;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface for Reporting Data Access Object.
 */
public interface LaporanDAO {
    /**
     * Retrieves inventory report for a specific period and optionally filtered by
     * category and search term.
     * 
     * @param startDate Start date of the period (inclusive).
     * @param endDate   End date of the period (inclusive).
     * @param kategori  Optional category filter. Pass null or empty string to
     *                  ignore.
     * @param search    Optional search term (kode or nama). Pass null or empty
     *                  string to ignore.
     * @return List of LaporanItem.
     * @throws SQLException If a database error occurs.
     */
    List<LaporanItem> getLaporanPersediaan(LocalDate startDate, LocalDate endDate, String kategori, String search)
            throws SQLException;

    /**
     * Get list of distinct categories for filter dropdown.
     */
    List<String> getAllCategories() throws SQLException;
}
