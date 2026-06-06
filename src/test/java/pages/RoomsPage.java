package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

public class RoomsPage extends BasePage {

    private final Locator reportSection;

    public RoomsPage(Page page) {
        super(page);

        this.reportSection = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Report"));
    }

    @Step("Verify 'Reports' section is opened")
    public void shouldSeeReportSection() {
        reportSection.isVisible();
    }
}
