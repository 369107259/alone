package alone.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AloneGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AloneGatewayApplication.class, args);
    }

}
