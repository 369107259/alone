package walker.zookeeper.lock;

import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: huangYong
 * @Date: 2021/4/15 18:35
 */
@Configuration
public class ZookeeperConfig {

    @Value("${zookeeper.address}")
    private String connection;

    @Value("${zookeeper.timeout}")
    private Integer timeout;

    public ZkClient getZkClient(){
        return  new ZkClient(connection,timeout);
    }
}
