package com.gudangx.dao;

import com.gudangx.model.Pengguna;
import java.sql.SQLException;

/**
 * Interface for Pengguna Data Access Object.
 */
public interface PenggunaDAO {
    /**
     * Authenticates a user by username and password.
     * 
     * @param username The username to check.
     * @param password The password to check (plain text as per requirements).
     * @return The Pengguna object if authentication is successful, null otherwise.
     * @throws SQLException If a database error occurs.
     */
    Pengguna login(String username, String password) throws SQLException;
}
