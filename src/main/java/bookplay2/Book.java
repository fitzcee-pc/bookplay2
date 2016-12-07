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
		
			if (this.lastPage == null & this.indexPage != null) {FindLastPageGivenIndex();}
			if (this.firstPage == null & this.indexPage != null) {FindFirstPageGivenIndex();}
			if (this.lastPage == null & this.firstPage != null) {FindLastPageGivenFirst();}
			if (this.firstPage == null & this.lastPage != null) {FindFirstPageGivenLast();}
			if (this.indexPage == null & this.lastPage != null) {FindIndexPageGivenLast();}
			if (this.indexPage == null & this.firstPage != null) {FindIndexPageGivenFirst();}
		}
		
		
	}
	
	private void FindIndexPageGivenFirst() {
		BookPage chiDoc = null;
		try {
			chiDoc = new BookPage(Jsoup.parse(new File(firstPage.urlPathBeforePageName + firstPage.indexPageName),null));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  

		this.indexPage = chiDoc;
		
	}

	private void FindIndexPageGivenLast() {
		BookPage chiDoc = null;
		try {
			chiDoc = new BookPage(Jsoup.parse(new File(lastPage.urlPathBeforePageName + lastPage.indexPageName),null));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  

		this.indexPage = chiDoc;
		
	}

	private void FindFirstPageGivenIndex() {
		// TODO Auto-generated method stub
		
	}

	private void FindFirstPageGivenLast() {
		
		BookPage chiDoc = null;
		try {
			chiDoc = new BookPage(Jsoup.parse(new File(lastPage.urlPathBeforePageName + lastPage.prevPageName),null));
		} catch (IOException e1) {
			e1.printStackTrace();
		}  
		
		while (!chiDoc.prevPageName.equals(chiDoc.indexPageName)) {
			try {
				chiDoc = new BookPage(Jsoup.parse(new File(chiDoc.urlPathBeforePageName + chiDoc.prevPageName),null));
			} catch (IOException e) {
				break;
			}  
		}
		this.firstPage = chiDoc;
	}

	private void FindLastPageGivenIndex() {
		// TODO Auto-generated method stub
		
	}

	private void FindLastPageGivenFirst() {
		
		BookPage chiDoc = null;
		try {
			chiDoc = new BookPage(Jsoup.parse(new File(firstPage.urlPathBeforePageName + firstPage.nextPageName),null));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		while (!chiDoc.nextPageName.equals(chiDoc.indexPageName)) {
			try {
				chiDoc = new BookPage(Jsoup.parse(new File(chiDoc.urlPathBeforePageName + chiDoc.nextPageName),null));
			} catch (IOException e) {
				break;
			}  
		}
		this.lastPage = chiDoc;
	}
	
	public String toString() {
		String indexPageString = (indexPage == null) ? "no Index" : indexPage.toString();
		String firstPageString = (firstPage == null) ? "no First Page" : firstPage.toString();
		String lastPageString = (lastPage == null) ? "no Last Page" : lastPage.toString();
		String me = new String(
				"-----index page-----\n\t" + indexPageString
				+ "\n-----first page-----\n\t" + firstPageString
				+ "\n-----last page-----\n\t" + lastPageString);
		return me;
		
	}
	

	
}
