package walker.zookeeper.lock;

import org.I0Itec.zkclient.ZkClient;

/**
 * @Author: huangYong
 * @Date: 2021/4/15 18:49
 */
public abstract class AbstractLock implements Lock{

    private  static final  String connected = "127.0.0.1:2181";
    protected ZkClient zkClient = new ZkClient(connected,5000);
    protected static final String path = "/lock";

    @Override
    public void getLock() {
        if (tryLock()){

        }else {
            waitLock();
            getLock();
        }

    }

    /***
     * 获取锁
     */
    public abstract boolean tryLock();

    /***
     * 等待锁
     */
    public abstract void waitLock();
}
