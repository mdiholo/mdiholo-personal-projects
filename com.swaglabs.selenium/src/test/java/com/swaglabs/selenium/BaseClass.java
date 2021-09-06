package com.swaglabs.selenium;

import java.io.BufferedReader;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

public class BaseClass {
	
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
		extent = new ExtentReports(System.getProperty("user.dir")+"/test-output/reports/swag-labs-test-report.html", true);
	}	
	
	
	@AfterSuite
	public void endSuite() {
		
		extent.flush();
	}	

}
