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

	private EpubSourceMaterial esm;
//	private  String srcPathRoot;
	private  String epubBuildDestPath;
	
	/*
	 * constructors
	 */

	/*
	 * getters and setters
	 */

	public  String getEpubBuildDestPath() {
		return epubBuildDestPath;
	}
	
	public  void setEpubBuildDestPath(String epubBuildDestPath) {
		this.epubBuildDestPath = epubBuildDestPath;
	}
	
//	public String getSrcPathRoot() {
//		return srcPathRoot;
//	}
//
//	public void setSrcPathRoot(String srcPathRoot) {
//		this.srcPathRoot = srcPathRoot;
//	}

	public  EpubSourceMaterial getEsm() {
		return esm;
	}

	public void setEsm(EpubSourceMaterial esm) {
		this.esm = esm;
	}

	/*
	 * public methods
	 */
	public  void buildEpubByWalkingSrc() {
		try {
			System.out.println(">>>>>> building " + esm.getBookTitle()  + " epub.........");

			FileOutputStream fos = new FileOutputStream(getEpubBuildDestPath() + getEsm().getBookTitle() + ".epub");
			ZipOutputStream zos = new ZipOutputStream(fos);


			addToZipFile(esm.getSrcRoot(), "mimetype", zos, false); // do not compress mimetype

			Path rootPath = Paths.get(esm.getSrcRoot());
			Integer startIndex = rootPath.getNameCount();
			Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path filePath,
						BasicFileAttributes attrs) throws IOException {
					if (filePath.getFileName().toString().startsWith(".")) {
						System.out.println("skip: " + filePath);
					} else if (filePath.getFileName().toString().equals("mimetype")) {
						System.out.println("already got: " + filePath);
					} else {
						System.out.println("zip: " + filePath.toString());
						addToZipFile(esm.getSrcRoot(), new String(filePath.subpath(startIndex, filePath.getNameCount()).toString()), zos);
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

	public  void buildEpub() {
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

			addToZipFileIfExists(esm.getSrcRoot(), "OEBPS" + File.separator + "coverpage.html",  zos, true);
			addToZipFileIfExists(esm.getSrcRoot(), "OEBPS" + File.separator + "cover.jpg", zos, true);
			
			addToZipFile(esm.getSrcRoot(), "OEBPS" + File.separator + "toc.ncx", zos);
			addToZipFile(esm.getSrcRoot(), "OEBPS" + File.separator + "Styles" + File.separator + "style.css", zos);

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
	private  void addToZipFileIfExists(String sourceFolder, String fileName, ZipOutputStream zos, boolean compress) throws FileNotFoundException, IOException {
		File file = new File(sourceFolder + fileName);
		if(file.exists() && !file.isDirectory()) { 
			addToZipFile(sourceFolder, fileName, zos, compress);
		}
	}

	private  void addToZipFile(String sourceFolder, String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {
		addToZipFile(sourceFolder, fileName, zos, true);
	}
	

	@SuppressWarnings("-access")
	private  void addToZipFile(String sourceFolder, String fileName, ZipOutputStream zos, boolean compress) throws FileNotFoundException, IOException {
		
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
