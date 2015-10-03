package com.zghw.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZookeeperSync implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zk = null;
	private static Stat stat=new Stat();
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
					System.out.println(new String(zk.getData(event.getPath(), true, stat)));
					System.out.println("stat: czxid :"+stat.getCzxid()+" ,mzxid:"+stat.getMzxid()+" ,version:"+stat.getVersion());
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
		zk =new ZooKeeper(connectString,sessionTimeout,new ZookeeperSync());
		System.out.println("zookeeper status:\n"+zk.getState());
		try {
			connectedSemaphore.await();
			long sessionId= zk.getSessionId();
			byte[] passwd = zk.getSessionPasswd();
			//invaild
			//zk=new ZooKeeper(connectString,sessionTimeout,new ZookeeperSync(),1l,passwd);
			//right
			zk=new ZooKeeper(connectString,sessionTimeout,new ZookeeperSync(),sessionId,passwd);
			zk.delete("/zk-test1", -1);
			zk.delete("/zk-test2", -1);
			String path1=zk.create("/zk-test1", "zktest1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			List<String> childList1=zk.getChildren("/zk-test1", true);
			System.out.println("childList1:"+childList1);
			zk.create("/zk-test1/node1", "node1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			zk.create("/zk-test1/node2", "node2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			System.out.println("create node1 path= "+path1);
			String path2=zk.create("/zk-test2", "zktest2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create("/zk-test2/node1", "zktestnode2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			List<String> childList2=zk.getChildren("/zk-test2", true);
			System.out.println("childList2:"+childList2);
			zk.create("/zk-test2/node2", "zktestnode2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			System.out.println("create node2 path= "+path2);
			System.out.println("main:"+new String(zk.getData("/zk-test1/node1", true, stat)));
			System.out.println("stat11: czxid :"+stat.getCzxid()+" ,mzxid:"+stat.getMzxid()+" ,version:"+stat.getVersion());
			//CreateMode.EPHEMERAL_SEQUENTIAL临时并且顺序目录不能直接/zk-test2/node2 
			//System.out.println(new String(zk.getData("/zk-test2/node2", true, stat)));
			//System.out.println("stat22: czxid :"+stat.getCzxid()+" ,mzxid:"+stat.getMzxid()+" ,version:"+stat.getVersion());
			Stat s=zk.setData("/zk-test1/node1", "new data 1111".getBytes(), -1);
			System.out.println("version set: czxid :"+s.getCzxid()+" ,mzxid:"+s.getMzxid()+" ,version:"+s.getVersion());
			Stat s2=zk.setData("/zk-test1/node1", "aaa".getBytes(), s.getVersion());
			System.out.println("version set: czxid :"+s2.getCzxid()+" ,mzxid:"+s2.getMzxid()+" ,version:"+s2.getVersion());
			//KeeperErrorCode = BadVersion 分布式锁需要版本来控制
			//Stat s3=zk.setData("/zk-test1/node1", "333".getBytes(), s.getVersion());
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
