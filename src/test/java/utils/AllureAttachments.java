package utils;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;

public final class AllureAttachments {

    private AllureAttachments() {
    }

    public static void attachText(String name, String content) {
        if (content == null) {
            content = "<null>";
        }

        Allure.addAttachment(name, "text/plain", content);
    }

    public static void attachJson(String name, String content) {
        if (content == null) {
            content = "<null>";
        }

        Allure.addAttachment(name, "application/json", content, ".json");
    }

    public static void attachScreenshot(String name, Page page) {
        if (page == null) {
            return;
        }

        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                .setFullPage(true));

        Allure.getLifecycle().addAttachment(
                name,
                "image/png",
                "png",
                screenshot
        );
    }
}