package bookplay2;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Book  {
//TODO: Make this an Interface/Superclass, which is implemented/Subclassed for each different book site. (e.g., 69chu.com)
	
	Document doc = null;
	private URL url = null;
	private String fullDecodedUrl = new String("");
	String urlPathBeforePageName = new String("");
	String thisPageName = new String("");
	String prevPageName = new String("");;
	String nextPageName = new String("");;
	
	public Book(Document theDoc) {
		
		// note underlying JSoup doc
		this.doc = theDoc;
		
		// parse out URL before page name
		try {
			url = new File(this.doc.baseUri()).toURI().toURL();
			} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fullDecodedUrl = URLDecoder.decode(url.toString(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		urlPathBeforePageName = url.getPath();
		urlPathBeforePageName = urlPathBeforePageName.substring(0, urlPathBeforePageName.lastIndexOf("/") + 1);
		
		try {
			urlPathBeforePageName = URLDecoder.decode(urlPathBeforePageName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// parse out current page name
		thisPageName = url.toString().substring(url.toString().lastIndexOf("/") + 1);
		
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
					prevPageName = m1.group(1); // value only
					if (!prevPageName.substring(prevPageName.length() - 5).equalsIgnoreCase(".html")) {
						prevPageName = prevPageName.concat(".html");
					}
					prevPageName = prevPageName.substring(prevPageName.lastIndexOf("/") + 1);
//					prevPageName = docURLBase.toString() + prevPageName;

					try {
						p1 = Pattern.compile("(?is)next_page = \"(.+?)\""); // Regex for the value of the key
						m1 = p1.matcher(scr.html()); // you have to use html here and NOT text! Text will drop the 'key' part
						while (m1.find()) {
							nextPageName = m1.group(1); // value only
							if (!nextPageName.substring(nextPageName.length() - 5).equalsIgnoreCase(".html")) {
								nextPageName = nextPageName.concat(".html");
							}
							nextPageName = nextPageName.substring(nextPageName.lastIndexOf("/") + 1);
//							nextPageName = docURLBase.toString() + nextPageName;

						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		
			
		}
		
		// other constructor tasks
		
	}
	
	public String toString() {
		String me = new String("at: " + this.doc.baseUri()
				+ "\n\tbase of url: " + this.urlPathBeforePageName
				+ "\n\ttitle: " + this.doc.title()
				+ "\n\tthis page: " + this.thisPageName
				+ "\n\tprev page: " + this.prevPageName
				+ "\n\tnext page: " + this.nextPageName);
		return me;
		
	}
	
}
