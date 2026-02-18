package com.gudangx.controller;

import com.gudangx.app.AppConfig;
import com.gudangx.app.Main;
import com.gudangx.dao.impl.BarangMasukDAOImpl;
import com.gudangx.model.Barang;
import com.gudangx.model.BarangMasuk;
import com.gudangx.service.GudangService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class BarangMasukController {

    @FXML
    private ComboBox<Barang> cmbBarang;
    @FXML
    private TextField txtQty;
    @FXML
    private TextField txtSupplier;
    @FXML
    private TextField txtNoDokumen;
    @FXML
    private TextArea txtKeterangan;

    @FXML
    private TableView<BarangMasuk> tblHistory;
    @FXML
    private TableColumn<BarangMasuk, String> colBarang;
    @FXML
    private TableColumn<BarangMasuk, Integer> colQty;
    @FXML
    private TableColumn<BarangMasuk, String> colTgl;
    @FXML
    private TableColumn<BarangMasuk, String> colSupplier;
    @FXML
    private TableColumn<BarangMasuk, String> colUser;

    private final GudangService service = new GudangService();
    // Temporary direct DAO usage for history list, ideally moved to service
    private final BarangMasukDAOImpl dao = new BarangMasukDAOImpl();

    @FXML
    public void initialize() {
        setupTable();
        loadBarangCombo();
        loadHistory();
    }

    private void setupTable() {
        colBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTgl.setCellValueFactory(new PropertyValueFactory<>("tglMasuk"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
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
            tblHistory.setItems(FXCollections.observableArrayList(dao.getAllBarangMasuk()));
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

            BarangMasuk bm = new BarangMasuk();
            bm.setIdBarang(selectedBarang.getIdBarang());
            bm.setQty(qty);
            bm.setSupplier(txtSupplier.getText());
            bm.setNoDokumen(txtNoDokumen.getText());
            bm.setKeterangan(txtKeterangan.getText());
            bm.setCreatedBy(AppConfig.getCurrentUser().getIdPengguna());
            bm.setTglMasuk(LocalDateTime.now());

            service.processBarangMasuk(bm);

            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang Masuk berhasil disimpan!");
            loadHistory();
            clearForm();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Jumlah (Qty) harus angka positif!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void clearForm() {
        cmbBarang.getSelectionModel().clearSelection();
        txtQty.clear();
        txtSupplier.clear();
        txtNoDokumen.clear();
        txtKeterangan.clear();
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
