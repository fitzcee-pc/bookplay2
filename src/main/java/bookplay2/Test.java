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
//	private static String epubSrcDocImagePath;
	private static String epubSrcMaterialRootPath;
	private static String epubBuildDestPath;
	private static Integer epubSrcMaterialLevel1Multiple;
	private static String epubSrcMaterialBookTitle;

	/* 
	 * just build
	 */
	public static void mainx(String[] args) throws IOException {
		System.out.println("-----Build Only-----");

		initProps();

		EpubSourceMaterial esm = new EpubSourceMaterial(epubSrcMaterialRootPath, epubSrcMaterialLevel1Multiple);

		EpubBuilder eb = new EpubBuilder();
		eb.setEsm(esm);
		eb.setEpubBuildDestPath(epubBuildDestPath);
		eb.buildEpubByWalkingSrc();

		System.out.println("-----Run Complete-----");

	}
	
	/*
	 * assemble src material, then build
	*/
	 public static void main(String[] args) throws IOException {

		System.out.println("-----Begin Run-----");

		Date now = Calendar.getInstance().getTime();        
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		runDateTime = df.format(now);

		System.out.println(runDateTime);

		initProps();
		
////		doBookStuff();

		EpubSourceMaterial esm = new EpubSourceMaterial(epubSrcMaterialRootPath);
		esm.prepEpubSrcDirStructure();
		esm.createMimetypeFile();
		esm.createContainerFile();
		esm.createCssFile();

		MyBookPage chineseBookPage = null;
		try {
			chineseBookPage = new MyBookPage(Jsoup.parse(new File(epubSrcDocPath),null),"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		esm.setBookTitle(epubSrcMaterialBookTitle);
//		esm.setBookTitle(chineseBookPage.getBookTitle());  // TODO this instead of hardcode above
		
		String pageFilename;

		Integer i = 0;
		do {
			i++;
//			if (chineseBookPage.pageName.isEmpty()) {
				pageFilename = new String("ch" + String.format("%04d", i) + ".html");
//			} else {
//				pageFilename = new String("ch" + String.format("%04d", i) + " " + page.chapterName + ".html");
//			}
//			String pageFilenameFixed = pageFilename.replaceAll("：", "-");  // NOTE: this is not a (regular) colon.  i had to copy-paste from the chinese title to get the char
//			pageFilename = pageFilenameFixed;
			esm.createIndividualContentFile(chineseBookPage.getBodyText(), pageFilename);
			System.out.println("Add to Src: " + pageFilename);
//			esm.addContentFilenameToList(pageFilename);  // TODO now auto fill the list in esm. 
		} while ((chineseBookPage = chineseBookPage.getNextPage()) != null);

//		esm.setSrcDocImagePath(epubSrcDocImagePath);
//		esm.prepEpubSrc_CopyImageFile(new File(epubSrcDocImagePath) );
//		esm.prepEpubSrc_PlaceCoverImageFile(new File(epubSrcDocImagePath) );
//		esm.createEpubSrc_CoverPageFile("xxx", "yyy");
		esm.createHtmlCoverPageFile();
		esm.setLevel1Multiple(epubSrcMaterialLevel1Multiple);
		esm.createTocFile();
		esm.createHtmlTocFile();
		esm.createContentFile();
		
		EpubBuilder eb = new EpubBuilder();
		eb.setEsm(esm);
		eb.setEpubBuildDestPath(epubBuildDestPath);
//		eb.buildEpub();
		eb.buildEpubByWalkingSrc();
		
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
//		epubSrcDocImagePath = new String(props.getProperty("epubSrcDocImagePath"));
		epubSrcMaterialLevel1Multiple = new Integer(props.getProperty("epubSrcMaterialLevel1Multiple"));
//		outTextPathAndFile = new String(props.getProperty("outTextPathAndFile"));
		epubSrcMaterialRootPath = new String(props.getProperty("epubSrcMaterialRootPath"));
		epubBuildDestPath = new String(props.getProperty("epubBuildDestPath"));
		epubSrcMaterialBookTitle = new String(props.getProperty("epubSrcMaterialBookTitle"));
		
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


}

