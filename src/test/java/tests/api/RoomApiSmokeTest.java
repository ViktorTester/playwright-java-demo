package tests.api;

import base.BaseApiTest;
import com.microsoft.playwright.APIResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static api.assertions.ApiResponseValidator.*;

class RoomApiSmokeTest extends BaseApiTest {

    @Test
    @DisplayName( "Should return rooms list")
    void shouldReturnRoomsList() {
        APIResponse response = authenticatedApi.room
                .getRoom()
                .get();

        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("rooms", NotEmptyList);

        verifyApiResponse(response, 200, expectedBody);
    }
}