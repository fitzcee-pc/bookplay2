package bookplay2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Test {

	public static Test test = new Test();
	private static Properties props;

	public static void main(String[] args) throws IOException {
		
		props = getProps();
		
		//**************
		String basePath = new String(props.getProperty("basePath"));
		String startingDoc = new String(props.getProperty("startingDoc"));
		String outTextPathAndFile = new String(props.getProperty("outTextPathAndFile"));
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date today = Calendar.getInstance().getTime();        
		String runDateTime = df.format(today);
		
		
		BookPage chiDocPage = new BookPage(Jsoup.parse(new File(basePath + startingDoc),null));  
		
//		System.out.println(runDateTime);
//		System.out.println(chiDocPage.toString());
//		System.out.println();

		Book chiBook = new Book(null, chiDocPage, null);
		System.out.println(runDateTime);
		System.out.println(chiBook.toString());
		System.out.println(chiBook.firstPage.getBodyText()); // !!!! NOTE: display in console was fixed(?) by hard-selecting UTF-8 in eclipse run->run config->common
		System.out.println();

		String pageCountString = chiBook.PagesBetween(chiBook.firstPage, chiBook.lastPage).toString();
		
		List<String> lines = Arrays.asList(new String[] { //!!!!! NOTE: display in file fixed by using charset=UTF-8 instead of charset=gbk. (don't know where I got that originally)
//				"<html>"
//				,"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head>"
//				, "<body>boo"
//				, runDateTime
				runDateTime
				, chiBook.toString()
				, "pageCount: " + pageCountString
				, "first page body: " + chiBook.firstPage.getBodyText()
				, "</body>"
				, "</html>" });
		Path path = Paths.get(outTextPathAndFile);
        Files.write(path, lines, StandardCharsets.UTF_8);
        System.out.println();
        System.out.println("===========================");
        System.out.println(lines);
        
		
//		chiDocPage = new BookPage(Jsoup.parse(new File(chiDocPage.urlPathBeforePageName + chiDocPage.nextPageName),null));  
//		System.out.println(runDateTime);
//		System.out.println(chiDocPage.toString());
//		System.out.println();
//
//		
//		chiBook = new Book(null, null, chiDocPage);
//		System.out.println(runDateTime);
//		System.out.println(chiBook.toString());
//		System.out.println();
		
	}

	private static Properties getProps() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("src/main/resources/config.properties");
			// load a properties file
			prop.load(input);			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

	public void PrintDocInfo(Document theDoc, String theBasePath, int theLevel) throws IOException {

//		URL url = new URL();
		
		String tabs = new String("");
		for (int i = 0; i < theLevel; i++) {
			tabs += "\t";
		}
	    String title = theDoc.title();  
	    System.out.println(tabs + "title is: " + title);  
	    
	    Elements links = theDoc.select("a[href]");  
	    for (Element link : links) {  
	        System.out.println("\n" + tabs + "link : " + link.attr("href"));  
	        System.out.println(tabs + "text : " + link.text());  
	    
	        String nextLink = new String(theBasePath + link.attr("href"));
	        System.out.println(tabs + "next link : " + nextLink);

			if (theLevel < 3) {
				try {
					Document doc2 = Jsoup.parse(new File(nextLink),	"utf-8");
					test.PrintDocInfo(doc2, theBasePath, theLevel + 1);
				} catch (Exception e) {
					System.out.println(tabs + "missing : " + nextLink);
//					e.printStackTrace();
				}
			}
	        
	    } 
	} 
	
	

}
