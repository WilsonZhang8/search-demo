package com.zghw.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZookeeperAsyncGet implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zk = null;
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event:"+ event);
		if(KeeperState.SyncConnected == event.getState()){
			if(EventType.None==event.getType() && null == event.getPath()){
				connectedSemaphore.countDown();
			}else if(event.getType() == EventType.NodeChildrenChanged){
				List<String> childList1;
				try {
					childList1 = zk.getChildren(event.getPath(), true);
					System.out.println("watch: "+childList1);
					
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
	}
	
	public static void main(String args[]) throws IOException{
		String connectString="127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
		int sessionTimeout=5000;
		zk =new ZooKeeper(connectString,sessionTimeout,new ZookeeperAsyncGet());
		System.out.println("zookeeper status:\n"+zk.getState());
		try {
			connectedSemaphore.await();
			long sessionId= zk.getSessionId();
			byte[] passwd = zk.getSessionPasswd();
			//invaild
			//zk=new ZooKeeper(connectString,sessionTimeout,new ZookeeperSync(),1l,passwd);
			//right
			zk=new ZooKeeper(connectString,sessionTimeout,new ZookeeperAsyncGet(),sessionId,passwd);
			String path1=zk.create("/zk-test1", "zktest1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.getChildren("/zk-test1",true,new IChildren2Callback(),null);
			
			zk.create("/zk-test1/node1", "node1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			zk.create("/zk-test1/node2", "node2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			System.out.println("create node1 path= "+path1);
			String path2=zk.create("/zk-test2", "zktest2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create("/zk-test2/node1", "zktestnode2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			zk.getChildren("/zk-test2", true,new IChildren2Callback(),null);
			zk.create("/zk-test2/node2", "zktestnode2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			System.out.println("create node2 path= "+path2);
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			//e.printStackTrace();
			System.out.println("Zookeeper session established .");
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
class IChildren2Callback implements AsyncCallback.Children2Callback{

	public void processResult(int rc, String path, Object ctx,
			List<String> children, Stat stat) {
		System.out.println("callback :" + rc +" ,"+path+" ,"+" childlist:"+children +" stat:"+stat);
	}
	
}