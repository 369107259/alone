package walker.mq.kafka.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;

/**
 * @Author: huangYong
 * @Date: 2021/4/1 11:41
 */
@Configuration
public class KafkaInitialConfiguration{
    @Autowired
    private ConsumerFactory consumerFactory;

    @Bean
    public NewTopic initTopic(){
        return new NewTopic("testTopic",3, (short) 2);
    }

    @Bean
    public NewTopic topic(){
        return new NewTopic("newTopic",2, (short) 1);
    }


    /***
     * 消费异常处理器
     */
    @Bean
    public ConsumerAwareListenerErrorHandler consumerAwareErrorHandler() {
        return (message, exception, consumer) -> {
            System.out.println("消费异常：" + message.getPayload());
            return null;
        };
    }

    /***
     * 消息过滤器
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<?,?> filterContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<?,?> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 被过滤的消息将被丢弃
        factory.setAckDiscarded(true);
        // 消息过滤策略
        factory.setRecordFilterStrategy(consumerRecord -> {
            if (Integer.parseInt(consumerRecord.value().toString()) % 2 == 0) {
                return false;
            }
            //返回true消息则被过滤
            return true;
        });
        return factory;
    }
}