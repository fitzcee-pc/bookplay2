package bookplay2;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MyBookPage  {
//TODO: Make this an Interface/Superclass, which is implemented/Subclassed for each different book site. (e.g., 69chu.com)
	
	Document doc = null;
	private URL url = null;
	private String fullDecodedUrl = new String("");
	private String urlPathBeforePageName = new String("");
	private String thisPageName = new String("");
	private String thisPageOutfileName = new String("");
	private String indexPageName = new String("");
	private String prevPageName = new String("");
	private String nextPageName = new String("");
	private String pageName = new String("");
	private String author = new String("");
	private String chapterName = new String("");
	private String bookTitle = new String("");

	/*
	 *  Getters/Setters
	 */
	public String getBookTitle() {
		return bookTitle;
	}

	private void setBookTitle(String bookTitle) {
	this.bookTitle = bookTitle;
	}

	public String getAuthor() {
		return author;
	}

	private void setAuthor(String author) {
		this.author = author;
	}

	public String getChapterName() {
		return chapterName;
	}

	private void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}

	/* 
	 * Constructors
	 */
	public MyBookPage(Document theDoc, String outfileName) {
		
		// note underlying JSoup doc
		this.doc = theDoc;
		
		this.thisPageOutfileName = outfileName;

		initializePathBeforePageName();

		initializeThisPageName();
		
		initializeIndexPrevNextPageNames();
		
		// other constructor tasks
		
	}
	
	private void initializePathBeforePageName(){
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
	}

	private void initializeThisPageName() {
		// parse out current page name
		thisPageName = url.toString().substring(url.toString().lastIndexOf("/") + 1);
	}

	private void initializeIndexPrevNextPageNames() {
		// TODO can I pull at least some of the try/catches into a common proc?
		Elements scripts = this.doc.select("script");
		for (Element scr : scripts) {
			//		boolean hasNextPrevVars = false;
			try {
				Pattern p1 = Pattern.compile("(?is)preview_page = \"(.+?)\""); // Regex for the value of the key
				Matcher m1 = p1.matcher(scr.html()); // you have to use html here and NOT text! Text will drop the 'key' part
				if( m1.find() )
				{
					//				hasNextPrevVars = true;
					prevPageName = m1.group(1); // value only
					if (!prevPageName.substring(prevPageName.length() - 5).equalsIgnoreCase(".html")) {
						prevPageName = prevPageName.concat(".html");
					}
					prevPageName = prevPageName.substring(prevPageName.lastIndexOf("/") + 1);
					//				prevPageName = docURLBase.toString() + prevPageName;

					try {
						p1 = Pattern.compile("(?is)next_page = \"(.+?)\""); // Regex for the value of the key
						m1 = p1.matcher(scr.html()); // you have to use html here and NOT text! Text will drop the 'key' part
						while (m1.find()) {
							nextPageName = m1.group(1); // value only
							if (!nextPageName.substring(nextPageName.length() - 5).equalsIgnoreCase(".html")) {
								nextPageName = nextPageName.concat(".html");
							}
							nextPageName = nextPageName.substring(nextPageName.lastIndexOf("/") + 1);
							//						nextPageName = docURLBase.toString() + nextPageName;

						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					try {
						p1 = Pattern.compile("(?is)index_page = \"(.+?)\""); // Regex for the value of the key
						m1 = p1.matcher(scr.html()); // you have to use html here and NOT text! Text will drop the 'key' part
						while (m1.find()) {
							indexPageName = m1.group(1); // value only
							if (!indexPageName.substring(indexPageName.length() - 5).equalsIgnoreCase(".html")) {
								indexPageName = indexPageName.concat(".html");
							}
							indexPageName = indexPageName.substring(indexPageName.lastIndexOf("/") + 1);

						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					try {
						p1 = Pattern.compile("(?is)author=\'(.+?)\'"); // Regex for the value of the key
						m1 = p1.matcher(scr.html()); // you have to use html here and NOT text! Text will drop the 'key' part
						while (m1.find()) {
							setAuthor(m1.group(1)); // value only
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					
					try {
						p1 = Pattern.compile("(?is)chaptername=\'(.+?)\'"); // Regex for the value of the key
						m1 = p1.matcher(scr.html()); // you have to use html here and NOT text! Text will drop the 'key' part
						while (m1.find()) {
							setChapterName(m1.group(1)); // value only
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if (getChapterName().equals("")) {
						try {
							p1 = Pattern.compile("(?is)chaptername=\"(.+?)\""); // Regex for the value of the key
							m1 = p1.matcher(scr.html()); // you have to use html here and NOT text! Text will drop the 'key' part
							while (m1.find()) {
								setChapterName(m1.group(1)); // value only
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}

					try {
						p1 = Pattern.compile("(?is)articlename=\'(.+?)\'"); // Regex for the value of the key
						m1 = p1.matcher(scr.html()); // you have to use html here and NOT text! Text will drop the 'key' part
						while (m1.find()) {
							bookTitle = m1.group(1); // value only
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
	
					pageName = this.doc.title();
					Integer beginIndex = (pageName.indexOf(".") == -1) ? 0 : pageName.indexOf(".") + 1;
					
					pageName = pageName.substring(beginIndex, pageName.length());
				}


			} catch (Exception e1) {
				e1.printStackTrace();
			}


		}
	}

	public MyBookPage getNextPage() {
			
			MyBookPage myBookPage = null;
			try {
				myBookPage = new MyBookPage(Jsoup.parse(new File(this.urlPathBeforePageName + this.nextPageName),null),"");
			} catch (IOException e1) {
//				e1.printStackTrace();
			}  
			
			return myBookPage;

	}
	
	public String getBodyText() {

		String bodyText = new String("body");
		
		Elements bodyDivX = doc.getElementsByClass("yd_text2");
		Element bodyElement = bodyDivX.first();
		bodyText = bodyDivX.first().toString();

		/* findings:
		 * - file has 	
		 * 				...
		 * 				<div class="yd_text2">
		 * 					<div id="txtright">
		 * 						<script>txttopshow3();
		 * 						</script>
		 * 					</div>
		 * 					<!--章节内容开始-->&nbsp;&nbsp;&nbsp;&nbsp;八角亭中传来吟吟的笑声，几名娇美的少女正在讨论些什么，脸上挂着明媚的笑容，而在她们的身侧还坐着两个男子，俊逸非凡，一看便知不是普通人，身上自有一番贵气。<br />
		 * 					<br />&nbsp;&nbsp;&nbsp;&nbsp;“韩公子昨日戏弄陈家小姐可是弄得人 ...
		 * 					...
		 * 					<br />&nbsp;&nbsp;&nbsp;&nbsp;听到她的责骂，温微暖却是没什么反应，反而还疑惑地看着温薇菱，“哪里脏了？鱼儿很可爱啊，二姐你吃它的时候怎么不觉得脏？”
		 * 					<script>txttopshow7();
		 * 					</script>
		 * 					<!--章节内容结束-->
		 * 				</div>
		 * 				<script>tool2();</script>
		 * 				<center>
		 * 					<script>txttopshow4();</script>
		 * 				</center>
		 * 				...
		 * - targeting elementById("txtright") and accessing .parent() gets you everything from <div class="yd_text2"> through </div> after txttopshow7 and <!--...-->
		 * 		- could also get this by targeting elementsByClass and then accessing .first()
		 * - TODO strip out the divs and scripts
		 */
		// regex: https://www.tutorialspoint.com/java/java_regular_expressions.htm
		//        https://www.udemy.com/learn-to-program-with-java/?tc=blog.javastringreplaceall&couponCode=half-off-for-blog&utm_source=blog&utm_medium=udemyads&utm_content=post118252&utm_campaign=content-marketing-blog&xref=blog
		//		  http://stackoverflow.com/questions/7124778/how-to-match-anything-up-until-this-sequence-of-characters-in-a-regular-expres
		// replaceAll: https://blog.udemy.com/java-string-replaceall/
		String bodyTextTrimmed = bodyText.replaceFirst("<div class=\"yd_text2\">","");
		bodyText = bodyTextTrimmed;
		bodyTextTrimmed = bodyText.replaceFirst("<div id=\"txtright\">[\\S\\s]*?</div>","");
		bodyText = bodyTextTrimmed;
		bodyTextTrimmed = bodyText.replaceAll("<!--[\\S\\s]*?-->","");
		bodyText = bodyTextTrimmed;
		bodyTextTrimmed = bodyText.replaceFirst("<script>[\\S\\s]*?</script>","");
		bodyText = bodyTextTrimmed;
		bodyTextTrimmed = bodyText.replaceFirst("</div>","");
		bodyText = bodyTextTrimmed;
		bodyTextTrimmed = bodyText.replaceAll("&nbsp;[\\S\\s]*?(?=[^(&nbsp;)])","");
		bodyText = bodyTextTrimmed;
		bodyTextTrimmed = bodyText.replaceAll("<br>","<br />");

		return bodyTextTrimmed;
	}
	
	public String toString() {
		String me = new String("at: " + this.doc.baseUri()
				+ "\n\tbase of url: " + this.urlPathBeforePageName
				+ "\n\ttitle: " + this.doc.title()
				+ "\n\tthis page: " + this.thisPageName
				+ "\n\tprev page: " + this.prevPageName
				+ "\n\tnext page: " + this.nextPageName
				+ "\n\tindex page: " + this.indexPageName);
		return me;
		
	}
	
}
