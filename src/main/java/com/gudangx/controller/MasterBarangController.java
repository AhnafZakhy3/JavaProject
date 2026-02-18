package com.gudangx.controller;

import com.gudangx.app.Main;
import com.gudangx.model.Barang;
import com.gudangx.service.GudangService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

public class MasterBarangController {

    @FXML
    private TableView<Barang> tblBarang;
    @FXML
    private TableColumn<Barang, String> colKode;
    @FXML
    private TableColumn<Barang, String> colNama;
    @FXML
    private TableColumn<Barang, String> colKategori;
    @FXML
    private TableColumn<Barang, String> colSatuan;
    @FXML
    private TableColumn<Barang, BigDecimal> colHargaBeli;
    @FXML
    private TableColumn<Barang, BigDecimal> colHargaJual;
    @FXML
    private TableColumn<Barang, Integer> colStok;
    @FXML
    private TextField txtSearch;

    private final GudangService service = new GudangService();
    private ObservableList<Barang> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colKode.setCellValueFactory(new PropertyValueFactory<>("kodeBarang"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colSatuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
        colHargaBeli.setCellValueFactory(new PropertyValueFactory<>("hargaBeli"));
        colHargaJual.setCellValueFactory(new PropertyValueFactory<>("hargaJual"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stokSekarang"));
    }

    private void loadData() {
        try {
            masterData.setAll(service.getAllBarang());
            FilteredList<Barang> filteredData = new FilteredList<>(masterData, p -> true);

            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(barang -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (barang.getNama().toLowerCase().contains(lowerCaseFilter))
                        return true;
                    if (barang.getKodeBarang().toLowerCase().contains(lowerCaseFilter))
                        return true;
                    if (barang.getKategori().toLowerCase().contains(lowerCaseFilter))
                        return true;
                    return false;
                });
            });

            tblBarang.setItems(filteredData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        showDialog(null);
    }

    @FXML
    private void handleEdit() {
        Barang selected = tblBarang.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDialog(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Pilih Barang", "Silakan pilih barang yang akan diedit.");
        }
    }

    @FXML
    private void handleDelete() {
        Barang selected = tblBarang.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Hapus Barang");
            alert.setHeaderText("Hapus " + selected.getNama() + "?");
            alert.setContentText("Apakah Anda yakin? Data yang sudah ada transaksi mungkin akan error jika dihapus.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    service.deleteBarang(selected.getIdBarang());
                    loadData();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Gagal Hapus", e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Pilih Barang", "Silakan pilih barang yang akan dihapus.");
        }
    }

    private void showDialog(Barang barang) {
        Dialog<Barang> dialog = new Dialog<>();
        dialog.setTitle(barang == null ? "Tambah Barang" : "Edit Barang");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtKode = new TextField();
        TextField txtNama = new TextField();
        TextField txtKategori = new TextField();
        TextField txtSatuan = new TextField();
        TextField txtHargaBeli = new TextField();
        TextField txtHargaJual = new TextField();
        TextField txtMinStok = new TextField();

        grid.add(new Label("Kode Barang:"), 0, 0);
        grid.add(txtKode, 1, 0);
        grid.add(new Label("Nama Barang:"), 0, 1);
        grid.add(txtNama, 1, 1);
        grid.add(new Label("Kategori:"), 0, 2);
        grid.add(txtKategori, 1, 2);
        grid.add(new Label("Satuan:"), 0, 3);
        grid.add(txtSatuan, 1, 3);
        grid.add(new Label("Harga Beli:"), 0, 4);
        grid.add(txtHargaBeli, 1, 4);
        grid.add(new Label("Harga Jual:"), 0, 5);
        grid.add(txtHargaJual, 1, 5);
        grid.add(new Label("Minimal Stok:"), 0, 6);
        grid.add(txtMinStok, 1, 6);

        if (barang != null) {
            txtKode.setText(barang.getKodeBarang());
            txtNama.setText(barang.getNama());
            txtKategori.setText(barang.getKategori());
            txtSatuan.setText(barang.getSatuan());
            txtHargaBeli.setText(barang.getHargaBeli().toString());
            txtHargaJual.setText(barang.getHargaJual().toString());
            txtMinStok.setText(String.valueOf(barang.getMinimalStok()));
            // Disable kode update if primary/unique key constraint issues, usually better
            // to allow but keep unique check
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Barang b = barang == null ? new Barang() : barang;
                    b.setKodeBarang(txtKode.getText());
                    b.setNama(txtNama.getText());
                    b.setKategori(txtKategori.getText());
                    b.setSatuan(txtSatuan.getText());
                    b.setHargaBeli(new BigDecimal(txtHargaBeli.getText()));
                    b.setHargaJual(new BigDecimal(txtHargaJual.getText()));
                    b.setMinimalStok(Integer.parseInt(txtMinStok.getText()));
                    return b;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Harga dan Stok harus angka!");
                    return null;
                }
            }
            return null;
        });

        Optional<Barang> result = dialog.showAndWait();

        result.ifPresent(b -> {
            try {
                if (barang == null) {
                    service.addBarang(b);
                } else {
                    service.updateBarang(b);
                }
                loadData();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    private void navDashboard() throws IOException {
        Main.setRoot("dashboard");
    }
}
