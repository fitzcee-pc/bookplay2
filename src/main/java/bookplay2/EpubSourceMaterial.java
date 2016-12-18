package bookplay2;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

	private String srcRoot;
	private static String srcMetaInf;
	private static String srcOebpsRoot;
	private static String srcOebpsImages;
	private static String srcOebpsStyles;
	private static String srcOebpsText;
	private List<String> chapterPathAndFileNames = new ArrayList<String>();

	/*
	 * constructors
	 */
	public EpubSourceMaterial(String theSrcRoot) {
		setSrcRoot(theSrcRoot);
	}

	/*
	 * getters and setters
	 */
	public String getSrcRoot() {
		return srcRoot;
	}

	public void setSrcRoot(String theSrcRoot) {
		srcRoot = theSrcRoot;
		srcMetaInf = srcRoot + "META-INF" + File.separator;
		srcOebpsRoot = srcRoot + "OEBPS" + File.separator;
		srcOebpsImages = srcOebpsRoot + "Images" + File.separator;
		srcOebpsStyles = srcOebpsRoot + "Styles" + File.separator;
		srcOebpsText = srcOebpsRoot + "Text" + File.separator;
	}


	/*
	 * public methods
	 */
	public void writeEpubSrc_MimetypeFile() {
		List<String> lines = Arrays.asList(new String[] { 
				"application/epub+zip"
		});

		Path path = Paths.get(srcRoot + "mimetype");
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
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

		Path path = Paths.get(srcMetaInf + "container.xml");
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

		Path path = Paths.get(srcOebpsStyles + "style.css");
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
		List<String> lines = Arrays.asList(new String[] { 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				, "<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\">"
				, "	<head>"
				, "		<meta name=\"dtb:uid\" content=\"015ffaec-9340-42f8-b163-a0c5ab7d0611\" />"
				, "</head>"
				, "<docTitle>"
				, "	<text>sample .epub eBook</text>"
				, "</docTitle>"
				, "<navMap>"
				, "	<navPoint id=\"navPoint-1\" playOrder=\"1\">"
				, "		<navLabel>"
				, "			<text>Sample Book</text>"
				, "		</navLabel>"
				, "		<content src=\"Text/title_page.xhtml\" />"
				, "	</navPoint>"
				, "	<navPoint id=\"navPoint-2\" playOrder=\"2\">"
				, "		<navLabel>"
				, "			<text>A Sample .epub Book</text>"
				, "		</navLabel>"
				, "		<content src=\"Text/title_page.xhtml#heading_id_3\" />"
				, "		<navPoint id=\navPoint-3\" playOrder=\"3\">"
				, "			<navLabel>"
				, "				<text>Title Page</text>"
				, "			</navLabel>"
				, "			<content src=\"Text/title_page.xhtml#heading_id_4\" />"
				, "		</navPoint>"
				, "		<navPoint id=\"navPoint-4\" playOrder=\"4\">"
				, "			<navLabel>"
				, "				<text>Chapter 1</text>"
				, "			</navLabel>"
				, "			<content src=\"Text/chap01.xhtml\" />"
				, "		</navPoint>"
				, "		<navPoint id=\"navPoint-5\" playOrder=\"5\">"
				, "			<navLabel>"
				, "				<text>Chapter 2</text>"
				, "			</navLabel>"
				, "			<content src=\"Text/chap02.xhtml\" />"
				, "		</navPoint>"
				, "	</navPoint>"
				, "</navMap>"
				, "</ncx>" 

		});

		Path path = Paths.get(srcOebpsRoot + "toc.ncx");
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public  void writeChapterFile(String bodyText, String chapterFileName) {

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
				, bodyText
				, "</body>"
				, "</html>"

		});

		Path path = Paths.get(srcOebpsText + chapterFileName);
		try {
			Files.write(path, lines, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public void setupEpubBuildSrcDirStructure() {
		doFolder(getSrcRoot());
		doFolder(srcMetaInf);
		doFolder(srcOebpsRoot);
		doFolder(srcOebpsImages);
		doFolder(srcOebpsStyles);
		doFolder(srcOebpsText);

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
