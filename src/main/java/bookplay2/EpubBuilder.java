package bookplay2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EpubBuilder {

	private static EpubSourceMaterial esm;
//	private static String srcPathRoot;
	private static String epubBuildDestPath;
	
	/*
	 * constructors
	 */

	/*
	 * getters and setters
	 */

	public static String getEpubBuildDestPath() {
		return epubBuildDestPath;
	}
	
	public static void setEpubBuildDestPath(String epubBuildDestPath) {
		EpubBuilder.epubBuildDestPath = epubBuildDestPath;
	}
	
//	public String getSrcPathRoot() {
//		return srcPathRoot;
//	}
//
//	public void setSrcPathRoot(String srcPathRoot) {
//		this.srcPathRoot = srcPathRoot;
//	}

	public static EpubSourceMaterial getEsm() {
		return esm;
	}

	public void setEsm(EpubSourceMaterial esm) {
		this.esm = esm;
	}

	/*
	 * public methods
	 */
	public static void buildEpubByWalking() {
		try {
			System.out.println(">>>>>> building " + esm.getBookTitle()  + " epub.........");

			FileOutputStream fos = new FileOutputStream(getEpubBuildDestPath() + getEsm().getBookTitle() + ".epub");
			ZipOutputStream zos = new ZipOutputStream(fos);


			addToZipFile(esm.getSrcRoot(), "mimetype", zos, false); // do not compress mimetype

			Path start = Paths.get(esm.getSrcRoot());
			Integer startIndex = start.getNameCount();
			Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					if (file.getFileName().toString().startsWith(".")) {
						System.out.println("skip: " + file);
					} else if (file.getFileName().toString().equals("mimetype")) {
						System.out.println("already got: " + file);
					} else {
						System.out.println("zip: " + file.toString());
						addToZipFile(esm.getSrcRoot(), new String(file.subpath(startIndex, file.getNameCount()).toString()), zos);
					}
					return FileVisitResult.CONTINUE;
				}
			});
			
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
			System.out.println(">>>>>> building " + esm.getBookTitle()  + " epub.........");

			FileOutputStream fos = new FileOutputStream(getEpubBuildDestPath() + getEsm().getBookTitle() + ".epub");
			ZipOutputStream zos = new ZipOutputStream(fos);


			// Set compression level to STORED (uncompressed) for mimetype
			zos.setLevel(ZipOutputStream.STORED);
			addToZipFile(esm.getSrcRoot(), "mimetype", zos, false);

			// Set compression level to DEFLATED (compressed) for everything else
			zos.setLevel(ZipOutputStream.DEFLATED);
			addToZipFile(esm.getSrcRoot(), "META-INF" + File.separator + "container.xml", zos);
			addToZipFile(esm.getSrcRoot(), "OEBPS" + File.separator + "content.opf", zos);
			// TODO "if exists..."
			addToZipFile(esm.getSrcRoot(), "OEBPS" + File.separator + "coverpage.html", zos);
			// TODO "if exists..."
			addToZipFile(esm.getSrcRoot(), "OEBPS" + File.separator + "cover.jpg", zos);
			addToZipFile(esm.getSrcRoot(), "OEBPS" + File.separator + "toc.ncx", zos);
			addToZipFile(esm.getSrcRoot(), "OEBPS" + File.separator + "Styles" + File.separator + "style.css", zos);

			
//			FileUtils.listFiles(directory, extensions, recursive)
//			Collection<File> files = FileUtils.listFiles(FileUtils.getFile(srcPathRoot + "OEBPS/"),
//			         FileFilterUtils.suffixFileFilter(".txt"), TrueFileFilter.INSTANCE);
			
			
			try(Stream<Path> paths = Files.walk(Paths.get(esm.getSrcRoot() + "OEBPS" + File.separator + "Images" + File.separator))) {
			    paths.forEach(filePath -> {
			        if (Files.isRegularFile(filePath)) {
			        	// TODO refactor, maybe don't need two parts any more?  maybe find better way to find OEBPS than assume "-3"?
			        	String name = new String(filePath.subpath(filePath.getNameCount() - 3, filePath.getNameCount()).toString());
						try {
							addToZipFile(esm.getSrcRoot(), name, zos);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    });
			} 
			
			try(Stream<Path> paths = Files.walk(Paths.get(esm.getSrcRoot() + "OEBPS" + File.separator + "Text" + File.separator))) {
			    paths.forEach(filePath -> {
			        if (Files.isRegularFile(filePath)) {
			        	// TODO refactor, maybe don't need two parts any more?  maybe find better way to find OEBPS than assume "-3"?
			        	String name = new String(filePath.subpath(filePath.getNameCount() - 3, filePath.getNameCount()).toString());
						try {
							addToZipFile(esm.getSrcRoot(), name, zos);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    });
			} 
			
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
