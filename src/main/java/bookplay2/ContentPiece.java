package bookplay2;

public class ContentPiece {

	public Boolean IncludeInToc;
	public String Filename;
	public String DisplayName;
	
	public ContentPiece(Boolean includeInToc, String filename, String displayName) {
		super();
		IncludeInToc = includeInToc;
		Filename = filename;
		DisplayName = displayName;
	}
	
	
}
