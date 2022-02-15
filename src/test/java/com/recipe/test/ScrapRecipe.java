package com.recipe.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Reporter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ScrapRecipe {
	WebDriver driver = null;
	String recipeTitle;
	String category;
	String ingredients;
	String method;
	String nutrientValues;
	String imageLink;
	String recipeLink;
	@Test
	public void f() {
	}

	@BeforeTest
	public void beforeTest() throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C:\\SeleniumDriver\\chromedriver.exe");
		driver = new ChromeDriver();
		//Navigate to the url
		driver.navigate().to("https://www.tarladalal.com/");
		driver.manage().window().maximize();
		Reporter.log("Tarla Dalal page is launched successsfully");
		//Implicit wait
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		Thread.sleep(2000);
	}


	@Test (priority = 1)
	public void SearchRecipe()
	{
		String recipe_search ;
		//Create a Scanner object to prompt for user input
		Scanner myObj = new Scanner(System.in);
		System.out.println("What recipe are you looking for ? ");
		recipe_search = myObj.nextLine();
		driver.findElement(By.id("ctl00_txtsearch")).sendKeys(recipe_search);
		driver.findElement(By.id("ctl00_imgsearch")).click();
	}


	@Test (priority = 2)
	public void RecipeSearchPage()
	{
		JavascriptExecutor js = (JavascriptExecutor) driver; //Interface cast
		js.executeScript("window.scrollBy(0,700)");
		driver.findElement(By.xpath("//span//a[@href='mini-beetroot-pancake-39444r']")).click();
	}

	@Test (priority = 3)
	public void RecipeDescriptionPage_getRecipeTitle()
	{
		JavascriptExecutor js = (JavascriptExecutor) driver; //Interface cast
		js.executeScript("window.scrollBy(0,500)");
		recipeTitle=driver.findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblRecipeName']")).getText();
		System.out.println("Recipe title:"+recipeTitle);
	}

	@Test (priority = 4)
	public void RecipeDescriptionPage_getRecipeCategory()
	{
		category=driver.findElement(By.xpath("//div[@id='recipe_tags']")).getText();
		System.out.println("Category:"+category);
	}

	@Test (priority = 5)
	public void RecipeDescriptionPage_getRecipeIngredients()
	{
		ingredients=driver.findElement(By.xpath("//div[@id='rcpinglist']")).getText();
		System.out.println("Ingredients:"+ingredients);
	}

	@Test (priority = 6)
	public void RecipeDescriptionPage_getRecipeMethod()
	{
		method=driver.findElement(By.xpath("//div[@id='ctl00_cntrightpanel_pnlRcpMethod']")).getText();
		System.out.println("Method:"+method);
	}


	@Test (priority = 7)
	public void RecipeDescriptionPage_getRecipeNutrientValues()
	{
		nutrientValues=driver.findElement(By.xpath("//div[@id='rcpnuts']")).getText();
		System.out.println("NutrientValues:"+nutrientValues);
	}

	@Test (priority = 8)
	public void RecipeDescriptionPage_getRecipeImageLink()
	{
		imageLink=driver.findElement(By.xpath("//img[@id='ctl00_cntrightpanel_imgRecipe']")).getAttribute("src");
		System.out.println("Image Link:"+imageLink);
	}

	@Test (priority = 9)
	public void RecipeDescriptionPage_getRecipeLink() throws IOException
	{
		recipeLink=driver.getCurrentUrl();
		System.out.println("RecipeLink:"+recipeLink);
		writeRecipeDetailsToExcel();
		
	}

	public void writeRecipeDetailsToExcel() throws IOException {
		// workbook object
		XSSFWorkbook workbook = new XSSFWorkbook();

		// spreadsheet object
		XSSFSheet spreadsheet
		= workbook.createSheet(" Recipe Data ");

		// creating a row object
		XSSFRow row;

		// This data needs to be written (Object[])
		Map<String, Object[]> recipeData = new TreeMap<String, Object[]>();

		recipeData.put(
				"1",
				new Object[] { "Title", "Category", "Ingredients","Method", "Nutrient Values","Image Link","Recipe Link"});

		recipeData.put("2", 
				new Object[] { recipeTitle, category,ingredients,method,nutrientValues,imageLink,recipeLink });
		
		Set<String> keyid = recipeData.keySet();
		  
        int rowid = 0;
  
        // writing the data into the sheets...
  
        for (String key : keyid) {
  
            row = spreadsheet.createRow(rowid++);
            Object[] objectArr = recipeData.get(key);
            int cellid = 0;
  
            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String)obj);
            }
        }
  
        // .xlsx is the format for Excel Sheets...
        // writing the workbook into the file...
        //FileOutputStream out = new FileOutputStream(new File("C:/savedexcel/recipesheet.xlsx"));
        FileOutputStream outputStream = new FileOutputStream("./data/Recipes.xlsx");
  
        workbook.write(outputStream);
        outputStream.close();
	}
}
