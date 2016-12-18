package bookplay2;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class EpubBuilder {

	private EpubSourceMaterial esm;
	private String destPath;
	private String destFilename;
	
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


}
