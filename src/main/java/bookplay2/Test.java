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
	private static String inSrcDocPathAndFilename;
//	private static String inSrcDocImagePath;
	private static String outEpubSrcMaterialRootPath;
	private static String outEpubBuildDestPath;
	private static Integer outEpubSrcMaterialLevel1Multiple;
	private static String outEpubSrcMaterialBookTitle;

	/* 
	 * just build
	 */
	public static void mainx(String[] args) throws IOException {
		System.out.println("-----Build Only-----");

		initProps();

		EpubSourceMaterial esm = new EpubSourceMaterial(outEpubSrcMaterialRootPath, outEpubSrcMaterialLevel1Multiple);

		EpubBuilder eb = new EpubBuilder();
		eb.setEsm(esm);
		eb.setEpubBuildDestPath(outEpubBuildDestPath);
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

		EpubSourceMaterial esm = new EpubSourceMaterial(outEpubSrcMaterialRootPath);
		esm.prepEpubSrcDirStructure();
		esm.createMimetypeFile();
		esm.createContainerFile();
		esm.createCssFile();

		MyBookPage chineseBookPage = null;
		try {
			chineseBookPage = new MyBookPage(Jsoup.parse(new File(inSrcDocPathAndFilename),null),"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		esm.setBookTitle(outEpubSrcMaterialBookTitle);
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

		Integer endIndex = Paths.get(inSrcDocPathAndFilename).getNameCount() - 1;
		String inSrcDocPath = Paths.get(inSrcDocPathAndFilename).subpath(0, endIndex).toString();
		esm.copyAllImagesToEpubSrc(Paths.get(File.separator + inSrcDocPath + File.separator));
//		esm.setInSrcDocImagePath(inSrcDocImagePath);
//		esm.prepEpubSrc_CopyImageFile(new File(inSrcDocImagePath) );
//		esm.prepEpubSrc_PlaceCoverImageFile(new File(inSrcDocImagePath) );
//		esm.createEpubSrc_CoverPageFile("xxx", "yyy");
		esm.createHtmlCoverPageFile(false, esm.getBookTitle());
		esm.setLevel1Multiple(outEpubSrcMaterialLevel1Multiple);
		esm.createTocFile();
		esm.createHtmlTocFile();
		esm.createContentFile();
//		Path inSrcImageRootPath = Paths.get(inSrcDocPathAndFilename);
//		inSrcImageRootPath = inSrcImageRootPath.subpath(0, inSrcImageRootPath.getNameCount() - 1);
		
		EpubBuilder eb = new EpubBuilder();
		eb.setEsm(esm);
		eb.setEpubBuildDestPath(outEpubBuildDestPath);
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

		inSrcDocPathAndFilename = new String(props.getProperty("inSrcDocPathAndFilename"));
//		inSrcDocImagePath = new String(props.getProperty("inSrcDocImagePath"));
		outEpubSrcMaterialLevel1Multiple = new Integer(props.getProperty("outEpubSrcMaterialLevel1Multiple"));
//		outTextPathAndFile = new String(props.getProperty("outTextPathAndFile"));
		outEpubSrcMaterialRootPath = new String(props.getProperty("outEpubSrcMaterialRootPath"));
		outEpubBuildDestPath = new String(props.getProperty("epubBuildDestPath"));
		outEpubSrcMaterialBookTitle = new String(props.getProperty("outEpubSrcMaterialBookTitle"));
		
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

