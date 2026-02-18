package com.gudangx.dao;

import com.gudangx.model.Barang;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

/**
 * Contoh Unit Test sederhana untuk Barang Model & Logika Bisnis (Simulator).
 * Untuk testing DAO sesungguhnya, disarankan menggunakan H2 Database in-memory
 * atau Mockito.
 */
public class BarangTest {

    @Test
    public void testCreateBarangObject() {
        Barang b = new Barang();
        b.setKodeBarang("B001");
        b.setNama("Laptop Gaming");
        b.setHargaBeli(new BigDecimal("15000000"));
        b.setHargaJual(new BigDecimal("17000000"));
        b.setMinimalStok(5);

        Assertions.assertEquals("B001", b.getKodeBarang());
        Assertions.assertEquals("Laptop Gaming", b.getNama());
        Assertions.assertEquals(new BigDecimal("15000000"), b.getHargaBeli());
    }

    @Test
    public void testStokValidationLogic() {
        // Simulasi logika validasi stok
        int currentStok = 10;
        int requestQty = 5;

        boolean isSufficient = currentStok >= requestQty;
        Assertions.assertTrue(isSufficient, "Stok harus mencukupi untuk request ini");

        requestQty = 15;
        isSufficient = currentStok >= requestQty;
        Assertions.assertFalse(isSufficient, "Stok tidak boleh minus");
    }
}
