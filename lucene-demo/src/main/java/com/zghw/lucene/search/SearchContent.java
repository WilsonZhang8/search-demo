package com.zghw.lucene.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import com.zghw.lucene.store.ContentStore;

public class SearchContent {
	private IndexReader reader;
	/**
	 * 搜索
	 */
	public void searcher() {
		try {
			// 1.创建Directory
			Directory dir = ContentStore.getDirectory();
			// 2.创建IndexReader
			IndexReader reader = IndexReader.open(dir);
			//有多少文档被存储
			System.out.println("存储的文档数量："+reader.numDocs());
			System.out.println("总存储的文档数量："+reader.maxDoc());
			System.out.println("删除的为文档数量："+reader.numDeletedDocs());
			// 9.关闭Reader
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IndexSearcher getSearcher(){
		try {
			Directory dir = ContentStore.getDirectory();
			
			if(reader==null){
				reader= IndexReader.open(dir);
			}else{
				IndexReader tr = IndexReader.open(dir);
				reader = tr;
			}
			IndexSearcher searcher = new IndexSearcher(reader);
			return searcher;
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 精确查询
	 * @param field 搜索域
	 * @param name 对应搜索词
	 * @param num 条数
	 */
	public void searchByTerm(String field,String name,int num){
		try {
			IndexSearcher searcher = getSearcher();
			Query query = new TermQuery(new Term(field, name));
			TopDocs tds = searcher.search(query, num);
			System.out.println("一共查询了：" + tds.totalHits);
			for (ScoreDoc sd : tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println(doc.get("") + "--->" + doc.get("name") + "["
						+ doc.get("email") + "]" + "," + doc.get("attach")
						+ "," + doc.get("date"));
			}
		} catch (Exception e) {
		}
	}
	/**
	 * 字符串范围查询
	 * @param field
	 * @param start
	 * @param end
	 * @param num
	 */
	public void searcherByTermRange(String field,String start,String end,int num){
			try {
				IndexSearcher searcher = getSearcher();
				Query query = TermRangeQuery.newStringRange(field, start, end,
						true, true);
				TopDocs tds = searcher.search(query, num);
				System.out.println("一共查询了：" + tds.totalHits);
				for (ScoreDoc sd : tds.scoreDocs) {
					Document doc = searcher.doc(sd.doc);
					System.out.println(doc.get("") + "--->" + doc.get("name")
							+ "[" + doc.get("email") + "]" + ","
							+ doc.get("attach") + "," + doc.get("date"));
				}
			} catch (Exception e) {
			}
	}
	public void searcherByNumricRange(String field,int start,int end,int num){
		try {
			IndexSearcher searcher = getSearcher();
			Query query = NumericRangeQuery.newIntRange(field, start, end, true,true);
			TopDocs tds = searcher.search(query, num);
			System.out.println("一共查询了：" + tds.totalHits);
			for (ScoreDoc sd : tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println(doc.get("") + "--->" + doc.get("name")
						+ "[" + doc.get("email") + "]" + ","
						+ doc.get("attach") + "," + doc.get("date"));
			}
		} catch (Exception e) {
		}
}
}
