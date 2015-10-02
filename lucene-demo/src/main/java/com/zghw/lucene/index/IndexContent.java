package com.zghw.lucene.index;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.zghw.lucene.store.ContentStore;

@SuppressWarnings("deprecation")
public class IndexContent {
	private String[] ids = { "1", "2", "3", "4", "5", "6" };
	private String[] emails = { "zhanghongwei@shangpin.com",
			"zhanghongwei@qq.com", "13699120345@shangpin.com", "zghw@163.com",
			"13699120345@shangpin.com", "384240320@qq.com" };
	private String[] contents = { "welcome to my home", "this is zghw's home",
			"I study lucene", "My name is zhanghongwei",
			"I like music and I like moive", "hello lucene" };

	private int[] attachs = { 2, 3, 2, 4, 6, 3 };

	private String[] names = { "zhangsan", "lisi", "john", "Dave", "jetty",
			"wlison" };
	/**
	 * 创建索引
	 */
	public void index() {
		index(OpenMode.CREATE);
	}

	public void index(OpenMode mode) {
		IndexWriter writer = null;
		try {
			// 创建索引文件目录
			Directory dir = ContentStore.getDirectory();
			// 分析器对象
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_4);
			// 索引写入器配置
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_4, analyzer);
			iwc.setOpenMode(mode);
			// 构建索引写入器
			writer = new IndexWriter(dir, iwc);
			// 创建Document对象
			Document doc = null;
			// 为Document添加 Field

			// 循环contents 添加文档到索引中
			for (int i = 0; i < ids.length; i++) {
				doc = new Document();
				// Field.Store.YES表示会把这个域中的内容完全储存到文件中，方便进行文件的还原
				// Field.Store.NO
				// 表示不会把这个域中的内容储存到文件中，但是可以被索引，此时内容无法还原（doc.get无法使用）
				doc.add(new StringField("id", ids[i],  Store.YES));
				doc.add(new StringField("email", emails[i], Store.YES));
				doc.add(new StringField("content", contents[i], Store.NO));
				doc.add(new StringField("name", names[i], Store.YES));
				doc.add(new IntField("attach",attachs[i],Store.YES));
				// 通过IndexWriter添加文档到索引中
				writer.addDocument(doc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public void update() {
		IndexWriter writer = null;
		try {
			// 创建索引文件目录
			Directory dir = ContentStore.getDirectory();
			// 分析器对象
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_4);
			// 索引写入器配置
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_4, analyzer);
			// 构建索引写入器
			writer = new IndexWriter(dir, iwc);
			
			Document doc = new Document();
			// Field.Store.YES表示会把这个域中的内容完全储存到文件中，方便进行文件的还原
			// Field.Store.NO
			// 表示不会把这个域中的内容储存到文件中，但是可以被索引，此时内容无法还原（doc.get无法使用）
			doc.add(new Field("id", "11111", Field.Store.YES,
					Field.Index.NOT_ANALYZED_NO_NORMS));
			doc.add(new Field("email", emails[0], Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			doc.add(new Field("content", contents[0], Field.Store.NO,
					Field.Index.ANALYZED));
			doc.add(new Field("name", names[0], Field.Store.YES,
					Field.Index.NOT_ANALYZED_NO_NORMS));
			//Lucene并没有提供更新，这里的更新操作其实是两个操作的合集 即 先删除后添加
			writer.updateDocument(new Term("id","1"), doc);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void delete() {
		IndexWriter writer = null;
		try {
			// 创建索引文件目录
			Directory dir = ContentStore.getDirectory();
			// 分析器对象
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_4);
			// 索引写入器配置
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_4, analyzer);
			// 构建索引写入器
			writer = new IndexWriter(dir, iwc);
			// 删除索引
			// 参数是一个选项，可以是一个Query,也可以是一个term，term是一个精确查找的值
			// 此时删除的文档并不会被完全删除，而是存储在一个回收站中，可以恢复
			writer.deleteDocuments(new Term("id", "1"));
			// 清空回收站，强制删除
			// writer.forceMergeDeletes();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
