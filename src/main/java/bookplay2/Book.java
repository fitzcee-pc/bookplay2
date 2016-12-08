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
		BookPage bookPage = null;
		try {
			bookPage = new BookPage(Jsoup.parse(new File(firstPage.urlPathBeforePageName + firstPage.indexPageName),null));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  

		this.indexPage = bookPage;
		
	}

	private void FindIndexPageGivenLast() {
		BookPage bookPage = null;
		try {
			bookPage = new BookPage(Jsoup.parse(new File(lastPage.urlPathBeforePageName + lastPage.indexPageName),null));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  

		this.indexPage = bookPage;
		
	}

	private void FindFirstPageGivenIndex() {
		// TODO Auto-generated method stub
		
	}

	private void FindFirstPageGivenLast() {
		
		BookPage bookPage = null;
		try {
			bookPage = new BookPage(Jsoup.parse(new File(lastPage.urlPathBeforePageName + lastPage.prevPageName),null));
		} catch (IOException e1) {
			e1.printStackTrace();
		}  
		
		while (!bookPage.prevPageName.equals(bookPage.indexPageName)) {
			try {
				bookPage = new BookPage(Jsoup.parse(new File(bookPage.urlPathBeforePageName + bookPage.prevPageName),null));
			} catch (IOException e) {
				break;
			}  
		}
		this.firstPage = bookPage;
	}

	private void FindLastPageGivenIndex() {
		// TODO Auto-generated method stub
		
	}

	private void FindLastPageGivenFirst() {
		
		BookPage bookPage = null;
		try {
			bookPage = new BookPage(Jsoup.parse(new File(firstPage.urlPathBeforePageName + firstPage.nextPageName),null));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		while (!bookPage.nextPageName.equals(bookPage.indexPageName)) {
			try {
				bookPage = new BookPage(Jsoup.parse(new File(bookPage.urlPathBeforePageName + bookPage.nextPageName),null));
			} catch (IOException e) {
				break;
			}  
		}
		this.lastPage = bookPage;
	}
	
	public String toString() {
		String indexPageString = (indexPage == null) ? "no Index" : indexPage.toString();
		String firstPageString = (firstPage == null) ? "no First Page" : firstPage.toString();
		String lastPageString = (lastPage == null) ? "no Last Page" : lastPage.toString();
		String me = new String(
				"-----index page-----\n\t" + indexPageString
				+ "\n-----first page-----\n\t" + firstPageString
				+ "\n-----last page-----\n\t" + lastPageString
				+ "\n-----page count----\n\t" + PagesBetween(firstPage, lastPage));
		return me;
		
	}
	
	public Double PagesBetween(BookPage startPage, BookPage endPage) {
		
		Double pageCount = 1.0;// count start

		BookPage curPage = null;
		try {
			curPage = new BookPage(Jsoup.parse(new File(startPage.urlPathBeforePageName + startPage.nextPageName),null));
//			if (!bookPage.thisPageName.equals(endPage.thisPageName)) {
				pageCount += 1; // don't double count end page in case only 2
//			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}  
		
		while (!curPage.nextPageName.equals(endPage.thisPageName)) {
			try {
				curPage = new BookPage(Jsoup.parse(new File(curPage.urlPathBeforePageName + curPage.nextPageName),null));
				pageCount += 1;
			} catch (IOException e) {
				break;
			}  
		}

		if (curPage.nextPageName.equals(endPage.thisPageName)) {
			pageCount +=1;
		}
		

		return pageCount;
		
	}
	
	
}
