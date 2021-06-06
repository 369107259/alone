package walker.mq.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: huangYong
 * @Date: 2021/4/1 10:44
 */
@Component
public class KafkaConsumer {

/*    @KafkaListener(id = "consumer1",groupId = "defaultConsumerGroup",topicPartitions = {
            @TopicPartition(topic = "testTopic",partitions = {"0","1","2"})
    })
    public void consumerMessage0(ConsumerRecord<?,?> record){
        System.out.println(record.partition()+"分区消费："+record.topic()+"-"+record.partition()+"-"+record.value());
    }*/

    /***
     * errorHandler --异常处理器
     * containerFactory --消息过滤器
     */
    @KafkaListener(id = "consumer1",groupId = "defaultConsumerGroup",topicPartitions = {
            @TopicPartition(topic = "testTopic",partitions = {"0","1","2"})
    },errorHandler = "consumerAwareErrorHandler",containerFactory = "filterContainerFactory")
    @SendTo("newTopic")
    public void consumerMessage1(List<ConsumerRecord<?,?>> records){
        for (ConsumerRecord<?,?> record :records){
            System.out.println(record.partition()+"分区消费："+record.topic()+"-"+record.partition()+"-"+record.value());
        }
    }




}
