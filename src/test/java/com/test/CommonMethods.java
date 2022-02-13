package com.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class CommonMethods {
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
	FileOutputStream outputStream;
	/**
	 * @param driver 
	 * @param excelName 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void iterateRecipes(WebDriver driver, String excelName) throws InterruptedException, IOException {
		List<WebElement> pagination = driver.findElements(By.xpath("//div[@id='pagination']/a"));
		List<String> pages = new ArrayList<String>();
		pagination.forEach((linkelement) -> pages.add(linkelement.getAttribute("href")));

		int pgSize = pagination.size();
		if (pagination.size() != 0) {
			for (int j = 0; j <pgSize; j++) {
				Thread.sleep(1000);
				System.out.println("Page number :"+j+":: page url ::"+pages.get(j));
				driver.navigate().to(pages.get(j));
				recipeListIteration(driver, excelName);
			}
		}else {
			recipeListIteration(driver, excelName);
		}
	}
	/**
	 * @param driver
	 * @param excelName
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void recipeListIteration(WebDriver driver, String excelName) throws InterruptedException, IOException {
		WebElement list = driver.findElement(By.xpath("//div[@class='recipelist']"));
		List<WebElement> allLinks = list.findElements(By.xpath("//span[@class='rcc_recipename']/a"));
		List<String> links = new ArrayList<String>();
		allLinks.forEach((linkelement) -> links.add(linkelement.getAttribute("href")));

		int totcount = allLinks.size();

		for (int i = 0; i < totcount; i++) {
			System.out.println("Recipe number :"+i+":: Recipe url ::"+links.get(i));
			driver.navigate().to(links.get(i));

			// To get recipe ingredients
			recipeLink = driver.getCurrentUrl();

			HashMap<String, Integer> recipes = new HashMap<>();

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

					Thread.sleep(1000);
					row += String.valueOf(i);//not workinas it taking one link from each page and appending the excel

					writeRecipeDetailsToExcel(excelName);
				}else 
					System.out.println("**Error page :: Its not a proper webpage**");
			}else {
				System.out.println("**Either error page or Duplicate Recipe**");
			}

		}
	}
	public void writeHeaderToExcel() {
		workbook = new XSSFWorkbook();
		// spreadsheet object
		spreadsheet
		= workbook.createSheet(" Recipe Data ");

		// This data needs to be written (Object[])

		recipeData.put(
				"1",
				new Object[] { "Title", "Category", "Ingredients","Method", "Nutrient Values","Image Link","Recipe Link"});
	}

	public void writeRecipeDetailsToExcel(String excelName) throws IOException {
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

	public static boolean isRecipeLinkAlreadyPresent(String recipeLink, List<String> hList) {
		if(hList.contains(recipeLink)) {
			return true;
		}
		return false;
	}
}
