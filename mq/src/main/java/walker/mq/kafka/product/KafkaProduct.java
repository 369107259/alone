package walker.mq.kafka.product;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: huangYong
 * @Date: 2021/3/31 18:27
 */
@RestController
public class KafkaProduct {
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;


    /***
     * 简单生产消息
     * 可以通过指定key来控制消息发送到同一个分区
     * 也可以通过自定义分区器实现 只能取其一
     * @param message
     */
    @GetMapping("/send/message")
    public void send(@RequestParam String message) {
        kafkaTemplate.send("testTopic", "1",message+Math.random());
        kafkaTemplate.send("testTopic","0",message+Math.random());
    }

    /***
     * 生产消息回调
     * @param message
     */
    @GetMapping("/send/message/callback")
    public void sendWithCallback(@RequestParam String message) {
        kafkaTemplate.send("testTopic", message).addCallback(
                success -> {
                    String topic = success.getRecordMetadata().topic();
                    int partition = success.getRecordMetadata().partition();
                    long offset = success.getRecordMetadata().offset();
                    System.out.println("发送消息成功:" + topic + "-" + partition + "-" + offset);
                }, failure -> {
                    System.out.println("发送消息失败:" + failure.getMessage());
                }
        );

        kafkaTemplate.send("testTopic", message).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("发送消息失败："+throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> success) {
                String topic = success.getRecordMetadata().topic();
                int partition = success.getRecordMetadata().partition();
                long offset = success.getRecordMetadata().offset();
                System.out.println("发送消息成功:" + topic + "-" + partition + "-" + offset);
            }
        });
    }

    /***
     * 事务生产消息
     * @param message
     */
    @GetMapping("/send/message/transaction")
    public void sendTransaction(@RequestParam String message) {
        kafkaTemplate.executeInTransaction(kafkaOperations -> kafkaOperations.send("testTopic", message));
    }
}
