package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class TestCases {
    WebDriver driver;

    @FindBy(xpath = "//a[contains(text(),'Hockey Teams: Forms, Searching and Pagination')]")
    private WebElement Hockeyteam;

    @FindBy(xpath = "//tr[@class='team']")
    private List<WebElement> tableRow;

    @FindBy(xpath = "//ul[@class='pagination']//a[normalize-space()='1']")
    private WebElement pagination;

    @FindBy(xpath = "//ul[@class='pagination']/li/a/strong[contains(text(),'1')]")
    private WebElement activePageOne;

    @FindBy(xpath = "//a[normalize-space()='1']")
    private WebElement Pageone;

    @FindBy(xpath = "//a[@aria-label='Next']")
    private WebElement NextPagebutton;

    @FindBy(xpath = "//a[contains(text(),'Oscar Winning Films: AJAX and Javascript')]")
    private WebElement oscarWinning;

    @FindBy(xpath = "//a[@class='year-link']")
    private List<WebElement> yearlinks;

    @FindBy(xpath = "//tr[@class='film']")
    private List<WebElement> Movierows;

    @FindBy(xpath = "//th[text()='Title']")
    private WebElement movietitleheading;

    /*
     * TODO: Write your tests here with testng @Test annotation. 
     * Follow `testCase01` `testCase02`... format or what is provided in instructions
     */



    @Test(enabled = true)
    public void testCase01(){
        System.out.println("Starting with testCase 01");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.scrapethissite.com/pages/");

        Wrappers.click(driver,Hockeyteam);
        List<HashMap<String,Object>> HockeyTeams = new ArrayList<>();

        if(!Wrappers.isElementPresent(driver,activePageOne)){
            Wrappers.click(driver,Pageone);
        }
        for(int i=1;i<=4;i++){
            for(WebElement row: tableRow){

                String teamName = row.findElement(By.xpath("./td[1]")).getText().trim();
                String Year = row.findElement(By.xpath("./td[2]")).getText().trim();
                String winpercentagestring = row.findElement(By.xpath("./td[6]")).getText().trim();
                Double Winpercentage = Double.parseDouble(winpercentagestring);
                long epochtime = Wrappers.getCurrentEpochTime();

                if(Winpercentage<0.40){
                    HashMap<String, Object> teamData =new HashMap<>();
                    teamData.put("Epoch Time", epochtime);
                    teamData.put("Team Name", teamName);
                    teamData.put("Year",Year);
                    teamData.put("Win Percentage",Winpercentage);
                    HockeyTeams.add(teamData);
                }

            }
            if(NextPagebutton.isEnabled()){
                Wrappers.click(driver,NextPagebutton);
            }else{
                break;
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writeValue(new File("hockey-team-data.json"),HockeyTeams);

            }catch (IOException e){
                e.printStackTrace();
            }
        }



    }
    @Test(enabled = true)
    public void testCase2() throws InterruptedException {
        System.out.println("Starting with testCase 02");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.scrapethissite.com/pages/");

        Wrappers.click(driver,oscarWinning);
        List<HashMap<String,Object>> MovieList = new ArrayList<>();

        for (WebElement yearlink : yearlinks){
            String year = yearlink.getText().trim();
            Wrappers.click(driver,yearlink);
//            wait.until(ExpectedConditions.visibilityOf(movietitleheading));
            Thread.sleep(4000);
            for(int i=0;i<5&&i<Movierows.size();i++){
                WebElement row= Movierows.get(i);
                String title = row.findElement(By.xpath("./td[@class='film-title']")).getText().trim();
                String Nomination = row.findElement(By.xpath("./td[@class='film-nominations']")).getText().trim();
                String Awards = row.findElement(By.xpath("./td[@class='film-awards']")).getText().trim();
                boolean isWinner = false;

                try {
                    WebElement winnerIcon = row.findElement(By.xpath("./td/i[@class='glyphicon glyphicon-flag']"));
                    if (winnerIcon.isDisplayed()) {
                        isWinner = true; // Set to true only for the Best Picture winner
                    }
                } catch (NoSuchElementException e) {

                    isWinner = false;
                }
                long epochtime = Wrappers.getCurrentEpochTime();

                HashMap<String,Object> MovieData = new HashMap<>();
                MovieData.put("Epoch Time:", epochtime);
                MovieData.put("Year:",year);
                MovieData.put("Title:",title);
                MovieData.put("Nomination:",Nomination);
                MovieData.put("Awards:", Awards);
                MovieData.put("isWinner:",isWinner);
                MovieList.add(MovieData);

            }
            driver.navigate().back();
        }
        ObjectMapper mapper = new ObjectMapper();
        File outputfolder = new File("Output");
        if(!outputfolder.exists()){
            outputfolder.mkdir();
        }
        File jsonfile = new File(outputfolder,"oscar-winner-data.json");
        try {
            mapper.writeValue(jsonfile,MovieList);
        }catch (IOException e){
            e.printStackTrace();
        }

        Assert.assertTrue(jsonfile.exists(), "JSON file is not present");
        Assert.assertTrue(jsonfile.length() > 0, "JSON file is empty");


    }
     
    /*
     * Do not change the provided methods unless necessary, they will help in automation and assessment
     */
    @BeforeTest
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        PageFactory.initElements(new AjaxElementLocatorFactory(driver,5), this);
    }

    @AfterTest
    public void endTest()
    {
        driver.close();
        driver.quit();

    }
}