package com.zghw.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperSimple implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event:"+ event);
		if(KeeperState.SyncConnected == event.getState()){
			connectedSemaphore.countDown();
		}
	}
	
	public static void main(String args[]) throws IOException{
		String connectString="127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
		int sessionTimeout=5000;
		ZooKeeper zk =new ZooKeeper(connectString,sessionTimeout,new ZookeeperSimple());
		System.out.println("zookeeper status:\n"+zk.getState());
		try {
			connectedSemaphore.await();
		} catch (InterruptedException e) {
			//e.printStackTrace();
			System.out.println("Zookeeper session established .");
		}
	}
}
