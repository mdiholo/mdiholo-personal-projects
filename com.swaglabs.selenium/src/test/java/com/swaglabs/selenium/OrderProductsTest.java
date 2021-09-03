package com.swaglabs.selenium;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class OrderProductsTest {
	
	ParameteriseTests parObj = new ParameteriseTests();
	WebDriver driver = null;
	static ExtentReports extent;
	BufferedReader buffreader;
	ExtentTest test;
	String imagePath;
	boolean testSucceeded;
	String username;
	String password;
	String projectHome = System.getProperty("user.dir");
	
	@BeforeSuite
	public void setupSuite(){
		//Create Report
		extent = new ExtentReports(System.getProperty("user.dir")+"/test-output/reports/test-report-"+parObj.captureRunTimeStamp()+".html", true);
	}		
	
	@BeforeTest
	public void setupTest() throws IOException, FileNotFoundException, CsvValidationException {		
		
		String browserName = parObj.readProperties(projectHome, "browser");
		
		this.buffreader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/src/test/resources/test-data.csv"));
			
		System.setProperty("webdriver."+browserName+".driver", projectHome+"/"+parObj.readProperties(projectHome, browserName+"driver"));
		
		if (browserName.equalsIgnoreCase("gecko")) {
			driver = new FirefoxDriver();
			
		} else {
			driver = new ChromeDriver();		    
		}
		
		driver.manage().window().maximize(); 
		
		this.username = parObj.readProperties(projectHome, "username");
		this.password = parObj.readProperties(projectHome, "password");		
		
	}
	
	@Test(priority=1)
	public void launchBrowser() throws Exception {
		
		test = extent.startTest("Launch Browser");
		test.log(LogStatus.INFO, "System Under Test: <b>SWAG Labs Online Store</b>");
		
		// Launch browser and parse the target url
		driver.get(parObj.readProperties(projectHome, "baseurl"));
		
		// Test if the target website is reached
		if(driver.getTitle().equals("Swag Labs")) {
			this.testSucceeded = true;
			
			// report with screenshot
			parObj.completeTest(test, driver, extent, this.testSucceeded, "site-found", "Site: <b>" +parObj.readProperties(projectHome, "baseurl")+"</b> found.");
	        imagePath = parObj.takeScreenshot(driver,"browser-launch-success");
			test.log(LogStatus.PASS, "Browser launched and Swag Labs site opened."+test.addScreenCapture(imagePath));
			
		}else {
	        // report with screenshot
			parObj.completeTest(test, driver, extent, this.testSucceeded, "site-not-found", "Site not found <b>" +parObj.readProperties(projectHome, "baseurl")+".</b>");
	        imagePath = parObj.takeScreenshot(driver,"browser-launch-failure");	      
			test.log(LogStatus.FAIL, "Swag Labs site not opened."+test.addScreenCapture(imagePath));
		}
		extent.endTest(test);
	}
	
	
	@Test(priority=2)
	public void login() throws Exception {
		
		test = extent.startTest("Login");
		WebElement username = driver.findElement(By.id("user-name"));
		WebElement password = driver.findElement(By.id("password"));
		WebElement loginbtn = driver.findElement(By.id("login-button"));
				
		username.sendKeys(this.username);		
		password.sendKeys(this.password);		
		loginbtn.click();
		
		if(driver.getPageSource().contains("Username and password do not match") ){
			// report with snapshot
			imagePath = parObj.takeScreenshot(driver,"browser-launch-failure");	      
			test.log(LogStatus.FAIL, "User <b>"+this.username+"</b> failed to login."+test.addScreenCapture(imagePath));
			
		}else{
			this.testSucceeded = true;
			// report with snapshot
	        imagePath = parObj.takeScreenshot(driver,"login-success");
			test.log(LogStatus.PASS, "User <b>"+this.username+"</b> successfully logged in."+test.addScreenCapture(imagePath));
		}		
	}
	
	
	@Test(priority=3)
	public void placeOrder() throws Exception {
		test = extent.startTest("Place Order");		
		String orderNo = "";
		String product = "";
		
		product = selectProduct(parObj.readProperties(projectHome, "products"));
		
		this.buffreader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/src/test/resources/test-data.csv"));
        CSVParser parser = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(buffreader);
         
	    for(CSVRecord record : parser) {
	    	
	        String firstName = record.get("FirstName");
	        String lastName = record.get("LastName");
	        String postalCode = record.get("PostalCode");
	        	        
	        if(driver.getPageSource().contains("THANK YOU FOR YOUR ORDER")){
	        	driver.findElement(By.id("back-to-products")).click();
	        	product = selectProduct(parObj.readProperties(projectHome, "products"));
	        	
	        	this.testSucceeded = true;	        	
	        }
	        
		    driver.findElement(By.id("first-name")).clear();
	        driver.findElement(By.id("first-name")).sendKeys(firstName);
	        driver.findElement(By.id("last-name")).clear();
	        driver.findElement(By.id("last-name")).sendKeys(lastName);
	        driver.findElement(By.id("postal-code")).clear();
	        driver.findElement(By.id("postal-code")).sendKeys(postalCode);
	        driver.findElement(By.id("continue")).click();
	        
	        if(driver.getPageSource().contains("SauceCard #")) {
				orderNo = driver.findElement(By.className("summary_value_label")).getText();
				imagePath = parObj.takeScreenshot(driver,"order-"+orderNo.substring(orderNo.indexOf('#')+1)+"-"+parObj.captureRunTimeStamp()+"-captured");
				test.log(LogStatus.PASS, "Order '<b>"+orderNo.substring(orderNo.indexOf('#')+1)+"</b>' for "+firstName+" "+lastName+", placed successfully."+test.addScreenCapture(imagePath));
			
	        }else {
	        	imagePath = parObj.takeScreenshot(driver,"order-failed");
				test.log(LogStatus.PASS, "User <b>"+this.username+"</b> Successfully logged in."+test.addScreenCapture(imagePath));
				
	        }
	        
			driver.findElement(By.id("finish")).click();     	        
	     }			
	}
	
	
	public String selectProduct(String products) {
		List<String> prod_list = Arrays.asList(products.split(",", -1));
		Random rand = new Random();
		
		String product = prod_list.get(rand.nextInt(prod_list.size()));
		driver.findElement(By.xpath("//*[text()='"+product+"']")).click();
		driver.findElement(By.xpath("//*[text()='Add to cart']")).click();		
		driver.findElement(By.className("shopping_cart_link")).click();
		driver.findElement(By.id("checkout")).click();
		
		return product;		
	}
	
	
	//@Test(priority=4)
	public void updateOrder() {		
	}
	
	
	@AfterTest
	public void completeTests() {
		extent.endTest(test);
	}
	
	
	@AfterTest
	public void closeBrowser() {
		//driver.close();
	}
	
	
	@Test(priority=4)
	public void logout() throws Exception {
		test = extent.startTest("Logout");	
		driver.findElement(By.id("back-to-products")).click();
		driver.findElement(By.className("bm-burger-button")).click();
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[1]/div[1]/div[1]/div/div[2]/div[1]/nav/a[3]")).click();
		
		imagePath = parObj.takeScreenshot(driver,"logout-captured");
		test.log(LogStatus.PASS, "logout"+test.addScreenCapture(imagePath));

		
	}
	@AfterSuite
	public void endSuite() {
		
		extent.flush();
	}	
}