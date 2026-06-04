package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class LoginPage extends BasePage {

    private final Locator loginInput;
    private final Locator passwordInput;
    private final Locator loginBtn;

    public LoginPage(Page page) {
        super(page);

        this.loginInput = page.getByPlaceholder("Enter username");
        this.passwordInput = page.getByPlaceholder("Password");
        this.loginBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Login"));
    }

    public void login(String login, String password) {
        loginInput.fill(login);
        passwordInput.fill(password);
        loginBtn.click();
    }

}
