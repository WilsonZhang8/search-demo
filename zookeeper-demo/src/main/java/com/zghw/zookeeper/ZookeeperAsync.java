package com.zghw.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperAsync implements Watcher{
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
		ZooKeeper zk =new ZooKeeper(connectString,sessionTimeout,new ZookeeperAsync());
		System.out.println("zookeeper status:\n"+zk.getState());
		try {
			connectedSemaphore.await();
			long sessionId= zk.getSessionId();
			byte[] passwd = zk.getSessionPasswd();
			//invaild
			//zk=new ZooKeeper(connectString,sessionTimeout,new ZookeeperSync(),1l,passwd);
			//right
			zk=new ZooKeeper(connectString,sessionTimeout,new ZookeeperAsync(),sessionId,passwd);
			zk.create("/zookeeper-test", "123".getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,new IStringCallback(),"I am content" );
			zk.create("/zookeeper-test", "123".getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,new IStringCallback(),"I am content" );
			zk.create("/zookeeper-test", "123".getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,new IStringCallback(),"I am content" );
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			System.out.println("Zookeeper session established .");
		}
	}
	
}
class IStringCallback implements AsyncCallback.StringCallback{
	public void processResult(int rc, String path, Object ctx, String name) {
		System.out.println("create node: ["+ rc +"]" + " "+path +" ,"+ctx+" ,real path:"+name);
	}
	
}
