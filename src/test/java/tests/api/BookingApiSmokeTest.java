package tests.api;

import base.BaseApiTest;
import com.microsoft.playwright.APIResponse;
import org.junit.jupiter.api.*;

import static api.assertions.ApiResponseValidator.verifyStatus;

class BookingApiSmokeTest extends BaseApiTest {

    static String authToken;

    @Test
    void shouldReturnBookingsList() {

        APIResponse response = api.room
                .getRoom()
                .header("token", "=" + authToken)
                .get();

        verifyStatus(response, 200);

    }

}