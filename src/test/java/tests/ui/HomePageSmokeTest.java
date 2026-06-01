package tests.ui;

import base.BaseUiTest;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static config.Config.baseUrl;

class HomePageSmokeTest extends BaseUiTest {

    @Test
    @DisplayName("Should open home page")
    void shouldOpenHomePage() {
        page.navigate(baseUrl());

        assertThat(page).hasTitle(java.util.regex.Pattern.compile("Restful-booker-platform demo"));
        assertThat(page.locator("h1")).containsText("Welcome to Shady Meadows B&B");
    }
}