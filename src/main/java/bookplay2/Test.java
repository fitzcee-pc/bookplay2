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
	private static String runDateTime;
	private static Book book;
	private static String outTextPathAndFile;

	public static void main(String[] args) throws IOException {
		
		props = getProps();
		
		String basePath = new String(props.getProperty("basePath"));
		String startingDoc = new String(props.getProperty("startingDoc"));
		outTextPathAndFile = new String(props.getProperty("outTextPathAndFile"));
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date today = Calendar.getInstance().getTime();        
		runDateTime = df.format(today);
		
		BookPage bookPage = new BookPage(Jsoup.parse(new File(basePath + startingDoc),null));  

		book = new Book(null, bookPage, null);

		System.out.println(runDateTime);

		System.out.println(getBookDeets());

		writeDeetsToOutfile(getBookDeets());

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

	private static String getBookDeets() {
		String bookDeets;
		
		bookDeets = "\n" + book.toString();
		bookDeets += "\n" + book.firstPage.getBodyText();
		bookDeets += "\n";

		
		return bookDeets;
	}	

	private static void writeDeetsToOutfile(String theDeets) {
		List<String> lines = Arrays.asList(new String[] { //!!!!! NOTE: display in file fixed by using charset=UTF-8 instead of charset=gbk. (don't know where I got that originally)
				"<html>"
				,"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head>"
				, "<body>"
				, runDateTime
				, theDeets
				, "</body>"
				, "</html>" 
		});

		Path path = Paths.get(outTextPathAndFile);
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
