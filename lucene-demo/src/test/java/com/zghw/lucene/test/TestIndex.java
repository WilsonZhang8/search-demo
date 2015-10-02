package com.zghw.lucene.test;
import org.junit.Test;

import com.zghw.lucene.index.IndexContent;
import com.zghw.lucene.index.IndexSimple;


public class TestIndex {
	
	@Test
	public void testIndex(){
		IndexSimple ic =new IndexSimple();
		ic.index();
	}
	@Test
	public void testContentIndex(){
		IndexContent ic=new IndexContent();
		ic.index();
	}
	
	@Test
	public void testContentIndexDelete(){
		IndexContent ic=new IndexContent();
		ic.delete();
	}
	
	@Test
	public void testContentIndexUpdate(){
		IndexContent ic=new IndexContent();
		ic.update();
	}
	
}
