package pages;

import com.microsoft.playwright.Page;

public class LoginPage {

    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void gotoLogin() {
        page.navigate("https://www.starbucks.com");
    }

    public void login(String email, String password) {

        // Click Sign In button on homepage
        page.locator(".sb-button.sb-button--default.sb-button--black.mr3").first().click();

        // Wait until modal OR login page is loaded
        page.waitForSelector("input[name='username'], input[type='email']");

        // Enter email
        page.locator("input[name='username'], input[type='email']").first().fill(email);

        // Enter password
        page.locator("input[name='password']").fill(password);

        // Click login
        page.locator("button[type='submit']").click();
    }
}
