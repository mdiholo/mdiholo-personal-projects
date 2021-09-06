package com.swaglabs.selenium;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.IOException;

//import org.apache.commons.csv.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;

public class ParameteriseTests {
	
	public String takeScreenshot(WebDriver webdriver,String screenName) throws Exception{
		
    	String projectHome = System.getProperty("user.dir");
    	String screenshotpath = projectHome+"/test-output/screenshots/"+screenName+captureRunTimeStamp()+".png";
    	
    	// Convert web driver object to TakeScreenshot 
        TakesScreenshot scrShot =((TakesScreenshot)webdriver);

        // Create image file 
          File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);
 
          File DestFile=new File(screenshotpath);
          FileUtils.copyFile(SrcFile, DestFile);
                
        return screenshotpath;
    }
	
	
	public void completeTest(ExtentTest test, WebDriver driver, ExtentReports extent, boolean testSucceeded, String screenshotmsg, String logmsg) throws Exception {
			
		takeScreenshot(driver,screenshotmsg);	
			
		if(testSucceeded) {
			test.log(LogStatus.PASS, logmsg);			
		}else{
			test.log(LogStatus.FAIL, logmsg);			
		}
		
		extent.endTest(test);
	}
	
	
	public String readProperties(String path, String arg) {
		Properties props = new Properties();
		String prop = null; 
		try {
			InputStream input = new FileInputStream(path+"/src/main/resources/configs/config.properties");
			props.load(input);
			prop = props.getProperty(arg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	
	public String readFromTestData(String path) throws IOException, CsvValidationException {
		
		String usrname = "";
		String passwrd = "";
		FileReader reader = new FileReader(path+"/src/test/resources/test-data.csv");
		CSVReader csvReader = new CSVReader(reader);
		String [] record = csvReader.readNext();
        
		if(record != null) {
			System.out.print(record.toString() + "\t");
			System.out.println();
		}		            
		csvReader.close();
        return usrname+","+passwrd;
	}
	
	
	public String captureRunTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		return sdf.format(timestamp);
	}
}
