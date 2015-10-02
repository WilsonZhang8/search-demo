package com.zghw.lucene.test;

import org.junit.Test;

import com.zghw.lucene.search.SearchContent;
import com.zghw.lucene.search.SearchSimple;

public class TestSearch {
	
	@Test
	public void testSearch(){
		SearchSimple sc =new SearchSimple();
		sc.searcher();
	}
	
	@Test
	public void testContentSearch(){
		SearchContent sc =new SearchContent();
		sc.searcher();
	}
	
	@Test
	public void testSearcherTerm(){
		SearchContent sc =new SearchContent();
		sc.searchByTerm("name", "wlison", 3);
	}
	
	@Test
	public void testSearcherTermRange(){
		SearchContent sc =new SearchContent();
		sc.searcherByTermRange("id", "1", "4", 3);
	}
	@Test
	public void testSearcherNumRange(){
		SearchContent sc =new SearchContent();
		sc.searcherByNumricRange("attach", 1, 4, 2);
	}
}
