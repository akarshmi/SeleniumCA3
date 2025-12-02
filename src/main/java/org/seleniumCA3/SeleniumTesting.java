package org.seleniumCA3;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class SeleniumTesting {
    public static void main(String[] args) throws InterruptedException {

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://ums.lpu.in/lpuums/LoginNew.aspx");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // 1) Select campus
        new Select(driver.findElement(By.id("DropDownList1")))
                .selectByVisibleText("HeadOffice");

        // 2) Username entry 
        WebElement userId = driver.findElement(By.id("txtU"));
        userId.sendKeys("Your_UMS_ID");

        By passwordLocator = By.id("TxtpwdAutoId_8767");

        int retries = 0;
        while (retries < 6) {
            try {
                WebElement password = wait.until(ExpectedConditions.elementToBeClickable(passwordLocator));
                password.clear();
                password.sendKeys("Your_UMS_Password");
                break;
            } catch (StaleElementReferenceException e) {
                System.out.println("Password field refreshed again — retrying...");
                retries++;
                Thread.sleep(200);
            }
        }
        if (retries == 6) {
            throw new RuntimeException("Password field kept refreshing — could not type password");
        }

        // 3) Captcha
        WebElement captcha = wait.until(ExpectedConditions.elementToBeClickable(By.id("CaptchaCodeTextBox")));
        captcha.click();

        // Popup message
        ((JavascriptExecutor) driver).executeScript(
                "let div=document.createElement('div');" +
                        "div.id='msgBox';" +
                        "div.innerText='Fill CAPTCHA manually — Auto Login Enabled';" +
                        "div.style='position:fixed;top:20px;left:50%;transform:translateX(-50%);" +
                        "background:#ffc107;padding:15px;border-radius:10px;font-size:20px;" +
                        "font-weight:600;color:#000;box-shadow:0 0 12px rgba(0,0,0,.3);z-index:999999;';" +
                        "document.body.appendChild(div);"
        );

        while (true) {
            String value = captcha.getAttribute("value");
            if (value != null && value.trim().length() == 6) {
                ((JavascriptExecutor) driver).executeScript("document.getElementById('msgBox').remove()");
                System.out.println("Captcha ready → Submitting...");
                driver.findElement(By.id("iBtnLogins150203125")).click();
                break;
            }
            Thread.sleep(250);
        }

        Thread.sleep(6000);
        driver.quit();
    }
}
