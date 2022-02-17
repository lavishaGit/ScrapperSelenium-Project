package com.recipe.test;


import org.testng.annotations.Test;

import com.recipe.util.CommonMethods;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterTest;

public class ScrapRecipeByEquipment {
	WebDriver driver=null;
	static String[] links = null;
	String method;
	String nutrientValues;
	String ingredients;
	String recipeLink;
	String recipeTitle;
	String category;
	String imageLink;
	Map<String, Object[]> recipeData = new TreeMap<String, Object[]>();
	XSSFSheet spreadsheet;
	XSSFRow rowno;
	XSSFWorkbook workbook ;
	String row;
	CommonMethods cm= new CommonMethods();

	@Test
	public void scrapRecipe() throws InterruptedException, IOException {
		cm.writeHeaderToExcel();

		driver.findElement(By.xpath("//a[@href='recipecategories.aspx?srchboxopt=r']")).click();


		List<WebElement> cuisineLinks = driver.findElements(By.xpath("//div[@id='ctl00_cntleftpanel_equiptree_tvEquipmentn0Nodes']//td[3]//a"));
		List<String> links = new ArrayList<String>();
		cuisineLinks.forEach((linkelement) -> links.add(linkelement.getAttribute("href")));

		int linksSize = cuisineLinks.size();
		System.out.println("linksSize:"+linksSize);
		for (int j = 0; j < linksSize; j++) {
			driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
			if(!links.get(j).contains("javascript:"))
				driver.navigate().to(links.get(j));
			// Traversing through the pages(using loop)
			cm.recipeListIteration(driver,"Recipes_Equipment" );
		}
	}


	@BeforeTest
	@Parameters("browser")
	public void beforeTest(String browser) {

		if(browser.equalsIgnoreCase("firefox"))
		{
			WebDriverManager.firefoxdriver().setup();
			FirefoxOptions options =new  FirefoxOptions();

			options.setHeadless(true);
			driver=new FirefoxDriver(options);		  
			driver.navigate().to("https://www.tarladalal.com/");
			driver.manage().window().maximize();
		}
		else {
			if(browser.equalsIgnoreCase("Chrome") )
			{
				{
					WebDriverManager.chromedriver().setup();
					ChromeOptions options =new ChromeOptions();
					options.setHeadless(true);
					HashMap<String, Object> prefs = new HashMap<String, Object>();
					prefs.put("profile.managed_default_content_settings.images", 2);

					options.setExperimentalOption("prefs", prefs);
					driver = new ChromeDriver(options);
					driver.navigate().to("https://www.tarladalal.com/");
					driver.manage().window().maximize(); 
				}}}
	}

	@AfterTest
	public void afterTest() {
		driver.close();

	}
}
