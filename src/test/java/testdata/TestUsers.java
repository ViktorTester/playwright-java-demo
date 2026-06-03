package testdata;

import api.dto.auth.AuthRequest;
import config.Config;

public final class TestUsers {

    private TestUsers() {
    }

    public static AuthRequest adminCredentials() {
        return new AuthRequest(
                Config.adminUsername(),
                Config.adminPassword()
        );
    }

    public static AuthRequest invalidAdminCredentials() {
        return new AuthRequest(
                Config.adminUsername(),
                "invalid-password"
        );
    }

    public static AuthRequest unknownUserCredentials() {
        return new AuthRequest(
                "unknown-user",
                "invalid-password"
        );
    }
}