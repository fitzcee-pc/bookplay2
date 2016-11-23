package bookplay2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;

public class ChiBookDoc  {
//TODO: Make this an Interface/Superclass, which is implemented/Subclassed for each different book site. (e.g., 69chu.com)
	
	Document doc = null;
	String prevPageLink = new String("");;
	String nextPageLink = new String("");;
	
	public ChiBookDoc(Document theDoc) {
		
		// note underlying JSoup doc
		this.doc = theDoc;
		
		// find next/prev page links
        Elements scripts = this.doc.select("script");
		for (Element scr : scripts) {
			boolean hasNextPrevVars = false;
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
		
		// other constructor tasks

	}
	
}
