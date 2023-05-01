package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

        var credentials = readCredentialsFromFile();

        var driver = openPageInChrome("https://sv-se.facebook.com/");

        //Calls the logIn method that is used to log in to Facebook
        logIn(credentials, driver);

        sleepForSeconds(3);

        //Calling the search method
        search("Java", driver);

        //Calling the sleep method
        sleepForSeconds(3);

        verifyUrl(driver);

        // Close the browser
        logger.info("Closing browser");
        driver.quit();
    }


    //Reading the facebook credentails from the json file
    private static JsonNode readCredentialsFromFile() throws IOException {
        logger.info("Import json file");
        File jsonFile = new File("C:\\temp\\Facebook.json");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonFile);
    }

    //Open the choosen page in Chrome Webdriver
    private static WebDriver openPageInChrome(String url) {
        logger.info("Opening chrome");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--start-maximized");

        //in second argument, insert path to executable chromedriver file
        System.setProperty("webdriver.chrome.driver", "src/main/java/chromedriver.exe");
        WebDriver driver = new ChromeDriver(options);
        driver.get(url);
        // Accept only necessary cookies
        logger.info("Handling cookies window, allow only necessary cookies");
        try {
            WebElement button = driver.findElement(By.xpath("//button[text()='Neka valfria cookies']"));
            button.click();
        } catch (Exception e) {
            logger.error(e.getMessage());
            try {
                WebElement button = driver.findElement(By.xpath("//button[text()='Tillåt endast nödvändiga cookies']"));
                button.click();
            } catch (Exception error) {
                logger.error(e.getMessage());
            }
        }
        return driver;
    }

    //Logging in using the facebook credentials from the json file
    private static void logIn(JsonNode jsonNode, WebDriver driver) {
        logger.info("Login");
        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.sendKeys(jsonNode.get("facebookCredentials").get("email").asText());

        WebElement passwordInput = driver.findElement(By.id("pass"));
        passwordInput.sendKeys(jsonNode.get("facebookCredentials").get("password").asText());

        WebElement loginButton = driver.findElement(By.name("login"));
        loginButton.click();
    }


    //Searching based on the input argument
    private static void search(String searchPhrase, WebDriver driver) {
        var search = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div/div[2]/div[3]/div/div/div/div/div/label/input"));

        try {
            search.click();
            search.sendKeys(searchPhrase);
            search.sendKeys(Keys.ENTER);

        } catch (Exception e) {
            logger.error("Sökning kunde inte genomföras korrekt ", e);
            e.printStackTrace();
        }
        sleepForSeconds(3);
    }

    //Comparing current URL with expected URL
    private static void verifyUrl(WebDriver driver) {
        logger.info("Verify");
        // /html/body/div[1]/div/div[1]/div/div[2]/div[3]/div/div/div/div/div/label/input
        // /html/body/div[1]/div/div[1]/div/div[2]/div[3]/div/div/div/div/div/label
        var currentUrl = driver.getCurrentUrl();
        if (currentUrl.equalsIgnoreCase("https://www.facebook.com/search/top/?q=Java"))
            System.out.println("Test passed");
        else {
            System.out.println("Test failed");
        }
    }

    //Pausing the program for chosen amount of seconds
    private static void sleepForSeconds(int s) {
        try {
            Thread.sleep(Duration.ofSeconds(s));
        } catch (InterruptedException e) {
            logger.error("Kunde inte pausa", e);
        }
    }
}