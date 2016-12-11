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
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
	private static Book book;
	private static String outTextPathAndFile;
	private static BookPage bookPage;

	public static void main(String[] args) throws IOException {

		doBookStuff();
		
//		outputChapter("C:\\Data\\_A-F\\Dev\\git\\bookplay2\\epub build dest\\testChap1.html","");
		outputChapter("epub build dest\\testChap1.html",book.firstPage.getBodyText());
		
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
	
	private static void doBookStuff() {
		props = getProps();

		String basePath = new String(props.getProperty("basePath"));
		String startingDoc = new String(props.getProperty("startingDoc"));
		outTextPathAndFile = new String(props.getProperty("outTextPathAndFile"));

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date today = Calendar.getInstance().getTime();        
		runDateTime = df.format(today);

		try {
			bookPage = new BookPage(Jsoup.parse(new File(basePath + startingDoc),null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

		book = new Book(null, bookPage, null);

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

	public static void zipFiles1() {
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

	public static void zipFiles2() {
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

	public static void buildEpub() {
		try {
			FileOutputStream fos = new FileOutputStream("epub build dest/testBook01.epub");
			ZipOutputStream zos = new ZipOutputStream(fos);


			// Set compression level to STORED (uncompressed) for mimetype
			zos.setLevel(ZipOutputStream.STORED);
			addToZipFile("epub build source/", "mimetype", zos);

			// Set compression level to DEFLATED (compressed) for everything else
			zos.setLevel(ZipOutputStream.DEFLATED);
			addToZipFile("epub build source/", "META-INF/container.xml", zos);
			addToZipFile("epub build source/", "OEBPS/content.opf", zos);
			addToZipFile("epub build source/", "OEBPS/toc.ncx", zos);
			addToZipFile("epub build source/", "OEBPS/Images/epub_logo_color.jpg", zos);
			addToZipFile("epub build source/", "OEBPS/Images/Tutori1.jpg", zos);
			addToZipFile("epub build source/", "OEBPS/Images/Tutori2.jpg", zos);
			addToZipFile("epub build source/", "OEBPS/Images/Tutori3.jpg", zos);
			addToZipFile("epub build source/", "OEBPS/Styles/style.css", zos);
			addToZipFile("epub build source/", "OEBPS/Text/002.xhtml", zos);
			addToZipFile("epub build source/", "OEBPS/Text/003.xhtml", zos);
			addToZipFile("epub build source/", "OEBPS/Text/004.xhtml", zos);
			addToZipFile("epub build source/", "OEBPS/Text/005.xhtml", zos);
			addToZipFile("epub build source/", "OEBPS/Text/Contents.xhtml", zos);
			addToZipFile("epub build source/", "OEBPS/Text/Cover.xhtml", zos);

			
			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void addToZipFile(String sourceFolder, String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

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

	public static void outputChapter(String outPathAndFile, String bodyText) {

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
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
}

