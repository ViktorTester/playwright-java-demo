package tests.api;

import api.assertions.ApiExpect;
import base.BaseAuthenticatedApiTest;
import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static api.assertions.ApiResponseValidator.verifyApiResponse;
import static tests.tags.TestTags.API;
import static tests.tags.TestTags.SMOKE;

/**
 Positive
 {@link #shouldReturnRoomsList} (GET/room) Should return a room list
 */

@Tag(API)
@Feature("Room")
class RoomApiTest extends BaseAuthenticatedApiTest {

    @Test
    @Tag(SMOKE)
    @DisplayName("(GET/room) Should return a rooms list")
    void shouldReturnRoomsList() {
        APIResponse response = authenticatedApi.room
                .getRoom()
                .get();

        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("rooms", ApiExpect.notEmptyList());

        verifyApiResponse(response, 200, expectedBody);
    }


}