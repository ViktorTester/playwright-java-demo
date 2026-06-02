package tests.api;

import base.BaseAuthenticatedApiTest;
import com.microsoft.playwright.APIResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static api.assertions.ApiResponseValidator.NotEmptyList;
import static api.assertions.ApiResponseValidator.verifyApiResponse;
import static tests.tags.TestTags.API;
import static tests.tags.TestTags.SMOKE;

@Tag(API)
class RoomApiSmokeTest extends BaseAuthenticatedApiTest {

    @Test
    @Tag(SMOKE)
    @DisplayName("Should return rooms list")
    void shouldReturnRoomsList() {
        APIResponse response = authenticatedApi.room
                .getRoom()
                .get();

        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("rooms", NotEmptyList);

        verifyApiResponse(response, 200, expectedBody);
    }
}