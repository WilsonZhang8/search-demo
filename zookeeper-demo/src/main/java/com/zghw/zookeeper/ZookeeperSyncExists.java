package com.zghw.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperSyncExists implements Watcher {
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zk = null;

	public void process(WatchedEvent event) {
		System.out.println("Receive watched event:" + event);
		if (KeeperState.SyncConnected == event.getState()) {
			try {
				if (EventType.None == event.getType()
						&& null == event.getPath()) {
					connectedSemaphore.countDown();
				} else if (event.getType() == EventType.NodeCreated) {
					// create node 
					System.out.println("Node(" + event.getPath() + ")Created");
					zk.exists(event.getPath(), true);
				} else if (event.getType() == EventType.NodeDataChanged){
					//date change
					System.out.println("Node (" + event.getPath() +")changed");
					zk.exists(event.getPath(), true);
				}else if(event.getType() == EventType.NodeDeleted){
					//node delete
					System.out.println("Node ("+event.getPath()+")deleted");
					zk.exists(event.getPath(), true);
				}
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {
		String connectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
		int sessionTimeout = 5000;
		String path = "/zk-test3";
		zk = new ZooKeeper(connectString, sessionTimeout,
				new ZookeeperSyncExists());
		zk.addAuthInfo("digest", "auth:true".getBytes());

		try {
			connectedSemaphore.await();
			zk.delete(path, -1);
			zk.exists(path, true);
			zk.create(path, "test3".getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
			zk.setData(path, "xxx".getBytes(), -1);
			zk.create(path + "/node1", "node1".getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);
			//zk.delete(path + "/node1", -1);
			//zk.delete(path, -1);
			
			/*ZooKeeper zk1=new ZooKeeper(connectString, sessionTimeout,
					new ZookeeperSyncExists());
			zk1.create(path, "test3".getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);*/
			ZooKeeper zk2=new ZooKeeper(connectString, sessionTimeout,
					new ZookeeperSyncExists());
			zk2.getData(path, true, null);
			
			ZooKeeper zk3=new ZooKeeper(connectString, sessionTimeout,
					new ZookeeperSyncExists());
			zk3.addAuthInfo("digest", "auth:false1".getBytes());
			System.out.println(zk3.getData(path, true, null));
			ZooKeeper zk4=new ZooKeeper(connectString, sessionTimeout,
					new ZookeeperSyncExists());
			zk4.addAuthInfo("digest", "auth:true".getBytes());
			System.out.println(new String(zk4.getData(path, true, null)));
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			System.out.println("Zookeeper session established .");
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}
}
