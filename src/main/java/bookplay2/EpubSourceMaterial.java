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

public class EpubSourceMaterial {

	/*
	 * EPUB folder structure:
	 * mimetype
	 * META-INF/container.xml
	 * OEBPS/content.opf
	 * OEBPS/toc.ncx
	 * OEBPS/Images/epub_logo_color.jpg
	 * OEBPS/Images/img1.jpg
	 * OEBPS/Images/img2.jpg
	 * OEBPS/Styles/style.css
	 * OEBPS/Text/chap002.xhtml
	 * OEBPS/Text/chap003.xhtml
	 * OEBPS/Text/chap004.xhtml
	 * OEBPS/Text/Contents.xhtml
	 * OEBPS/Text/Cover.xhtml
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

	/*
	 * constructors
	 */
	
	public EpubSourceMaterial(String theSrcRoot) {
		setSrcRoot(theSrcRoot);
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


	/*
	 * public methods
	 */
	public void writeEpubSrc_MimetypeFile() {
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

	public void writeContainerFile() {
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
	
	public void writeCssFile() {
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

	public void writeEpubSrc_tocFile_overlyComplicated() {
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

	public void writeTocFile() {
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

		Integer i = 0;
		Integer endIndex = 1;
		for(String s: chapterPathAndFilenames) {
			i++;
			if (s.indexOf(".html") != -1) {
				endIndex = s.indexOf(".html"); 
			} else if (s.indexOf(".html") != -1) {
				endIndex = s.indexOf(".html"); 
			} else {
				endIndex = s.length() + 1;
			}
		
			lines.add("	<navPoint id=\"navPoint-" + i.toString() + "\" playOrder=\"" + i.toString() + "\">");
			lines.add("		<navLabel>");
			lines.add("			<text>" + s.substring(0, endIndex) + "</text>");
			lines.add("		</navLabel>");
			lines.add("		<content src=\"Text/" + s + "\" />");
			lines.add("	</navPoint>");
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
	
	public void writeContentFile() {
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
				, "	    </metadata>"
				, "	    <manifest>"
				, "	        <item id=\"ncx\" href=\"toc.ncx\" media-type=\"application/x-dtbncx+xml\"/>"
//				, "	        <item id=\"sample.png\" href=\"Images/sample.png\" media-type=\"image/png\"/>"
//				, "	        <item id=\"page-template.xpgt\" href=\"Styles/page-template.xpgt\" media-type=\"application/vnd.adobe-page-template+xml\"/>"
				, "	        <item id=\"stylesheet.css\" href=\"Styles/style.css\" media-type=\"text/css\"/>"

		}));

		for(String s: chapterPathAndFilenames) {
			lines.add("	        <item id=\"" + s + "\" href=\"Text/" + s + "\" media-type=\"application/xhtml+xml\"/>");
		}

//		lines.add("        <item id=\"title_page.xhtml\" href=\"Text/title_page.xhtml\" media-type=\"application/xhtml+xml\"/>");
		lines.add("    </manifest>");
		lines.add("    <spine toc=\"ncx\">");
//		lines.add("        <itemref idref=\"title_page.xhtml\"/>");

		for(String s: chapterPathAndFilenames) {
			lines.add("        <itemref idref=\"" + s + "\"/>");
		}

		lines.add("    </spine>");
		lines.add("</package>");

		Path path = Paths.get(srcPathOebpsRoot + "content.opf");
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	public void addChapterFile(String chapterFilename) {
		this.chapterPathAndFilenames.add(chapterFilename);
	}
	
	public  void writeChapterFile(String bodyText, String chapterFilename) {

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
	
	public void setupEpubBuildSrcDirStructure() {
		
		try {
			delete(new File(getSrcRoot()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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