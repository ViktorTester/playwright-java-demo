package tests.ui;

import base.BaseUiTest;
import org.junit.jupiter.api.*;
import pages.HomePage;
import pages.LoginPage;
import pages.RoomsPage;

import static tests.tags.TestTags.SMOKE;
import static tests.tags.TestTags.UI;


@Tag(UI)
class HomePageSmokeTest extends BaseUiTest {

    @Test
    @Tag(SMOKE)
    @DisplayName("Should login as Admin")
    void shouldOpenHomePage() {

        HomePage homePage = new HomePage(page);
        LoginPage loginPage = new LoginPage(page);
        RoomsPage roomsPage = new RoomsPage(page);

        homePage.open();
        homePage.shouldBeOpened();

        homePage.openAdminSection();

        loginPage.login("admin", "password");

        roomsPage.shouldSeeReportSection();

    }
}