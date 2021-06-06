package walker.zookeeper.lock;

/**
 * @Author: huangYong
 * @Date: 2021/4/15 18:46
 */
public interface Lock {
    /***
     * 获取锁
     */
    void getLock();

    /***
     * 释放锁
     */
    void unlock();
}
