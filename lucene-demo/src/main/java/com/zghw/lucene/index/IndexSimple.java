package com.zghw.lucene.index;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.zghw.lucene.store.Dirstore;

@SuppressWarnings("deprecation")
public class IndexSimple {
	/**
	 * 创建索引
	 */
	public void index(){
		index(OpenMode.CREATE);
	}
	public void index(OpenMode mode) {
		IndexWriter writer = null;
		try {
			// 创建索引文件目录
			Directory dir = Dirstore.getDirectory();
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
			//为Document添加 Field
			File file = new File("/home/zghw/testdoc");
			//循环目录下的文件 添加文档到索引中
			for(File f:file.listFiles()){
				doc = new Document();
				doc.add(new Field("content",new FileReader(f)));
				doc.add(new Field("filename",f.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("path",f.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				//通过IndexWriter添加文档到索引中
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
}
