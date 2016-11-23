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


public class RunMe {

	public static RunMe runMe = new RunMe();

	public static void main(String[] args) throws IOException {
		
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
		
		//**************
		String basePath = new String(prop.getProperty("basePath"));
		String startingDoc = new String(prop.getProperty("startingDoc"));
		
//        Document doc = Jsoup.parse(new File(basePath + startingDoc),"utf-8");  
//        ChiBookDoc doc = new ChiBookDoc("File", "basePath + startingDoc","utf-8");  
//		ChiBookDoc doc = new ChiBookDoc(basePath);
//		(Document) doc = Jsoup.parse(new File(basePath + startingDoc),"utf-8");  
		ChiBookDoc doc = (ChiBookDoc) Jsoup.parse(new File(basePath + startingDoc),"utf-8");  
        int lvl = 0;
        
//        runMe = new RunMe();
        runMe.PrintDocInfo(doc, basePath, lvl);
        
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


            Elements scripts = theDoc.select("script");
            for (Element scr : scripts) {
            	boolean hasNextPrevVars = false;
            	String prevPageLink = new String("");
            	String nextPageLink = new String("");
            	try {
            		Pattern p1 = Pattern.compile("(?is)preview_page = \"(.+?)\""); // Regex for the value of the key
            		Matcher m1 = p1.matcher(scr.html()); // you have to use html here and NOT text! Text will drop the 'key' part
            		if( m1.find() )
            		{
            			hasNextPrevVars = true;
            			prevPageLink = m1.group(1); // value only
    					try {
    						p1 = Pattern.compile("(?is)next_page = \"(.+?)\""); // Regex for the value of the key
    						m1 = p1.matcher(scr.html()); // you have to use html here and NOT text! Text will drop the 'key' part
    						while (m1.find()) {
    							nextPageLink = m1.group(1); // value only
    						}
    					} catch (Exception e1) {
    						e1.printStackTrace();
    					}
            		}
            	} catch (Exception e1) {
            		e1.printStackTrace();
            	}

            	
            	if (hasNextPrevVars) {

            		System.out.println("\t prev: " + prevPageLink);
            		System.out.println("\t next: " + nextPageLink);
            	}


            }

			
			if (theLevel < 3) {
				try {
					Document doc2 = Jsoup.parse(new File(nextLink),	"utf-8");
					runMe.PrintDocInfo(doc2, theBasePath, theLevel + 1);
				} catch (Exception e) {
					System.out.println(tabs + "missing : " + nextLink);
//					e.printStackTrace();
				}
			}
	        
	    } 
	} 
	
	

}
