package com.zghw.lucene.search;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.zghw.lucene.store.Dirstore;
@SuppressWarnings("deprecation")
public class SearchSimple {
	/**
	 * 搜索
	 */
	public void searcher() {
		try {
			// 1.创建Directory
			Directory dir = Dirstore.getDirectory();
			// 2.创建IndexReader
			IndexReader reader = IndexReader.open(dir);

			// 3.根据IndexReader创建IndexSearcher
			IndexSearcher searcher = new IndexSearcher(reader);
			// 4.创建搜索的query
			// 创建parser来确定要搜索文件的内容，第二个参数表示搜索的域
			QueryParser parser = new QueryParser(Version.LUCENE_4_10_4,
					"content", new StandardAnalyzer(Version.LUCENE_4_10_4));
			// 创建query，表示搜索域为content中包含java的文档
			Query query = parser.parse("java");
			// 5.根据searcher搜索并且返回TopDocs
			TopDocs tds = searcher.search(query, 10);
			// 6.根据TopDocs获取ScoreDoc对象
			ScoreDoc[] sds = tds.scoreDocs;
			for (ScoreDoc sd : sds) {
				// 7.根据seacher和ScordDoc对象获取具体的Document对象
				Document doc = searcher.doc(sd.doc);
				// 8.根据Document对象获取需要的值
				System.out.println(doc.get("filename") + "[" + doc.get("path")
						+ "]");
			}
			// 9.关闭Reader
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
