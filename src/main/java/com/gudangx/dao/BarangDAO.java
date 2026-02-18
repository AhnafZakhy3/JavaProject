package com.gudangx.dao;

import com.gudangx.model.Barang;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Barang Data Access Object.
 */
public interface BarangDAO {
    List<Barang> getAllBarang() throws SQLException;

    Barang getBarangById(int id) throws SQLException;

    Barang getBarangByKode(String kode) throws SQLException;

    void addBarang(Barang barang) throws SQLException;

    void updateBarang(Barang barang) throws SQLException;

    void deleteBarang(int id) throws SQLException;

    void updateStok(int idBarang, int deltaQty) throws SQLException;
}
