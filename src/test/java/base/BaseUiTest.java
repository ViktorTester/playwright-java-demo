package base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import config.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseUiTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    void setUpUi() {
        Config.validate();

        playwright = Playwright.create();
        browser = createBrowser(playwright);

        context = browser.newContext();
        context.setDefaultTimeout(Config.defaultTimeoutMs());

        page = context.newPage();
    }

    @AfterEach
    void tearDownUi() {
        if (context != null) {
            context.close();
        }

        if (browser != null) {
            browser.close();
        }

        if (playwright != null) {
            playwright.close();
        }
    }

    private Browser createBrowser(Playwright playwright) {
        return switch (Config.browser()) {
            case CHROMIUM -> playwright.chromium().launch(
                    new com.microsoft.playwright.BrowserType.LaunchOptions()
                            .setHeadless(Config.headless())
            );
            case FIREFOX -> playwright.firefox().launch(
                    new com.microsoft.playwright.BrowserType.LaunchOptions()
                            .setHeadless(Config.headless())
            );
            case WEBKIT -> playwright.webkit().launch(
                    new com.microsoft.playwright.BrowserType.LaunchOptions()
                            .setHeadless(Config.headless())
            );
        };
    }
}