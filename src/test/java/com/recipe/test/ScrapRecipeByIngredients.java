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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;

public class ScrapRecipeByIngredients {
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
	CommonMethods cm= new CommonMethods();
	
	XSSFWorkbook workbook ;
	String row;
	FileOutputStream outputStream;
	
	HashMap<String, Integer> recipes = new HashMap<>();

	@Test
	public void scrapRecipe() throws InterruptedException, IOException {
		cm.writeHeaderToExcel();

		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebDriverWait wait = new WebDriverWait(driver, 5);
		driver.findElement(By.xpath("//a[@href='recipecategories.aspx?srchboxopt=r']")).click();
		//driver.findElement(By.xpath("//div[@id='ctl00_cntleftpanel_ingtree_tvIngn0Nodes']//table//tbody//tr//td[2]//a[@id='ctl00_cntleftpanel_ingtree_tvIngn69']")).click();

		List<WebElement> ingredientsInsidePlus = driver.findElements(By.xpath("//div[@id='ctl00_cntleftpanel_ingtree_tvIngn0Nodes']//table//tbody//tr//td[2]//a[@href]"));
		List<String> inPlus = new ArrayList<String>();
		ingredientsInsidePlus.forEach((linkelement) -> inPlus.add(linkelement.getAttribute("href")));

		int inPlusSize = ingredientsInsidePlus.size();
		System.out.println("InPlusSize:"+inPlusSize);
		for (int i = 0; i < inPlusSize; i++) {
			System.out.println("Plus number:"+i);
			ingredientsInsidePlus.get(i).click();
		List<WebElement> ingredientsInsideLinks = driver.findElements(By.xpath("//div[@id='ctl00_cntleftpanel_ingtree_tvIngn0Nodes']//div//table//tbody//tr//td[4]//a"));
		List<String> inlinks = new ArrayList<String>();
		ingredientsInsideLinks.forEach((linkelement) -> inlinks.add(linkelement.getAttribute("href")));

		int inlinksSize = ingredientsInsideLinks.size();
		System.out.println("IngredientsInsideLinks:"+ingredientsInsideLinks);
		for (int k = 0; k < inlinksSize; k++) {
			System.out.println("Inside Link :"+k+":: page url ::"+inlinks.get(k));
			driver.navigate().to(inlinks.get(k));
			// Traversing through the pages(using loop)
			recipeListIteration(driver,"Recipe_Ingredients");
			//div[@itemprop='ItemList']//article//div[@class='rcc_rcpcore']//span[@class='rcc_recipename']//a
		}
		}
	}

	@BeforeTest
	public void beforeTest() {

		driver = cm.setUpHeadlessBrowser(driver);

	}

	@AfterTest
	public void afterTest() {
		driver.close();

	}
	public void recipeListIteration(WebDriver driver, String excelName) throws InterruptedException, IOException {
		//div[@itemprop='ItemList']//article//div[@class='rcc_rcpcore']//span[@class='rcc_recipename']//a
		//WebElement list = driver.findElement(By.xpath("//div[@itemprop='ItemList']"));
		List<WebElement> allLinks = driver.findElements(By.xpath("//div[@itemprop='ItemList']//article//div[@class='rcc_rcpcore']//span[@class='rcc_recipename']//a"));
		List<String> links = new ArrayList<String>();
		allLinks.forEach((linkelement) -> links.add(linkelement.getAttribute("href")));

		int totcount = allLinks.size();
		System.out.println("Total number of recipes::"+totcount);
		for (int i = 0; i < totcount; i++) {
			System.out.println("Recipe number :"+i+":: Recipe url ::"+links.get(i));
			driver.navigate().to(links.get(i));

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

					category = driver.findElement(By.xpath("//div[@id='recipe_tags']")).getText();

					imageLink = driver.findElement(By.xpath("//img[@id='ctl00_cntrightpanel_imgRecipe']"))
							.getAttribute("src");

					driver.manage().timeouts().implicitlyWait(1000, TimeUnit.SECONDS);
					row += String.valueOf(i);

					writeRecipeDetailsToExcel(excelName);
				}else 
					System.out.println("**Error page :: Its not a proper webpage**");
			}else {
				System.out.println("**Either error page or Duplicate Recipe**");
			}

		}
	}

	/*public void writeHeaderToExcel() {
		workbook = new XSSFWorkbook();
		// spreadsheet object
		spreadsheet= workbook.createSheet(" Recipe Data ");

		// This data needs to be written (Object[])

		recipeData.put(
				"1",
				new Object[] { "Title", "Category", "Ingredients","Method", "Nutrient Values","Image Link","Recipe Link"});
	}*/

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
}
