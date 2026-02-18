package com.gudangx.controller;

import com.gudangx.app.AppConfig;
import com.gudangx.app.Main;
import com.gudangx.model.Pengguna;
import com.gudangx.service.GudangService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller for Login Screen.
 */
public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private final GudangService service = new GudangService();

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Username dan Password harus diisi!");
            return;
        }

        try {
            Pengguna user = service.login(username, password);
            if (user != null) {
                AppConfig.setCurrentUser(user);
                System.out.println("Login berhasil: " + user.getNama());
                Main.setRoot("dashboard"); // Go to dashboard
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau Password salah!");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Terjadi kesalahan sistem: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
