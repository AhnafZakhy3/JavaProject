package com.gudangx.controller;

import com.gudangx.app.AppConfig;
import com.gudangx.app.Main;
import com.gudangx.model.Barang;
import com.gudangx.service.GudangService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the Dashboard.
 */
public class DashboardController {

    @FXML
    private Label lblWelcome;
    @FXML
    private Label lblTotalBarang;
    @FXML
    private Label lblTotalStok;
    @FXML
    private Label lblLowStock;
    @FXML
    private TableView<Barang> tblLowStock;
    @FXML
    private TableColumn<Barang, String> colKode;
    @FXML
    private TableColumn<Barang, String> colNama;
    @FXML
    private TableColumn<Barang, Integer> colStok;
    @FXML
    private TableColumn<Barang, Integer> colMinStok;

    private final GudangService service = new GudangService();

    @FXML
    public void initialize() {
        if (AppConfig.getCurrentUser() != null) {
            lblWelcome.setText("Selamat Datang, " + AppConfig.getCurrentUser().getNama() + " ("
                    + AppConfig.getCurrentUser().getRole() + ")");
        }

        colKode.setCellValueFactory(new PropertyValueFactory<>("kodeBarang"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stokSekarang"));
        colMinStok.setCellValueFactory(new PropertyValueFactory<>("minimalStok"));

        loadDashboardData();
    }

    private void loadDashboardData() {
        try {
            List<Barang> allBarang = service.getAllBarang();

            // Summary
            lblTotalBarang.setText(String.valueOf(allBarang.size()));

            int totalStok = allBarang.stream().mapToInt(Barang::getStokSekarang).sum();
            lblTotalStok.setText(String.valueOf(totalStok));

            // Low Stock
            List<Barang> lowStockItems = allBarang.stream()
                    .filter(b -> b.getStokSekarang() <= b.getMinimalStok())
                    .collect(Collectors.toList());

            lblLowStock.setText(String.valueOf(lowStockItems.size()));

            ObservableList<Barang> observableLowStock = FXCollections.observableArrayList(lowStockItems);
            tblLowStock.setItems(observableLowStock);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navMasterBarang() throws IOException {
        Main.setRoot("master_barang");
    }

    @FXML
    private void navBarangMasuk() throws IOException {
        Main.setRoot("barang_masuk");
    }

    @FXML
    private void navBarangKeluar() throws IOException {
        Main.setRoot("barang_keluar");
    }

    @FXML
    private void navLaporan() throws IOException {
        Main.setRoot("laporan");
    }

    @FXML
    private void handleLogout() throws IOException {
        AppConfig.logout();
        Main.setRoot("login");
    }
}
