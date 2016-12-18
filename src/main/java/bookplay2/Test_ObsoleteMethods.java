package bookplay2;

public class Test_ObsoleteMethods {

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


}
