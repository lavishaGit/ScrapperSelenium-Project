package com.recipe.test;

import org.testng.annotations.Test;

import com.recipe.util.CommonMethods;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.BeforeTest;

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
import org.testng.annotations.AfterTest;

public class ScrapRecipeByHealth {
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

		List<WebElement> healthLinks = driver.findElements(By.xpath("//div[@id='ctl00_cntleftpanel_ttlhealthtree_tvTtlHealthn0Nodes']//table//tbody//tr//td[3]//a"));
		List<String> links = new ArrayList<String>();
		healthLinks.forEach((linkelement) -> links.add(linkelement.getAttribute("href")));

		int linksSize = healthLinks.size();
		System.out.println("linksSize:"+linksSize);
		for (int j = 0; j < linksSize; j++) {
			System.out.println("link number:"+j);
			System.out.println("web page link:"+links.get(j));
			driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
			if(!links.get(j).contains("javascript:")) {
				driver.navigate().to(links.get(j));
			// Traversing through the pages(using loop)
			cm.iterateRecipes(driver,"Recipe_Health");
			}else
				System.out.println("No valid url");
		}

	}

	@BeforeTest
	public void beforeTest() {
		

		//COMMON WAY TO RUN THE SCRIPT
		/*System.setProperty("webdriver.chrome.driver", "C:\\SeleniumDriver\\chromedriver.exe"); 
        driver = new ChromeDriver();
        driver.navigate().to("https://www.tarladalal.com/");
		driver.manage().window().maximize();*/
		
		
		//TO RUN THE SCRIPT FASTER
		//System.setProperty("webdriver.chrome.driver", "C:\\SeleniumDriver\\chromedriver.exe");
		//driver = cm.setUp(driver);
		driver = cm.setUpHeadlessBrowser(driver);

	}

	@AfterTest
	public void afterTest() {
		driver.close();

	}

}
