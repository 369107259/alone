package walker.mq.kafka.configuration;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: huangYong
 * @Date: 2021/4/1 15:00
 */
//@Component
public class CustomizePartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] bytes, Object value, byte[] bytes1, Cluster cluster) {
        List<PartitionInfo> partitionInfos = cluster.partitionsForTopic(topic);
        int size = partitionInfos.size();
        int hashCode = value.hashCode();
        return hashCode % size;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
