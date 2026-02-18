package com.gudangx.dao.impl;

import com.gudangx.dao.PenggunaDAO;
import com.gudangx.model.Pengguna;
import com.gudangx.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of PenggunaDAO using JDBC.
 */
public class PenggunaDAOImpl implements PenggunaDAO {

    /**
     * Authenticates a user by username and password.
     * Queries the database for a matching username and password.
     * 
     * @param username The username to check.
     * @param password The password to check (plain text).
     * @return The Pengguna object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Pengguna login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM pengguna WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Pengguna user = new Pengguna();
                    user.setIdPengguna(rs.getInt("id_pengguna"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setNama(rs.getString("nama"));
                    user.setRole(rs.getString("role"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
            }
        }
        return null;
    }
}
