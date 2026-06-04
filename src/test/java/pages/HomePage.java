package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static config.Config.baseUrl;

public class HomePage extends BasePage {

    private static final Pattern PAGE_TITLE = Pattern.compile("Restful-booker-platform demo");
    private static final String EXPECTED_HEADER = "Welcome to Shady Meadows B&B";

    private final Locator header;
    private final Locator adminLink;


    public HomePage(Page page) {
        super(page);

        this.header = page.locator("h1");
        this.adminLink = page.getByRole(
                AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Admin").setExact(true)
        );
    }

    public void open() {
        open(baseUrl());
    }

    public void shouldBeOpened() {
        assertThat(page).hasTitle(PAGE_TITLE);
        assertThat(header).containsText(EXPECTED_HEADER);
    }

    public void openAdminSection() {
        adminLink.click();
    }
}