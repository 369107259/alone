package walker.zookeeper.lock;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: huangYong
 * @Date: 2021/4/15 19:06
 */
public class ZookeeperDistributeLock extends AbstractLock {

    /***
     * 当前节点
     */
    private String currentPath;
    /**
     * 当前请求得前一个节点
     */
    private String beforePath;
    private CountDownLatch countDownLatch = null;

    public ZookeeperDistributeLock() {
        if (!this.zkClient.exists(path)) {
            this.zkClient.createPersistent(path);
        }
    }

    @Override
    public boolean tryLock() {
        if (null == currentPath || currentPath.length() <= 0) {
            currentPath = this.zkClient.createEphemeralSequential(path, "data");
        }
        List<String> children = zkClient.getChildren(path);
        Collections.sort(children);
        if (currentPath.equals(path + "/" + children.get(0))) {
            return true;
        } else {
            int length = path.length();
            int binarySearch = Collections.binarySearch(children, currentPath.substring(length + 1));
            beforePath = path + "/" + children.get(binarySearch - 1);
        }
        return false;
    }

    @Override
    public void waitLock() {
        IZkDataListener zkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                countDownLatch.countDown();
            }
        };
        //监听前一个结点，存在则等待
        this.zkClient.subscribeDataChanges(beforePath, zkDataListener);
        if (this.zkClient.exists(beforePath)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //不存在则关闭对前一个节点得监听
        this.zkClient.unsubscribeDataChanges(beforePath, zkDataListener);
    }

    @Override
    public void unlock() {
        zkClient.delete(currentPath);
        zkClient.close();
    }
}
