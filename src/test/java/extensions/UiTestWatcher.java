package extensions;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

import static utils.AllureAttachments.attachScreenshot;

public class UiTestWatcher implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Object testInstance = context.getRequiredTestInstance();

        if (!(testInstance instanceof HasPage hasPage)) {
            return;
        }

        Page page = hasPage.page();

        if (page != null) {
            attachScreenshot("Screenshot on failure", page);
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        // No action required.
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        // No action required.
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        // No action required.
    }
}