package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class JiraFiltersTest {

    WebDriver driver;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        driver.get("https://your-jira-url.atlassian.net");

        // Login (update with your test credentials)
        driver.findElement(By.id("username")).sendKeys("your-email");
        driver.findElement(By.id("login-submit")).click();

        driver.findElement(By.id("password")).sendKeys("your-password");
        driver.findElement(By.id("login-submit")).click();
    }

    @Test
    public void createAndValidateOpenItemsFilter() {

        driver.findElement(By.linkText("Filters")).click();
        driver.findElement(By.linkText("Advanced issue search")).click();

        String openItemsJQL =
                "status in (\"Open\", \"To Do\", \"In Progress\")";

        WebElement jqlBox = driver.findElement(By.name("jql"));
        jqlBox.clear();
        jqlBox.sendKeys(openItemsJQL);

        driver.findElement(By.xpath("//button[text()='Search']")).click();

        List<WebElement> statuses =
                driver.findElements(By.cssSelector("[data-testid='status-field']"));

        for (WebElement status : statuses) {
            String value = status.getText();
            Assert.assertTrue(
                    value.equals("Open") ||
                    value.equals("To Do") ||
                    value.equals("In Progress"),
                    "Unexpected status found: " + value
            );
        }

        driver.findElement(By.xpath("//button[text()='Save as']")).click();
        driver.findElement(By.id("filter-name")).sendKeys("Open Items Filter");
        driver.findElement(By.xpath("//button[text()='Save']")).click();

        Assert.assertTrue(
                driver.getPageSource().contains("Filter saved"),
                "Filter was not saved successfully"
        );
    }

    @Test
    public void createAndValidateClosedItemsFilter() {

        String closedItemsJQL =
                "status in (\"Done\", \"Closed\", \"Resolved\")";

        WebElement jqlBox = driver.findElement(By.name("jql"));
        jqlBox.clear();
        jqlBox.sendKeys(closedItemsJQL);

        driver.findElement(By.xpath("//button[text()='Search']")).click();

        List<WebElement> statuses =
                driver.findElements(By.cssSelector("[data-testid='status-field']"));

        if (statuses.isEmpty()) {
            Assert.assertTrue(
                    driver.getPageSource().contains("No issues were found"),
                    "Expected empty result message not displayed"
            );
        } else {
            for (WebElement status : statuses) {
                String value = status.getText();
                Assert.assertTrue(
                        value.equals("Done") ||
                        value.equals("Closed") ||
                        value.equals("Resolved"),
                        "Unexpected status found: " + value
                );
            }
        }

        driver.findElement(By.xpath("//button[text()='Save as']")).click();
        driver.findElement(By.id("filter-name")).sendKeys("Closed Items Filter");
        driver.findElement(By.xpath("//button[text()='Save']")).click();
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
