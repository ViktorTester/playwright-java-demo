package tests.ui;

import base.BaseUiTest;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static config.Config.baseUrl;

import static tests.tags.TestTags.SMOKE;
import static tests.tags.TestTags.UI;


@Tag(UI)
class HomePageSmokeTest extends BaseUiTest {

    @Test
    @Tag(SMOKE)
    @DisplayName("Should open home page")
    void shouldOpenHomePage() {
        page.navigate(baseUrl());

        assertThat(page).hasTitle(java.util.regex.Pattern.compile("Restful-booker-platform demo"));
        assertThat(page.locator("h1")).containsText("Welcome to Shady Meadows B&B");
    }
}