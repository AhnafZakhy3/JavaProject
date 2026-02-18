package com.gudangx.controller;

import com.gudangx.app.Main;
import com.gudangx.model.LaporanItem;
import com.gudangx.service.GudangService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class LaporanController {

    @FXML
    private DatePicker dpStart;
    @FXML
    private DatePicker dpEnd;
    @FXML
    private ComboBox<String> cmbKategori;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<LaporanItem> tblLaporan;
    @FXML
    private TableColumn<LaporanItem, String> colKode;
    @FXML
    private TableColumn<LaporanItem, String> colNama;
    @FXML
    private TableColumn<LaporanItem, String> colKat;
    @FXML
    private TableColumn<LaporanItem, Integer> colStokAwal;
    @FXML
    private TableColumn<LaporanItem, Integer> colMasuk;
    @FXML
    private TableColumn<LaporanItem, Integer> colKeluar;
    @FXML
    private TableColumn<LaporanItem, Integer> colStokAkhir;
    @FXML
    private TableColumn<LaporanItem, String> colHarga;
    @FXML
    private TableColumn<LaporanItem, String> colTotalNilai;

    private final GudangService service = new GudangService();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @FXML
    public void initialize() {
        setupTable();
        loadCategories();

        // Default Period: First day of month to today
        dpStart.setValue(LocalDate.now().withDayOfMonth(1));
        dpEnd.setValue(LocalDate.now());

        handleTampilkan(); // Load initial data
    }

    private void setupTable() {
        colKode.setCellValueFactory(new PropertyValueFactory<>("kodeBarang"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colKat.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colStokAwal.setCellValueFactory(new PropertyValueFactory<>("stokAwal"));
        colMasuk.setCellValueFactory(new PropertyValueFactory<>("barangMasuk"));
        colKeluar.setCellValueFactory(new PropertyValueFactory<>("barangKeluar"));
        colStokAkhir.setCellValueFactory(new PropertyValueFactory<>("stokAkhir"));

        // Format Currency
        colHarga.setCellValueFactory(cell -> formatCurrency(cell.getValue().getHargaBeli()));
        colTotalNilai.setCellValueFactory(cell -> formatCurrency(cell.getValue().getTotalNilai()));
    }

    private SimpleStringProperty formatCurrency(BigDecimal value) {
        if (value == null)
            return new SimpleStringProperty("-");
        return new SimpleStringProperty(currencyFormat.format(value));
    }

    private void loadCategories() {
        try {
            cmbKategori.getItems().add(""); // Empty option for "All"
            cmbKategori.getItems().addAll(service.getKategoriList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTampilkan() {
        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();
        String kategori = cmbKategori.getValue();
        String search = txtSearch.getText();

        if (start == null || end == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Periode tanggal harus diisi!");
            return;
        }

        if (start.isAfter(end)) {
            showAlert(Alert.AlertType.WARNING, "Peringatan",
                    "Tanggal Awal tidak boleh lebih besar dari Tanggal Akhir!");
            return;
        }

        try {
            tblLaporan.setItems(FXCollections.observableArrayList(
                    service.getLaporanPersediaan(start, end, kategori, search)));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat laporan: " + e.getMessage());
        }
    }

    @FXML
    private void handleReset() {
        dpStart.setValue(LocalDate.now().withDayOfMonth(1));
        dpEnd.setValue(LocalDate.now());
        cmbKategori.getSelectionModel().clearSelection();
        txtSearch.clear();
        handleTampilkan();
    }

    @FXML
    private void navDashboard() throws IOException {
        Main.setRoot("dashboard");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
