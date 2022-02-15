package com.recipe.test;

import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.BeforeTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;

public class ReceipeScrap_Headless {
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

	@Test
	public void scrapRecipe() throws InterruptedException, IOException {
		excelfile();

		driver.findElement(By.xpath("//a[@href='recipecategories.aspx?srchboxopt=r']")).click();
		driver.findElement(By.xpath("//a[@href='recipes-for-american-132']")).click();

		// Traversing through the pages(using loop)

		List<WebElement> pagination = driver.findElements(By.xpath("//div[@id='pagination']/a"));
		List<String> pages = new ArrayList<String>();
		pagination.forEach((linkelement) -> pages.add(linkelement.getAttribute("href")));

		int pgSize = pagination.size();
		for (int j = 0; j < 2; j++) {
			Thread.sleep(1000);
			// WebElement pagei = driver.findElement(By.xpath("(//div[@id='pagination']/a)[" + j + "]"));
			driver.navigate().to(pages.get(j));
			WebElement list = driver.findElement(By.xpath("//div[@class='recipelist']"));
			List<WebElement> allLinks = list.findElements(By.xpath("//span[@class='rcc_recipename']/a"));
			List<String> links = new ArrayList<String>();
			allLinks.forEach((linkelement) -> links.add(linkelement.getAttribute("href")));

			int totcount = allLinks.size();
			// links= new String[totcount];

			for (int i = 0; i < totcount; i++) {
				driver.navigate().to(links.get(i));

				// To get recipe ingredients
				recipeLink = driver.getCurrentUrl();
				System.out.println("recepie link::::: : " + recipeLink);

				ingredients = driver.findElement(By.xpath("//div[@id='rcpinglist']")).getText();
				System.out.println("Ingredients : " + ingredients);

				// To get recipe methods

				method = driver.findElement(By.xpath("//div[@id='ctl00_cntrightpanel_pnlRcpMethod']/div"))
						.getText();

				System.out.println("Methods :============== " + method);

				// To get Nutrition values
				// public boolean isElementDisplayed()

				List<WebElement> dynamicElement = driver
						.findElements(By.xpath("//div[@id='recipe_nutrients']/div//table"));
				if (dynamicElement.size() != 0) {
					nutrientValues = driver.findElement(By.xpath("//div[@id='recipe_nutrients']/div//table"))
							.getText();

					// If list size is non-zero, element is present
					System.out.println("Element present");
				} else
					// Else if size is 0, then element is not present
					System.out.println("Element not present");
				recipeTitle = driver.findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblRecipeName']"))
						.getText();
				System.out.println("Recipe title:" + recipeTitle);

				category = driver.findElement(By.xpath("//div[@id='recipe_tags']")).getText();
				System.out.println("Category:" + category);

				imageLink = driver.findElement(By.xpath("//img[@id='ctl00_cntrightpanel_imgRecipe']"))
						.getAttribute("src");
				System.out.println("Image Link:" + imageLink);

				Thread.sleep(1000);
				// driver.navigate().to(allLinks.n);
				// driver.navigate().;
				// driver.navigate().back();
				// allLinks = list.findElements(By.tagName("a"));
				row += String.valueOf(i);//not workinas it taking one link from each page and appending the excel
				// row = String.valueOf(i+2);
				System.out.println("value of row:::::::::::::::" + row);

				writeRecipeDetailsToExcel();



			}
		}

	}
	public void excelfile() {
		workbook = new XSSFWorkbook();


		// spreadsheet object
		spreadsheet
		= workbook.createSheet(" Recipe Data ");

		// This data needs to be written (Object[])

		recipeData.put(
				"1",
				new Object[] { "Title", "Category", "Ingredients","Method", "Nutrient Values","Image Link","Recipe Link"});



	}

	public void writeRecipeDetailsToExcel() throws IOException {
		// workbook object

		recipeData.put(row, 
				new Object[] { recipeTitle, category,ingredients,method,nutrientValues,imageLink,recipeLink });

		Set<String> keyid = recipeData.keySet();

		int rowid = 0;

		// writing the data into the sheets...

		for (String key : keyid) {

			rowno = spreadsheet.createRow(rowid++);
			Object[] objectArr = recipeData.get(key);
			int cellid = 0;

			for (Object obj : objectArr) {
				Cell cell = rowno.createCell(cellid++);
				cell.setCellValue((String)obj);
			}
		}
		FileOutputStream outputStream = new FileOutputStream("./data/Recipes_H.xlsx");

		workbook.write(outputStream);
		outputStream.close();
	}  

	@BeforeTest
	public void beforeTest() {
		/*System.setProperty("webdriver.chrome.driver", "C:\\SeleniumDriver\\chromedriver.exe"); 
		driver = new ChromeDriver();
		driver.navigate().to("https://www.tarladalal.com/");
		driver.manage().window().maximize();*/
		ChromeOptions options = new ChromeOptions();
		options.setHeadless(true);
		WebDriverManager.chromedriver().setup();
		// WebDriver Interface - Create an instance of the web driver of the browser
		driver = new ChromeDriver(options);
		driver.navigate().to("https://www.tarladalal.com/");
		driver.manage().window().maximize();

	}

	@AfterTest
	public void afterTest() {
		driver.close();

	}

}
