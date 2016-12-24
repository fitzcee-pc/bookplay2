package bookplay2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public class EpubSourceMaterial {

	/*
	 * EPUB folder structure:
	 * mimetype
	 * META-INF/container.xml
	 * OEBPS/content.opf
	 * OEBPS/toc.ncx
	 * OEBPS/cover.jpg   (optional)
	 * OEBPS/coverpage.html		(optional.  used to display cover image on readers that don't honor <id=cover>
	 * OEBPS/Images/<image.jpg>  (optional.  any images used in content)
	 * OEBPS/Styles/style.css
	 * OEBPS/Text/chap002.xhtml
	 * OEBPS/Text/chap003.xhtml
	 * OEBPS/Text/chap004.xhtml
	 * OEBPS/Text/Contents.xhtml
	 * OEBPS/Text/htmltoc.xhtml  
	 */	

	private String srcPathRoot;
	private static String srcPathMetaInf;
	private static String srcPathOebpsRoot;
	private static String srcPathOebpsImages;
	private static String srcPathOebpsStyles;
	private static String srcPathOebpsText;
	private List<String> chapterPathAndFilenames = new ArrayList<>(Arrays.asList());
	private static String bookAuthor = "unknown author";
	private static String bookTitle = "unknown title";
	private Integer level1Multiple;
	private String srcDocImagePath;
	private String srcDocImageFilename;

	/*
	 * constructors
	 */
	
	public String getSrcDocImagePath() {
		return srcDocImagePath;
	}

	public void setSrcDocImagePath(String srcDocImagePath) {
		this.srcDocImagePath = srcDocImagePath;
		this.srcDocImageFilename = srcDocImagePath.substring(srcDocImagePath.lastIndexOf("/") + 1, srcDocImagePath.length());
//				urlPathBeforePageName = urlPathBeforePageName.substring(0, urlPathBeforePageName.lastIndexOf("/") + 1);

	}

	public EpubSourceMaterial(String theSrcRoot) {
		setSrcRoot(theSrcRoot);
		level1Multiple = 10;
	}

	public EpubSourceMaterial(String theSrcRoot, Integer level1Multiple) {
		setSrcRoot(theSrcRoot);
		setLevel1Multiple(level1Multiple);
	}

	/*
	 * getters and setters
	 */
	public static String getBookAuthor() {
		return bookAuthor;
	}
	
	public static void setBookAuthor(String bookAuthor) {
		EpubSourceMaterial.bookAuthor = bookAuthor;
	}
	
	public static String getBookTitle() {
		return bookTitle;
	}
	
	public static void setBookTitle(String bookTitle) {
		EpubSourceMaterial.bookTitle = bookTitle;
	}
	
	public String getSrcRoot() {
		return srcPathRoot;
	}

	public void setSrcRoot(String theSrcRoot) {
		srcPathRoot = theSrcRoot;
		srcPathMetaInf = srcPathRoot + "META-INF" + File.separator;
		srcPathOebpsRoot = srcPathRoot + "OEBPS" + File.separator;
		srcPathOebpsImages = srcPathOebpsRoot + "Images" + File.separator;
		srcPathOebpsStyles = srcPathOebpsRoot + "Styles" + File.separator;
		srcPathOebpsText = srcPathOebpsRoot + "Text" + File.separator;
	}


	public Integer getLevel1Multiple() {
		return level1Multiple;
	}

	public void setLevel1Multiple(Integer level1Multiple) {
		this.level1Multiple = level1Multiple;
	}

	/*
	 * public methods
	 */
	public void createMimetypeFile() {
		List<String> lines = Arrays.asList(new String[] { 
				"application/epub+zip"
		});

		Path path = Paths.get(srcPathRoot + "mimetype");

// WTF: Files.write was appending an extra, empty line at end of file.  This caused epub validation to fail		
//      as mimetype technically had more than the specified 20 characters in it.  (Calibre could still read it though)
//      Switch to BufferedWriter made it work.
//
//		try {
////			Files.write(path, lines, StandardCharsets.UTF_8);
//			Files.write(path, Collections.singletonList("application/epub+zip"), StandardCharsets.UTF_8);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		String txt = new String("application/epub+zip");
		final int chunkSize=8000;
		try(Writer w=Files.newBufferedWriter(path)) {
		    for(int s=0, e; s<txt.length(); s=e) {
		        e=Math.min(s+chunkSize, txt.length());
		        w.append(txt.subSequence(s, e));
		    }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void createContainerFile() {
		List<String> lines = Arrays.asList(new String[] { 
				  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				, "<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">"
				, "   <rootfiles>"
				, "       <rootfile full-path=\"OEBPS/content.opf\" media-type=\"application/oebps-package+xml\"/>"
				, "   </rootfiles>"
				, "</container>"

		});

		Path path = Paths.get(srcPathMetaInf + "container.xml");
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void createCssFile() {
		List<String> lines = Arrays.asList(new String[] { 
				  "/* Style Sheet */"
				, "/* This defines styles and classes used in the book */"
				, "body { margin-left: 5%; margin-right: 5%; margin-top: 5%; margin-bottom: 5%; text-align: justify; }"
				, "pre { font-size: x-small; }"
				, "h1 { text-align: center; }"
				, "h2 { text-align: center; }"
				, "h3 { text-align: center; }"
				, "h4 { text-align: center; }"
				, "h5 { text-align: center; }"
				, "h6 { text-align: center; }"
				, ".CI {"
				, "    text-align:center;"
				, "    margin-top:0px;"
				, "    margin-bottom:0px;"
				, "    padding:0px;"
				, "    }"
				, ".center   {text-align: center;}"
				, ".smcap    {font-variant: small-caps;}"
				, ".u        {text-decoration: underline;}"
				, ".bold     {font-weight: bold;}"

		});

		Path path = Paths.get(srcPathOebpsStyles + "style.css");
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void createEpubSrc_TocFile___OverlyComplicated() {
		// according to one source, much of this is not needed.
		// https://gist.github.com/elmimmo/d7b8dbebc4e972734e9a
//		List<String> lines = Arrays.asList(new String[] { 
//				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//				, "<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\">"
//				, " "
//				, "<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\">"
//				, "	<head>"
//				, "		<meta name=\"dtb:uid\" content=\"015ffaec-9340-42f8-b163-a0c5ab7d0611\" />"
//				, "		<meta name=\"dtb:depth\" content=\"2\" />"
//				, "		<meta name=\"dtb:totalPageCount\" content=\"0\" />"
//				, "		<meta name=\"dtb:maxPageNumber\" content=\"0\" />"
//				, "</head>"
//				, "<docTitle>"
//				, "	<text>mple .epub eBook</text>"
//				, "</docTitle>"
//				, "<navMap>"
//				, "	<navPoint id=\"navPoint-1\" playOrder=\"1\">"
//				, "		<navLabel>"
//				, "			<text>Sample Book</text>"
//				, "		</navLabel>"
//				, "		<content src=\"Text/title_page.xhtml\" />"
//				, "	</navPoint>"
//				, "	<navPoint id=\"navPoint-2\" playOrder=\"2\">"
//				, "		<navLabel>"
//				, "			<text>A Sample .epub Book</text>"
//				, "		</navLabel>"
//				, "		<content src=\"Text/title_page.xhtml#heading_id_3\" />"
//				, "		<navPoint id=\navPoint-3\" playOrder=\"3\">"
//				, "			<navLabel>"
//				, "				<text>Title Page</text>"
//				, "			</navLabel>"
//				, "			<content src=\"Text/title_page.xhtml#heading_id_4\" />"
//				, "		</navPoint>"
//				, "		<navPoint id=\"navPoint-4\" playOrder=\"4\">"
//				, "			<navLabel>"
//				, "				<text>Chapter 1</text>"
//				, "			</navLabel>"
//				, "			<content src=\"Text/chap01.xhtml\" />"
//				, "		</navPoint>"
//				, "		<navPoint id=\"navPoint-5\" playOrder=\"5\">"
//				, "			<navLabel>"
//				, "				<text>Chapter 2</text>"
//				, "			</navLabel>"
//				, "			<content src=\"Text/chap02.xhtml\" />"
//				, "		</navPoint>"
//				, "	</navPoint>"
//				, "</navMap>"
//				, "</ncx>" 
//
//		});
//
//		Path path = Paths.get(srcOebpsRoot + "toc.ncx");
//		try {
//			Files.write(path, lines, StandardCharsets.UTF_8);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
	}

	public void createTocFile() {
		// WTF: what is the diff?  http://stackoverflow.com/questions/16748030/difference-between-arrays-aslistarray-vs-new-arraylistintegerarrays-aslist
		// List<String> lines = Arrays.asList(new String[] { 
		// the one above wouldn't let me lines.add
		List<String> lines = new ArrayList<String>(Arrays.asList(new String[] { 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				, "<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\">"
				, "	<head>"
				, "		<meta name=\"dtb:uid\" content=\"015ffaec-9340-42f8-b163-a0c5ab7d0611\" />"
				, "</head>"
				, "<docTitle>"
				, "	<text>sample .epub eBook</text>"
				, "</docTitle>"
				, "<navMap>"

		}));

		Integer curLevel = 1;
		Integer playOrder = 0;
		Integer chapterCount = 0;
		Integer endIndex = 1;

		for(String s: chapterPathAndFilenames) {
			playOrder++;
			chapterCount++;
			
			if (s.indexOf(".html") != -1) {
				endIndex = s.indexOf(".html"); 
			} else if (s.indexOf(".html") != -1) {
				endIndex = s.indexOf(".html"); 
			} else {
				endIndex = s.length() + 1;
			}
		

			if ( (getLevel1Multiple() > 0) && (playOrder == 1) || ((chapterCount - 1) % getLevel1Multiple() == 0) ) {
				/* TODO WTF: validator giving errors about multiple navPoints with same target.  I found this to be
				 * because <content src= ... /> had same page. I tried using a # at the end of one, but validator complained because
				 * that anchor didn't exist in the doc. I don't know what to put in <content /> to just have it fold in lower nav
				 * without pointing to page itself.  Giving up now as reader seems to handle it ok and only validator is complaining
				 * NOTE TO SELF: ignore these errors in validator and get used to not seeing a clean report.
				*/
				if (curLevel > 1) {
					curLevel--;
					lines.add("	</navPoint>");
				}
				curLevel++;
				lines.add("	<navPoint id=\"navPoint-" + playOrder.toString() + "\" playOrder=\"" + playOrder.toString() + "\">");
				lines.add("		<navLabel>");
				lines.add("			<text>Chapter: " + chapterCount.toString() + "</text>");
				lines.add("		</navLabel>");
				lines.add("		<content src=\"Text/" + s + "\" />");
				playOrder++;
			}
			lines.add("	<navPoint id=\"navPoint-" + playOrder.toString() + "\" playOrder=\"" + playOrder.toString() + "\">");
			lines.add("		<navLabel>");
			lines.add("			<text>" + s.substring(0, endIndex) + "</text>");
			lines.add("		</navLabel>");
			lines.add("		<content src=\"Text/" + s + "\" />");
			lines.add("	</navPoint>");
		}

		if (getLevel1Multiple() > 0){
			while (curLevel > 1) {
				lines.add("	</navPoint>");
				curLevel--;
			}
		}

		lines.add("</navMap>");
		lines.add("</ncx>"); 

		Path path = Paths.get(srcPathOebpsRoot + "toc.ncx");
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void createHtmlTocFile() {
		// TODO build this page
	}
	
	public void createContentFile() {
		List<String> lines = new ArrayList<String>(Arrays.asList(new String[] { 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				, "<package xmlns=\"http://www.idpf.org/2007/opf\" unique-identifier=\"BookID\" version=\"2.0\">"
				, "    <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:opf=\"http://www.idpf.org/2007/opf\">"
				, "				<dc:title>" + this.bookTitle+ "</dc:title>"
				, "				<dc:language>en</dc:language>"
				, "		        <dc:rights>Public Domain</dc:rights>"
				, "				<dc:creator opf:role=\"aut\">" + this.bookAuthor + "</dc:creator>"
				, "		        <dc:publisher>mePub</dc:publisher>"
				, "		        <dc:identifier id=\"BookID\" opf:scheme=\"UUID\">015ffaec-9340-42f8-b163-a0c5ab7d0611</dc:identifier>"
				, "    </metadata>"
				// ***** <manifest>
				, "    <manifest>"
				, "        <item id=\"ncx\" href=\"toc.ncx\" media-type=\"application/x-dtbncx+xml\"/>"
//				, "        <item id=\"sample.png\" href=\"Images/sample.png\" media-type=\"image/png\"/>"
				, "        <item id=\"Book1.jpg\" href=\"Images/Book1.jpg\" media-type=\"image/jpeg\"/>"
//				, "        <item id=\"page-template.xpgt\" href=\"Styles/page-template.xpgt\" media-type=\"application/vnd.adobe-page-template+xml\"/>"
				, "        <item id=\"stylesheet.css\" href=\"Styles/style.css\" media-type=\"text/css\"/>"

		}));

		// Manifest entries for each chapter
		Integer index = 0;
		for(String s: chapterPathAndFilenames) {
			index++;
			lines.add("	        <item id=\"x" + String.format("%04d", index) + "\" href=\"Text/" + s + "\" media-type=\"application/xhtml+xml\"/>");
		}

//		lines.add("        <item id=\"title_page.xhtml\" href=\"Text/title_page.xhtml\" media-type=\"application/xhtml+xml\"/>");
		lines.add("        <item id=\"Cover.xhtml\" href=\"Text/Cover.xhtml\" media-type=\"application/xhtml+xml\"/>");
		lines.add("        <item id=\"htmlcoverpage.html\" href=\"coverpage.html\" media-type=\"application/xhtml+xml\"/>");
		lines.add("    </manifest>");
		// ***** </manifest>
		
		// ***** <spine>
		lines.add("    <spine toc=\"ncx\">");
//		lines.add("        <itemref idref=\"title_page.xhtml\"/>");
		lines.add("        <itemref idref=\"Cover.xhtml\"/>");
		lines.add("        <itemref idref=\"htmlcoverpage.html\"/>");

		// spine toc entries for each chapter
		index = 0;
		for(String s: chapterPathAndFilenames) {
			index++;
//			lines.add("        <itemref idref=\"" + s + "\"/>");
			lines.add("        <itemref idref=\"x" + String.format("%04d", index) + "\"/>");
		}

		lines.add("    </spine>");
		// ***** </spine>
		
		// ***** <guide>
		lines.add("    <guide>");
		lines.add("    <reference href=\"Text/Cover.xhtml\" title=\"Cover\" type=\"cover\" />");
		lines.add("    </guide>");
		  // ***** </guide>
		
		
		
		lines.add("</package>");

		Path path = Paths.get(srcPathOebpsRoot + "content.opf");
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	public void addContentFilenameToList(String chapterFilename) {
		this.chapterPathAndFilenames.add(chapterFilename);
	}
	
	public  void createIndividualContentFile(String bodyText, String chapterFilename) {
		
		if (bodyText == null) {
			bodyText = "\t\t<h1 class=\"sgc-2\" id=\"heading_id_2\">Default Chapter</h1>" + "\n" + "\t\t<p><br /></p>"
					+ "\n" + "\t\t<p class=\"sgc-3\">Body text goes here.</p>" + "\n" + "\t\t<p>&nbsp;</p>";
		}
		List<String> lines = Arrays.asList(new String[] { //!!!!! NOTE: display in file fixed by using charset=UTF-8 instead of charset=gbk. (don't know where I got that originally)
				"<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>"
				,"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">"
				, "	<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">"
				, "<head>"
				, "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
				, "\t<link href=\"../Styles/style.css\" rel=\"stylesheet\" type=\"text/css\" />"
				, "\n"
				, "\t<title></title>"
				, "\t\t<style type=\"text/css\">"
				, "\n"
				, "\t\t\t/*<![CDATA[*/"
				, "\n"
				, "\t\t\th1.sgc-2 {text-align: center;}"
				, "\t\t\tspan.sgc-1 {font-family: Arial; font-size: medium;}"
				, "\n"
				, "\t\t\tp.sgc-3 {font-weight: bold}"
				, "\t\t\t/*]]>*/"
				, "\t\t</style>"
				, "</head>"
				, "\n"
				, "<body>"
				, "<div>"
				, bodyText
				, "</div>"
				, "</body>"
				, "</html>"

		});

		Path path = Paths.get(srcPathOebpsText + chapterFilename);
		try {
			Files.write(path, lines, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		addContentFilenameToList(chapterFilename);
		
	}
	
	public void writeChapterSequenceFile() {
		Path path = Paths.get(srcPathRoot + "ChapSeq.txt");
		try (BufferedWriter writer = Files.newBufferedWriter(path)) 
		{
			for(String s: chapterPathAndFilenames) {
				writer.write(s + "\n");
			}
			// WTF: how do you do lambdas where function throws exception?
//			chapterPathAndFilenames.forEach(s -> writer.write(s + "\n"));
//		    writer.write("Hello World !!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public  void createEpubSrc_CoverPageFile(String bodyText, String chapterFilename) {
//		
//		if (bodyText == null) {
//			bodyText = "\t\t<h1 class=\"sgc-2\" id=\"heading_id_2\">Default Chapter</h1>" + "\n" + "\t\t<p><br /></p>"
//					+ "\n" + "\t\t<p class=\"sgc-3\">Body text goes here.</p>" + "\n" + "\t\t<p>&nbsp;</p>";
//		}
//		List<String> lines = Arrays.asList(new String[] { //!!!!! NOTE: display in file fixed by using charset=UTF-8 instead of charset=gbk. (don't know where I got that originally)
//				"<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>"
//				, "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">"
//				, ""
//				, "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
//				, "<head>"
//				, "  <link href=\"../Styles/style.css\" rel=\"stylesheet\" type=\"text/css\" />"
//				, ""
//				, "  <title>Cover</title>"
//				, "  <style type=\"text/css\">"
//				, "/*<![CDATA[*/"
//				, ""
//				, "  body.sgc-1 {word-wrap: break-word; -webkit-nbsp-mode: space; -webkit-line-break: after-white-space;}"
//				, ""
//				, "  span.sgc-2 {font-family: Arial; font-size: medium;}"
//				, ""
//				, "  p.sgc-4 {text-align: center;}"
//				, "  h1.sgc-3 {text-align: center;}"
//				, ""
//				, "  div.sgc-5 {font-weight: bold; text-align: center}"
//				, ""
//				, "  p.sgc-6 {font-weight: bold}"
//				, "  /*]]>*/"
//				, "  </style>"
//				, "</head>"
//				, ""
//				, "<body class=\"sgc-1\">"
//				, "  <p class=\"sgc-4\">&nbsp;</p>"
//				, ""
//				, "  <h1 class=\"sgc-3\" id=\"heading_id_2\" title=\"Cover\">" + this.getBookTitle() + "</h1>"
//				, ""
//				, "  <p class=\"sgc-4\">&nbsp;</p>"
//				, ""
//				, "  <h1 class=\"sgc-3 sigilNotInTOC\" id=\"heading_id_3\"><img align=\"middle\" alt=\"ePub\" src=\"../Images/" + this.srcDocImageFilename + "\" />" + "" + "</h1>"
//				, ""
//				, "  <p class=\"sgc-4\">&nbsp;</p>"
//				, ""
//				, "  <p class=\"sgc-4\">&nbsp;</p>"
//				, ""
//				, "  <p class=\"sgc-4 sgc-6\">By: " + this.getBookAuthor() + "</p>"
//				, "</body>"
//				, "</html>"
//
//		});
//
//		Path path = Paths.get(srcPathOebpsText + "Cover.xhtml");
//		try {
//			Files.write(path, lines, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
//	public  void createEpubSrc_HtmlCoverPageFile(String bodyText, String chapterFilename) {
	public  void createHtmlCoverPageFile() {
		
		List<String> lines = Arrays.asList(new String[] { //!!!!! NOTE: display in file fixed by using charset=UTF-8 instead of charset=gbk. (don't know where I got that originally)
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				, "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">"
				, "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">"
				, "<head>"
				, "<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml; charset=utf-8\" />"
				, "<title>My eBook</title>"  // TODO format dynamically
				, "<style type=\"text/css\">"
				, "  body {text-align: center; padding:0; margin: 0;}"
				, "  div {text-align: center; padding:0; margin: 0;}"
				, "  img {padding:0; margin: 0; height: 100%;}"
				, "</style>"
				, "</head>"
				, "<body>"
				, "<div><img src=\"cover.jpg\" alt=\"Cover for My eBook\" /></div>" // TODO check if this exists
//				, "<div><img src=\"Images" + File.separator + this.srcDocImageFilename + "\" alt=\"Cover for this book\" /></div>"
				, "</body>"
				, "</html>"

		});

		Path path = Paths.get(srcPathOebpsRoot + "coverpage.html");
		try {
			Files.write(path, lines, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void createEpubSrc_IndividualImageFile(File src) {
		File dest = new File(this.srcPathOebpsImages + src.getName());
		try {
		    FileUtils.copyFile(src, dest);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
//	public void prepEpubSrc_PlaceCoverImageFile(File src) {
//		File dest = new File(this.srcPathOebpsRoot + "cover.jpg");
//		try {
//		    FileUtils.copyFile(src, dest);
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}
//	}
	
	public void prepEpubSrcDirStructure() {
		
		try {
			delete(new File(getSrcRoot()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		doFolder(getSrcRoot());
		doFolder(srcPathMetaInf);
		doFolder(srcPathOebpsRoot);
		doFolder(srcPathOebpsImages);
		doFolder(srcPathOebpsStyles);
		doFolder(srcPathOebpsText);

	}
	private void delete(File f) throws IOException {
		  if (f.isDirectory()) {
		    for (File c : f.listFiles())
		      delete(c);
		  }
		  if (!f.delete())
		    throw new FileNotFoundException("Failed to delete file: " + f);
		}
	/*
	 * private methods
	 */
	private void doFolder(String theFolder) {
		Path path = Paths.get(theFolder);

		try {
			Path newDir = Files.createDirectory(path);
		} catch(FileAlreadyExistsException e){
			// the directory already exists.
		} catch (IOException e) {
			//something else went wrong
			e.printStackTrace();
		}

	}
	

}
