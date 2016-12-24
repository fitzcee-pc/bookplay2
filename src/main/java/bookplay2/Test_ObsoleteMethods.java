package bookplay2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jsoup.Jsoup;

public class Test_ObsoleteMethods {

//	private static void doZFSStuff() {
//		// https://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html
//		// determined this can't have an uncompressed file, so won't work for epub
//		// http://stackoverflow.com/questions/28239621/java-nio-zip-filesystem-equivalent-of-setmethod-in-java-util-zip-zipentry
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
//	}

//	private static void doBookStuff() {
//		try {
//			myBookPage = new MyBookPage(Jsoup.parse(new File(epubSrcDocPath),null),"");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
//
//		myBook = new MyBook(null, myBookPage, null);
//
//		System.out.println(runDateTime);
//
//		System.out.println(getBookDeets());
//
//		writeDeetsToOutfile(getBookDeets());
//
//	}
//
//	private static void doZipStuff(){
//		zipFiles1();
//		zipFiles2();
//	}
//

//	private static String getBookDeets() {
//		String bookDeets;
//
//		bookDeets = "\n" + myBook.toString();
//		bookDeets += "\n-----body text-----\n" + myBook.firstPage.getBodyText();
//		bookDeets += "\n";
//
//
//		return bookDeets;
//	}	
//	
//	private static void writeDeetsToOutfile(String theDeets) {
//		List<String> lines = Arrays.asList(new String[] { //!!!!! NOTE: display in file fixed by using charset=UTF-8 instead of charset=gbk. (don't know where I got that originally)
//				"<html>"
//				,"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head>"
//				, "<body>"
//				, runDateTime
//				, theDeets
//				, "</body>"
//				, "</html>" 
//		});
//
//		Path path = Paths.get(outTextPathAndFile);
//		try {
//			Files.write(path, lines, StandardCharsets.UTF_8);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	private static void zipFiles1() {
//		try
//		{
//			ZipOutputStream out = new ZipOutputStream(new 
//					BufferedOutputStream(new FileOutputStream("out.zip")));
//			out.setLevel(ZipOutputStream.STORED);
//			byte[] data = new byte[1000]; 
//			BufferedInputStream in = new BufferedInputStream
//					(new FileInputStream("out.txt"));
//			int count;
//			out.putNextEntry(new ZipEntry("out.txt"));
//			while((count = in.read(data,0,1000)) != -1)
//			{  
//				out.write(data, 0, count);
//			}
//			in.close();
//			out.flush();
//			out.close();
//			System.out.println("Your file is zipped");
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}  
//
//	}
//
//	private static void zipFiles2() {
//		try {
//			FileOutputStream fos = new FileOutputStream("somecompressed-test.zip");
//			ZipOutputStream zos = new ZipOutputStream(fos);
//
//			// This sets the compression level to STORED, ie, uncompressed
//			zos.setLevel(ZipOutputStream.STORED);
//
//			String file1Name = "out.txt";
//			String file2Name = "file2.txt";
//			String file3Name = "folder/file3.txt";
//			String file4Name = "folder/file4.txt";
//			String file5Name = "f1/f2/f3/file5.txt";
//
////			addToZipFile(file1Name, zos);
////			zos.setLevel(ZipOutputStream.DEFLATED);
////			addToZipFile(file2Name, zos);
////			addToZipFile(file4Name, zos);
////			addToZipFile(file5Name, zos);
////			addToZipFile(file3Name, zos);
//
//			zos.close();
//			fos.close();
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

	
}
