package bookplay2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EpubBuilder {

	private EpubSourceMaterial esm;
	private String destPath;
	private String destFilename;
	private static String srcPathRoot;
	
	/*
	 * constructors
	 */

	/*
	 * getters and setters
	 */
	public String getSrcPathRoot() {
		return srcPathRoot;
	}

	public void setSrcPathRoot(String srcPathRoot) {
		this.srcPathRoot = srcPathRoot;
	}

	public EpubSourceMaterial getEsm() {
		return esm;
	}

	public void setEsm(EpubSourceMaterial esm) {
		this.esm = esm;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}

	public void setDestFilename(String destFilename) {
		this.destFilename = destFilename;
	}

	/*
	 * public methods
	 */
	public static void buildEpub() {
		srcPathRoot = "epub build src/";
		try {
			FileOutputStream fos = new FileOutputStream("epub build dest/testBook01.epub");
			ZipOutputStream zos = new ZipOutputStream(fos);


			// Set compression level to STORED (uncompressed) for mimetype
			zos.setLevel(ZipOutputStream.STORED);
			addToZipFile(srcPathRoot, "mimetype", zos, false);

			// Set compression level to DEFLATED (compressed) for everything else
			zos.setLevel(ZipOutputStream.DEFLATED);
			addToZipFile(srcPathRoot, "META-INF/container.xml", zos);
			addToZipFile(srcPathRoot, "OEBPS/content.opf", zos);
			addToZipFile(srcPathRoot, "OEBPS/toc.ncx", zos);
			addToZipFile(srcPathRoot, "OEBPS/Styles/style.css", zos);

			
//			FileUtils.listFiles(directory, extensions, recursive)
//			Collection<File> files = FileUtils.listFiles(FileUtils.getFile(srcPathRoot + "OEBPS/"),
//			         FileFilterUtils.suffixFileFilter(".txt"), TrueFileFilter.INSTANCE);
			
			
			try(Stream<Path> paths = Files.walk(Paths.get(srcPathRoot + "OEBPS/Text/"))) {
			    paths.forEach(filePath -> {
			        if (Files.isRegularFile(filePath)) {
			        	String name = new String(filePath.subpath(1, filePath.getNameCount()).toString());
						try {
							addToZipFile(srcPathRoot, name, zos);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    });
			} 
			
//			addToZipFile(srcPathRoot, "OEBPS/Text/Chapter0001.html", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Text/Chapter0002.html", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Text/Chapter0003.html", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Text/Chapter0004.html", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Images/epub_logo_color.jpg", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Images/Tutori1.jpg", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Images/Tutori2.jpg", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Images/Tutori3.jpg", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Text/002.xhtml", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Text/003.xhtml", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Text/004.xhtml", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Text/005.xhtml", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Text/Contents.xhtml", zos);
//			addToZipFile(srcPathRoot, "OEBPS/Text/Cover.xhtml", zos);

			
			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * private methods
	 */
	private static void addToZipFile(String sourceFolder, String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {
		addToZipFile(sourceFolder, fileName, zos, true);
	}
	

	@SuppressWarnings("static-access")
	private static void addToZipFile(String sourceFolder, String fileName, ZipOutputStream zos, boolean compress) throws FileNotFoundException, IOException {

		System.out.println("Writing '" + fileName + "' to zip file");

		File file = new File(sourceFolder + fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		if (compress) {
			zipEntry.setMethod(ZipEntry.DEFLATED);
		} else {
	           int bytesRead;
	            byte[] buffer = new byte[1024];
	            CRC32 crc = new CRC32();
	            try (
	                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
	             ) {
	                crc.reset();
	                while ((bytesRead = bis.read(buffer)) != -1) {
	                    crc.update(buffer, 0, bytesRead);
	                }
	            }
			zipEntry.setMethod(zipEntry.STORED);
			zipEntry.setCompressedSize(file.length());
            zipEntry.setSize(file.length());
            zipEntry.setCrc(crc.getValue());	
		}
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}


}
