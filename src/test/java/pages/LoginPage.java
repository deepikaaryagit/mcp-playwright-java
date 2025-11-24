package pages;

import com.microsoft.playwright.Page;
import mcp.AIUtils;

public class LoginPage {

    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void gotoLogin() {
        page.navigate("https://www.starbucks.com");
    }

    public void login(String email, String password) {
        // For demo: get locator from MCP using snippet (pretend snippet)
        page.locator(".sb-button.sb-button--default.sb-button--black.mr3").first().click();
        String emailLocator = AIUtils.getSmartLocator("<input id='email' />");

        page.locator(emailLocator).fill(email);
        page.locator("#password").fill(password);
        page.locator("button[type='submit']").click();
    }
}
