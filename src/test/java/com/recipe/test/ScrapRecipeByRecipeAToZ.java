package com.recipe.test;

import org.testng.annotations.Test;

import com.recipe.util.CommonMethods;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.BeforeTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;

public class ScrapRecipeByRecipeAToZ {
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
	HashMap<String, Integer> recipes = new HashMap<>();
	FileOutputStream outputStream;
	@Test
	public void scrapRecipe() throws InterruptedException, IOException {
		driver.findElement(By.xpath("//div[@id='toplinks']//a[5]")).click();
		List<WebElement> menuAlphabets = driver.findElements(By.xpath("//table[@id='ctl00_cntleftpanel_mnuAlphabets']//tbody//tr//td//a"));

		List<String> links = new ArrayList<String>();
		menuAlphabets.forEach((linkelement) -> links.add(linkelement.getText()));

		int linksSize = menuAlphabets.size();
		System.out.println("linksSize:"+linksSize);
		String clicklnk = Keys.chord(Keys.CONTROL,Keys.ENTER);
		for (int j = 1; j < linksSize-1; j++) {
			System.out.println("link number:"+j);
			//System.out.println("web page link:"+links.get(j));
			driver.manage().timeouts().implicitlyWait(1000, TimeUnit.SECONDS);
			menuAlphabets.get(j).sendKeys(clicklnk);
			System.out.println("Current URL:"+driver.getCurrentUrl());
			List<WebElement> pagination = driver.findElements(By.xpath("//div[4]//a[contains(@href,\"/RecipeAtoZ.aspx?beginswith\")]"));
			List<String> pages = new ArrayList<String>();
			pagination.forEach((linkelement) -> pages.add(linkelement.getAttribute("href")));

			int pgSize = pagination.size();
			if (pagination.size() != 0) {
				for (int k = 0; k <pgSize; k++) {
					driver.manage().timeouts().implicitlyWait(1000, TimeUnit.SECONDS);
					System.out.println("Page number :"+k+":: page url ::"+pages.get(k));
					driver.navigate().to(pages.get(k));
					recipeListIteration(driver, "RecipesAToZ_1");
				}
			}else {
				recipeListIteration(driver, "RecipesAToZ_1");
			}
		}

	}

	public void recipeListIteration(WebDriver driver, String excelName) throws InterruptedException, IOException {
		//WebElement list = driver.findElement(By.xpath("//div[@class='recipelist']"));
		List<WebElement> allLinks = driver.findElements(By.xpath("//div//div[@class='rcc_recipecard']//div[3]//span[@class='rcc_recipename']//a"));
		List<String> links = new ArrayList<String>();
		allLinks.forEach((linkelement) -> links.add(linkelement.getAttribute("href")));

		int totcount = allLinks.size();

		for (int i = 0; i < totcount; i++) {
			System.out.println("Recipe number :"+i+":: Recipe url ::"+links.get(i));
			driver.navigate().to(links.get(i));
			driver.manage().timeouts().implicitlyWait(1000, TimeUnit.SECONDS);
			//Get Category
			category = driver.findElement(By.xpath("//div[@id='recipe_tags']")).getText();
			if(!category.contains("No Cooking")) {
				// To get recipe ingredients
				recipeLink = driver.getCurrentUrl();

				List<String> recipeLinkList = new ArrayList();
				recipeLinkList.add(recipeLink);

				for(String recipeLink : recipeLinkList)
				{
					if(recipes.containsKey(recipeLink))
					{
						int count = recipes.get(recipeLink);
						System.out.println("count:"+count);
						recipes.put(recipeLink,  count+1);
					}
					else {
						recipes.put(recipeLink,  1);
					}
				}

				if(recipes.get(recipeLink) == 1) {

					// To get ingredients values
					List<WebElement> ingredientsElement = driver.findElements(By.xpath("//div[@id='rcpinglist']"));
					if (ingredientsElement.size() != 0) {
						ingredients = driver.findElement(By.xpath("//div[@id='rcpinglist']")).getText();
						// To get recipe methods
						method = driver.findElement(By.xpath("//div[@id='ctl00_cntrightpanel_pnlRcpMethod']/div"))
								.getText();


						// To get Nutrition values
						List<WebElement> dynamicElement = driver
								.findElements(By.xpath("//div[@id='recipe_nutrients']/div//table"));
						if (dynamicElement.size() != 0) {
							nutrientValues = driver.findElement(By.xpath("//div[@id='recipe_nutrients']/div//table"))
									.getText();

							// If list size is non-zero, element is present
						} else
							// Else if size is 0, then element is not present
							System.out.println("Element not present");

						recipeTitle = driver.findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblRecipeName']"))
								.getText();


						imageLink = driver.findElement(By.xpath("//img[@id='ctl00_cntrightpanel_imgRecipe']"))
								.getAttribute("src");

						driver.manage().timeouts().implicitlyWait(1000, TimeUnit.SECONDS);
						row += String.valueOf(i);//not workinas it taking one link from each page and appending the excel

						writeRecipeDetailsToExcel(excelName);
					}else 
						System.out.println("**Error page :: Its not a proper webpage**");
				}else {
					System.out.println("**Duplicate Recipe**");
				}

			}else{
				System.out.println("***Page is not related Recipes***");
			}}
	}

	public void writeRecipeDetailsToExcel(String excelName) throws IOException {
		workbook = new XSSFWorkbook();
		// spreadsheet object
		spreadsheet= workbook.createSheet(" Recipe Data ");

		// This data needs to be written (Object[])

		recipeData.put(
				"1",
				new Object[] { "Title", "Category", "Ingredients","Method", "Nutrient Values","Image Link","Recipe Link"});
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
		outputStream = new FileOutputStream("./data/"+excelName+".xlsx");

		workbook.write(outputStream);
		outputStream.close();
	}  

	@BeforeTest
	public void beforeTest() {
		//COMMON WAY TO RUN THE SCRIPT
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.navigate().to("https://www.tarladalal.com/");
		driver.manage().window().maximize();


		//TO RUN THE SCRIPT FASTER
		//System.setProperty("webdriver.chrome.driver", "C:\\SeleniumDriver\\chromedriver.exe");
		/*WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.managed_default_content_settings.javascript", 2);
		options.setExperimentalOption("prefs", prefs);
		driver = new ChromeDriver(options);
		driver.navigate().to("https://www.tarladalal.com/");
		driver.manage().window().maximize();*/


	}

	@AfterTest
	public void afterTest() {
		driver.close();

	}

}
