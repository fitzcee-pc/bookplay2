package bookplay2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jsoup.Jsoup;


public class Test {

	public static Test test = new Test();
	private static Properties props;
	private static String runDateTime;
	private static MyBook myBook;
	private static String outTextPathAndFile;
	private static MyBookPage myBookPage;
	private static String epubBuildRoot;
	private static String basePath;
	private static String startingDoc;
	private static String epubBuildSrcRoot;
	private static String epubBuildSrcMetaInf;
	private static String epubBuildSrcOebpsRoot;
	private static String epubBuildSrcOebpsImages;
	private static String epubBuildSrcOebpsStyles;
	private static String epubBuildSrcOebpsText;

	public static void main(String[] args) throws IOException {

//		Double x = 1.23456;
//		System.out.println(x.toString());
//		System.out.println(String.format("%1.1f", x));
//		Integer y = 1;
//		System.out.println(String.format("%03d", y));
//		
		
		
		initProps();
		
		System.out.println(runDateTime);

//		doBookStuff();
		try {
			myBookPage = new MyBookPage(Jsoup.parse(new File(basePath + startingDoc),null),"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		myBook = new MyBook(null, myBookPage, null);

		setupEpubBuildSrcDirStructure();
		
		writeEpubSrc_MimetypeFile(epubBuildSrcRoot + "mimetype");
		writeEpubSrc_ContainerFile(epubBuildSrcMetaInf + "container.xml");
		writeEpubSrc_CssFile(epubBuildSrcOebpsStyles + "style.css");
		
//		writeChapterFile(epubBuildSrcOebpsText + "testChap1.html",myBook.firstPage.getBodyText());
//		myBook.appendToToc("testChap1.html");
//		writeChapterFile(epubBuildSrcOebpsText + "testChap2.html",myBook.firstPage.getNextPage().getBodyText());
//		myBook.appendToToc("testChap2.html");
//		writeChapterFile(epubBuildSrcOebpsText + "testChapLast.html",myBook.lastPage.getBodyText());
//		myBook.appendToToc("testChapLast.html");

		System.out.println(getBookDeets());

		writeDeetsToOutfile(getBookDeets());


//		doBookStuff();

//		buildEpub();
		
//		doZFSStuff();
		
//		doZipStuff();
//
		System.out.println("-----Run Complete-----");

	}

	private static void doZFSStuff() {
		// https://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html
		// determined this can't have an uncompressed file, so won't work for epub
		// http://stackoverflow.com/questions/28239621/java-nio-zip-filesystem-equivalent-of-setmethod-in-java-util-zip-zipentry
//		Map<String, String> env = new HashMap<>(); 
//		env.put("create", "true");
//		// locate file system by using the syntax 
//		// defined in java.net.JarURLConnection
//		URI uri = URI.create("jar:file:/codeSamples/zipfs/zipfstest.zip");
//
//		try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
//			Path externalTxtFile = Paths.get("/codeSamples/zipfs/SomeTextFile.txt");
//			Path pathInZipfile = zipfs.getPath("/SomeTextFile.txt");          
//			// copy a file into the zip file
//			Files.copy( externalTxtFile,pathInZipfile, 
//					StandardCopyOption.REPLACE_EXISTING ); 
//		} 
	}

	private static void initProps() {
		props = getProps();

		basePath = new String(props.getProperty("basePath"));
		startingDoc = new String(props.getProperty("startingDoc"));
		outTextPathAndFile = new String(props.getProperty("outTextPathAndFile"));
		epubBuildRoot = new String(props.getProperty("epubBuildSrcRoot"));
		epubBuildSrcRoot = new String(props.getProperty("epubBuildSrcRoot"));
		epubBuildSrcMetaInf = new String(props.getProperty("epubBuildSrcMetaInf"));
		epubBuildSrcOebpsRoot = new String(props.getProperty("epubBuildSrcOebpsRoot"));
		epubBuildSrcOebpsImages = new String(props.getProperty("epubBuildSrcOebpsImages"));
		epubBuildSrcOebpsStyles = new String(props.getProperty("epubBuildSrcOebpsStyles"));
		epubBuildSrcOebpsText = new String(props.getProperty("epubBuildSrcOebpsText"));
		
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date today = Calendar.getInstance().getTime();        
		runDateTime = df.format(today);

		
	}
	
	private static void doBookStuff() {
		try {
			myBookPage = new MyBookPage(Jsoup.parse(new File(basePath + startingDoc),null),"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

		myBook = new MyBook(null, myBookPage, null);

		System.out.println(runDateTime);

		System.out.println(getBookDeets());

		writeDeetsToOutfile(getBookDeets());

	}

	private static void doZipStuff(){
		zipFiles1();
		zipFiles2();
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

		bookDeets = "\n" + myBook.toString();
		bookDeets += "\n-----body text-----\n" + myBook.firstPage.getBodyText();
		bookDeets += "\n";


		return bookDeets;
	}	

	private static void writeEpubSrc_MimetypeFile(String outPathAndFile) {
		List<String> lines = Arrays.asList(new String[] { 
				"application/epub+zip"
		});

		Path path = Paths.get(outPathAndFile);
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeEpubSrc_ContainerFile(String outPathAndFile) {
		List<String> lines = Arrays.asList(new String[] { 
				  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				, "<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">"
				, "   <rootfiles>"
				, "       <rootfile full-path=\"OEBPS/content.opf\" media-type=\"application/oebps-package+xml\"/>"
				, "   </rootfiles>"
				, "</container>"

		});

		Path path = Paths.get(outPathAndFile);
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeEpubSrc_CssFile(String outPathAndFile) {
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

		Path path = Paths.get(outPathAndFile);
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private static void writeEpubSrc_tocFile_overlyComplicated(String outPathAndFile) {
		// according to one source, much of this is not needed.
		// https://gist.github.com/elmimmo/d7b8dbebc4e972734e9a
		List<String> lines = Arrays.asList(new String[] { 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				, "<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\">"
				, " "
				, "<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\">"
				, "	<head>"
				, "		<meta name=\"dtb:uid\" content=\"015ffaec-9340-42f8-b163-a0c5ab7d0611\" />"
				, "		<meta name=\"dtb:depth\" content=\"2\" />"
				, "		<meta name=\"dtb:totalPageCount\" content=\"0\" />"
				, "		<meta name=\"dtb:maxPageNumber\" content=\"0\" />"
				, "</head>"
				, "<docTitle>"
				, "	<text>mple .epub eBook</text>"
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

		Path path = Paths.get(outPathAndFile);
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeEpubSrc_tocFile(String outPathAndFile) {
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

		Path path = Paths.get(outPathAndFile);
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

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

	private static void zipFiles1() {
		try
		{
			ZipOutputStream out = new ZipOutputStream(new 
					BufferedOutputStream(new FileOutputStream("out.zip")));
			out.setLevel(ZipOutputStream.STORED);
			byte[] data = new byte[1000]; 
			BufferedInputStream in = new BufferedInputStream
					(new FileInputStream("out.txt"));
			int count;
			out.putNextEntry(new ZipEntry("out.txt"));
			while((count = in.read(data,0,1000)) != -1)
			{  
				out.write(data, 0, count);
			}
			in.close();
			out.flush();
			out.close();
			System.out.println("Your file is zipped");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}  

	}

	private static void zipFiles2() {
		try {
			FileOutputStream fos = new FileOutputStream("somecompressed-test.zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			// This sets the compression level to STORED, ie, uncompressed
			zos.setLevel(ZipOutputStream.STORED);

			String file1Name = "out.txt";
			String file2Name = "file2.txt";
			String file3Name = "folder/file3.txt";
			String file4Name = "folder/file4.txt";
			String file5Name = "f1/f2/f3/file5.txt";

//			addToZipFile(file1Name, zos);
//			zos.setLevel(ZipOutputStream.DEFLATED);
//			addToZipFile(file2Name, zos);
//			addToZipFile(file3Name, zos);
//			addToZipFile(file4Name, zos);
//			addToZipFile(file5Name, zos);

			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void setupEpubBuildSrcDirStructure() {
		doFolder(epubBuildSrcRoot);
		doFolder(epubBuildSrcMetaInf);
		doFolder(epubBuildSrcOebpsRoot);
		doFolder(epubBuildSrcOebpsImages);
		doFolder(epubBuildSrcOebpsStyles);
		doFolder(epubBuildSrcOebpsText);

	}

	private static void doFolder(String theFolder) {
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
	
	private static void buildEpub() {
		try {
			FileOutputStream fos = new FileOutputStream("epub build dest/testBook01.epub");
			ZipOutputStream zos = new ZipOutputStream(fos);


			// Set compression level to STORED (uncompressed) for mimetype
			zos.setLevel(ZipOutputStream.STORED);
			addToZipFile(epubBuildRoot, "mimetype", zos);

			// Set compression level to DEFLATED (compressed) for everything else
			zos.setLevel(ZipOutputStream.DEFLATED);
			addToZipFile(epubBuildRoot, "META-INF/container.xml", zos);
			addToZipFile(epubBuildRoot, "OEBPS/content.opf", zos);
			addToZipFile(epubBuildRoot, "OEBPS/toc.ncx", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Images/epub_logo_color.jpg", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Images/Tutori1.jpg", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Images/Tutori2.jpg", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Images/Tutori3.jpg", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Styles/style.css", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Text/002.xhtml", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Text/003.xhtml", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Text/004.xhtml", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Text/005.xhtml", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Text/Contents.xhtml", zos);
			addToZipFile(epubBuildRoot, "OEBPS/Text/Cover.xhtml", zos);

			
			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void addToZipFile(String sourceFolder, String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

		System.out.println("Writing '" + fileName + "' to zip file");

		File file = new File(sourceFolder + fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

	private static void writeEpubSrc_ChapterFile(String outPathAndFile, String bodyText) {

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

		Path path = Paths.get(outPathAndFile);
		try {
			Files.write(path, lines, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
}

