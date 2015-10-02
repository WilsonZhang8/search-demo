package com.zghw.lucene.store;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class ContentStore {
	private static Directory dir=null;
	static {
		try {
			dir = FSDirectory.open(new File("/home/zghw/indexs01"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 索引文件目录对象
	 * @return
	 */
	public static Directory getDirectory(){
		return dir;
	}
}
