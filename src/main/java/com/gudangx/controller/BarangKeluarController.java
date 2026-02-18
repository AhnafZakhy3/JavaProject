package com.gudangx.controller;

import com.gudangx.app.AppConfig;
import com.gudangx.app.Main;
import com.gudangx.dao.impl.BarangKeluarDAOImpl;
import com.gudangx.model.Barang;
import com.gudangx.model.BarangKeluar;
import com.gudangx.service.GudangService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class BarangKeluarController {

    @FXML
    private ComboBox<Barang> cmbBarang;
    @FXML
    private TextField txtQty;
    @FXML
    private TextField txtTujuan;
    @FXML
    private TextArea txtKeterangan;
    @FXML
    private Label lblStokSaatIni;

    @FXML
    private TableView<BarangKeluar> tblHistory;
    @FXML
    private TableColumn<BarangKeluar, String> colBarang;
    @FXML
    private TableColumn<BarangKeluar, Integer> colQty;
    @FXML
    private TableColumn<BarangKeluar, String> colTgl;
    @FXML
    private TableColumn<BarangKeluar, String> colTujuan;
    @FXML
    private TableColumn<BarangKeluar, String> colUser;

    private final GudangService service = new GudangService();
    private final BarangKeluarDAOImpl dao = new BarangKeluarDAOImpl();

    @FXML
    public void initialize() {
        setupTable();
        loadBarangCombo();
        loadHistory();

        cmbBarang.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                // Ensure we get latest stock
                try {
                    Barang refreshed = service.getAllBarang().stream()
                            .filter(b -> b.getIdBarang() == newValue.getIdBarang())
                            .findFirst().orElse(newValue);
                    lblStokSaatIni.setText("Stok Tersedia: " + refreshed.getStokSekarang());
                } catch (SQLException e) {
                    lblStokSaatIni.setText("Stok: ?");
                }
            } else {
                lblStokSaatIni.setText("-");
            }
        });
    }

    private void setupTable() {
        colBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTgl.setCellValueFactory(new PropertyValueFactory<>("tglKeluar"));
        colTujuan.setCellValueFactory(new PropertyValueFactory<>("tujuan"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("namaUser"));
    }

    private void loadBarangCombo() {
        try {
            cmbBarang.setItems(FXCollections.observableArrayList(service.getAllBarang()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHistory() {
        try {
            tblHistory.setItems(FXCollections.observableArrayList(dao.getAllBarangKeluar()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSimpan() {
        Barang selectedBarang = cmbBarang.getValue();
        if (selectedBarang == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Barang", "Silakan pilih barang terlebih dahulu.");
            return;
        }

        try {
            String qtyText = txtQty.getText();
            if (qtyText.isEmpty())
                throw new NumberFormatException();
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0)
                throw new NumberFormatException();

            BarangKeluar bk = new BarangKeluar();
            bk.setIdBarang(selectedBarang.getIdBarang());
            bk.setQty(qty);
            bk.setTujuan(txtTujuan.getText());
            bk.setKeterangan(txtKeterangan.getText());
            bk.setCreatedBy(AppConfig.getCurrentUser().getIdPengguna());
            bk.setTglKeluar(LocalDateTime.now());

            service.processBarangKeluar(bk);

            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang Keluar berhasil disimpan!");
            loadHistory();
            clearForm();
            // Refresh combo to update local list stock info if needed, or trigger listener
            // update
            loadBarangCombo();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Jumlah (Qty) harus angka positif!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal Simpan", e.getMessage());
        }
    }

    private void clearForm() {
        cmbBarang.getSelectionModel().clearSelection();
        txtQty.clear();
        txtTujuan.clear();
        txtKeterangan.clear();
        lblStokSaatIni.setText("-");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    private void navDashboard() throws IOException {
        Main.setRoot("dashboard");
    }
}
