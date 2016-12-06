package bookplay2;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;

public class Book {
	BookPage indexPage;
	BookPage firstPage;
	BookPage lastPage;
	
	public Book(BookPage theIndexPage, BookPage theFirstPage, BookPage theLastPage) {

		if (theIndexPage == null & theFirstPage == null & theLastPage == null) {
			// TODO throw error
		} else {
			
			if (theIndexPage != null) {this.indexPage = theIndexPage;}
			if (theFirstPage != null) {this.firstPage = theFirstPage;}
			if (theLastPage != null) {this.lastPage = theLastPage;}
		
			if (theLastPage == null & theIndexPage != null) {FindLastPageGivenIndex();}
			if (theFirstPage == null & theIndexPage != null) {FindFirstPageGivenIndex();}
			if (theLastPage == null & theFirstPage != null) {FindLastPageGivenFirst();}
			if (theFirstPage == null & theLastPage != null) {FindFirstPageGivenLast();}
		}
		
		
	}
	
	private void FindFirstPageGivenLast() {
		// TODO Auto-generated method stub
		
	}

	private void FindFirstPageGivenIndex() {
		// TODO Auto-generated method stub
		
	}

	private void FindLastPageGivenIndex() {
		// TODO Auto-generated method stub
		
	}

	private void FindLastPageGivenFirst() {
		BookPage chiDoc = null;
		try {
			chiDoc = new BookPage(Jsoup.parse(new File(chiDoc.urlPathBeforePageName + chiDoc.prevPageName),null));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  

		while (chiDoc.prevPageName != chiDoc.indexPageName) {
			try {
				chiDoc = new BookPage(Jsoup.parse(new File(chiDoc.urlPathBeforePageName + chiDoc.prevPageName),null));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		this.firstPage = chiDoc;
	}
	
}
