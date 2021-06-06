package walker.mongodb;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
class MongodbApplicationTests {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void mongodbTest(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",123456);
        jsonObject.put("name","huangYong");
        jsonObject.put("age",18);
        mongoTemplate.insert(jsonObject);
    }

}
