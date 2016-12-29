//package bookplay2Obsolete;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.jsoup.Jsoup;
//
//public class MyBook {
//	MyBookPage indexPage;
//	MyBookPage firstPage;
//	MyBookPage lastPage;
//	List<MyBookPage> pages;
//	List<String> toc;
//	
//	
//	// TODO: 1. require first page
//	//       2. add first page to thisMyBook.
//	//       3. FindLastGivenFirst - add pages to thisMyBook as we go.
//	public MyBook(MyBookPage theIndexPage, MyBookPage theFirstPage, MyBookPage theLastPage) {
//
//		if (theIndexPage == null & theFirstPage == null & theLastPage == null) {
//			// TODO throw error
//		} else {
//			
//			pages = new ArrayList<MyBookPage>();
//			toc = new ArrayList<String>();
//			
//			if (theIndexPage != null) {
//				this.indexPage = theIndexPage;
//				pages.add(theIndexPage);
//				}
//			if (theFirstPage != null) {
//				this.firstPage = theFirstPage;
//				pages.add(theFirstPage);
//				this.appendToToc(theFirstPage.thisPageOutfileName);
//				}
//			if (theLastPage != null) {
//				this.lastPage = theLastPage;
//				pages.add(theLastPage);
//				}
//		
//			if (this.lastPage == null & this.indexPage != null) {FindLastPageGivenIndex();}
//			if (this.firstPage == null & this.indexPage != null) {FindFirstPageGivenIndex();}
//			if (this.lastPage == null & this.firstPage != null) {FindLastPageGivenFirst();}
//			if (this.firstPage == null & this.lastPage != null) {FindFirstPageGivenLast();}
//			if (this.indexPage == null & this.lastPage != null) {FindIndexPageGivenLast();}
//			if (this.indexPage == null & this.firstPage != null) {FindIndexPageGivenFirst();}
//		}
//		
//	}
//	
//	private void FindIndexPageGivenFirst() {
//		MyBookPage myBookPage = null;
//		try {
//			myBookPage = new MyBookPage(Jsoup.parse(new File(firstPage.urlPathBeforePageName + firstPage.indexPageName),null),"Chap00001.html");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}  
//
//		this.indexPage = myBookPage;
//		
//	}
//
//	private void FindIndexPageGivenLast() {
//		MyBookPage myBookPage = null;
//		try {
//			myBookPage = new MyBookPage(Jsoup.parse(new File(lastPage.urlPathBeforePageName + lastPage.indexPageName),null),"Chap99999Last.html");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}  
//
//		this.indexPage = myBookPage;
//		
//	}
//
//	private void FindFirstPageGivenIndex() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void FindFirstPageGivenLast() {
//		
//		MyBookPage myBookPage = null;
//		try {
//			myBookPage = new MyBookPage(Jsoup.parse(new File(lastPage.urlPathBeforePageName + lastPage.prevPageName),null),"xxx");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}  
//		
//		while (!myBookPage.prevPageName.equals(myBookPage.indexPageName)) {
//			try {
//				myBookPage = new MyBookPage(Jsoup.parse(new File(myBookPage.urlPathBeforePageName + myBookPage.prevPageName),null),"xxx");
//			} catch (IOException e) {
//				break;
//			}  
//		}
//		this.firstPage = myBookPage;
//	}
//
//	private void FindLastPageGivenIndex() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void FindLastPageGivenFirst() {
//		
//		Integer chapterNum = 1;
//		
//		MyBookPage myBookPage = null;
//		try {
//			myBookPage = new MyBookPage(Jsoup.parse(new File(firstPage.urlPathBeforePageName + firstPage.nextPageName),null), "Chap" + String.format("%04d", chapterNum) + ".html");
//			appendToToc(myBookPage.thisPageOutfileName);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}  
//		
//		while (!myBookPage.nextPageName.equals(myBookPage.indexPageName)) {
//			chapterNum += 1;
//			try {
//				myBookPage = new MyBookPage(Jsoup.parse(new File(myBookPage.urlPathBeforePageName + myBookPage.nextPageName),null), "Chap" + String.format("%04d", chapterNum) + ".html");
//				appendToToc(myBookPage.thisPageOutfileName);
//			} catch (IOException e) {
//				break;
//			}  
//		}
//		this.lastPage = myBookPage;
//	}
//	
//	public String toString() {
//		String tocString = (toc == null) ? "empty toc" : toc.toString();
//		String indexPageString = (indexPage == null) ? "no Index" : indexPage.toString();
//		String firstPageString = (firstPage == null) ? "no First Page" : firstPage.toString();
//		String lastPageString = (lastPage == null) ? "no Last Page" : lastPage.toString();
//		String me = new String(
//				"-----index page-----\n\t" + indexPageString
//				+ "\n-----first page-----\n\t" + firstPageString
//				+ "\n-----last page-----\n\t" + lastPageString
//				+ "\n-----toc-----\n\t" + tocString
//				+ "\n-----page count----\n\t" + PagesBetween(firstPage, lastPage));
//		return me;
//		
//	}
//	
//	public Double PagesBetween(MyBookPage startPage, MyBookPage endPage) {
//		
//		Double pageCount = 1.0;// count start
//
//		MyBookPage curPage = null;
//		try {
//			curPage = new MyBookPage(Jsoup.parse(new File(startPage.urlPathBeforePageName + startPage.nextPageName),null), "");
////			if (!bookPage.thisPageName.equals(endPage.thisPageName)) {
//				pageCount += 1; // don't double count end page in case only 2
////			}
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}  
//		
//		while (!curPage.nextPageName.equals(endPage.thisPageName)) {
//			try {
//				curPage = new MyBookPage(Jsoup.parse(new File(curPage.urlPathBeforePageName + curPage.nextPageName),null), "");
//				pageCount += 1;
//			} catch (IOException e) {
//				break;
//			}  
//		}
//
//		if (curPage.nextPageName.equals(endPage.thisPageName)) {
//			pageCount +=1;
//		}
//		
//
//		return pageCount;
//		
//	}
//
//	public void appendToToc(String fileName){
//		toc.add(fileName);
//	}
//	
//}
