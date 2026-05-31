package tests.api;

import base.BaseApiTest;
import com.microsoft.playwright.APIResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static api.assertions.ApiResponseValidator.verifyStatus;

class RoomApiSmokeTest extends BaseApiTest {

    @Test
    @DisplayName( "Should return rooms list")
    void shouldReturnRoomsList() {
        APIResponse response = authenticatedApi.room
                .getRoom()
                .get();

        verifyStatus(response, 200);
    }
}