package demo.wrappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    WebDriver driver;

    public static boolean click(WebDriver driver, WebElement element){
        try{
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.elementToBeClickable(element));
            Actions act = new Actions(driver);
            act.moveToElement(element);
            Thread.sleep(5000);
            act.click(element).perform();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            try{
                element.click();
                return true;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return false;
    }


    public static boolean sendkeys(WebDriver driver, WebElement inputbox, String keystosend){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(inputbox));
        try {
            inputbox.sendKeys(keystosend);
            return true;

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;

    }

    public static long getCurrentEpochTime() {
        return Instant.now().getEpochSecond();
    }

    public static boolean isElementPresent(WebDriver driver, WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
