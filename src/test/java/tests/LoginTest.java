package tests;

import com.microsoft.playwright.*;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest {

    @Test
    public void loginSmokeTest() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();

            LoginPage loginPage = new LoginPage(page);
            loginPage.gotoLogin();

            loginPage.login("test@example.com", "Password123!");
        }
    }
}
