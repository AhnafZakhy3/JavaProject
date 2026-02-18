package com.gudangx.app;

import com.gudangx.model.Pengguna;

/**
 * Application Configuration and Session Management.
 */
public class AppConfig {
    private static Pengguna currentUser;

    public static Pengguna getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Pengguna user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}
