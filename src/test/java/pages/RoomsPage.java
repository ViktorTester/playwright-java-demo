package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class RoomsPage extends BasePage {

    private final Locator reportSection;

    public RoomsPage(Page page) {
        super(page);

        this.reportSection = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Report"));
    }

    public void shouldSeeReportSection() {
        reportSection.isVisible();
    }
}
