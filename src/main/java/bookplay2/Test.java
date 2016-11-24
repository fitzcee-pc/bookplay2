package bookplay2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
		
//        Document doc = Jsoup.parse(new File(basePath + startingDoc),"utf-8");  
//        ChiBookDoc doc = new ChiBookDoc("File", "basePath + startingDoc","utf-8");  
//		ChiBookDoc doc = new ChiBookDoc(basePath);
//		(Document) doc = Jsoup.parse(new File(basePath + startingDoc),"utf-8");  
		Book chiDoc = new Book(Jsoup.parse(new File(basePath + startingDoc),"utf-8"));  
//        int lvl = 0;
		
//        System.out.println("title: " + chiDoc.doc.title());
//        System.out.println("prev: " + chiDoc.prevPageLink);
//        System.out.println("next: " + chiDoc.nextPageLink);

		System.out.println(chiDoc.toString());
		
		chiDoc = new Book(Jsoup.parse(new File(chiDoc.urlBeforePageName + chiDoc.nextPageName),"utf-8"));  
		System.out.println(chiDoc.toString());
//
//        System.out.println("title: " + chiDoc.doc.title());
//        System.out.println("prev: " + chiDoc.prevPageLink);
//        System.out.println("next: " + chiDoc.nextPageLink);
      
//        test.PrintDocInfo(chiDoc.doc, basePath, lvl);
        
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
