package bookplay2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
	private static String epubSrcDocPath;
	private static String epubSrcMaterialRootPath;
	private static String epubBuildDestPath;
	private static Integer epubSrcMaterialLevel1Multiple;
	
	public static void main(String[] args) throws IOException {

		System.out.println("-----Begin Run-----");

		Date now = Calendar.getInstance().getTime();        
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		runDateTime = df.format(now);

		System.out.println(runDateTime);

		initProps();
		
////		doBookStuff();

		EpubSourceMaterial esm = new EpubSourceMaterial(epubSrcMaterialRootPath, epubSrcMaterialLevel1Multiple);
//		EpubSourceMaterial esm = new EpubSourceMaterial(epubSrcMaterialRootPath, 2);
		esm.setupEpubBuildSrcDirStructure();
		esm.writeEpubSrc_MimetypeFile();
		esm.writeContainerFile();
		esm.writeCssFile();

		//		MyBookPage page = myBook.firstPage;
		MyBookPage page = null;
		try {
			page = new MyBookPage(Jsoup.parse(new File(epubSrcDocPath),null),"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		String chapFilename;

		Integer i = 0;
		do {
			i++;
			if (page.chapterName.isEmpty()) {
				chapFilename = new String("" + String.format("%04d", i) + ".html");
			} else {
				chapFilename = new String("" + String.format("%04d", i) + " " + page.chapterName + ".html");
			}
			String chapFilenameXX = chapFilename.replaceAll("：", "-");  // NOTE: this is not a (regular) colon.  i had to copy-paste from the chinese title to get the char
			chapFilename = chapFilenameXX;
			esm.writeChapterFile(page.getBodyText(), chapFilename);
			System.out.println("Add to Src: " + chapFilename);
			esm.addChapterFile(chapFilename);
		} while ((page = page.getNextPage()) != null);

		esm.writeTocFile();
		esm.writeContentFile();
		
		EpubBuilder eb = new EpubBuilder();
		eb.setEsm(esm);
		eb.setEpubBuildDestPath(epubBuildDestPath);
		eb.buildEpub();
		
//		esm.writeChapterSequenceFile();


//		System.out.println(getBookDeets());
//
//		writeDeetsToOutfile(getBookDeets());


//		doBookStuff();

//		buildEpub();
		
//		doZFSStuff();
		
//		doZipStuff();
//
		now = Calendar.getInstance().getTime();        
		runDateTime = df.format(now);

		System.out.println(runDateTime);
		System.out.println("-----Run Complete-----");

	}

	private static void initProps() {
		props = getProps();

		epubSrcDocPath = new String(props.getProperty("epubSrcDocPath"));
		epubSrcMaterialLevel1Multiple = new Integer(props.getProperty("epubSrcMaterialLevel1Multiple"));
//		outTextPathAndFile = new String(props.getProperty("outTextPathAndFile"));
		epubSrcMaterialRootPath = new String(props.getProperty("epubSrcMaterialRootPath"));
		epubBuildDestPath = new String(props.getProperty("epubBuildDestPath"));
		
	}
	
	private static void doBookStuff() {
		try {
			myBookPage = new MyBookPage(Jsoup.parse(new File(epubSrcDocPath),null),"");
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
//			addToZipFile(file4Name, zos);
//			addToZipFile(file5Name, zos);
//			addToZipFile(file3Name, zos);

			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

