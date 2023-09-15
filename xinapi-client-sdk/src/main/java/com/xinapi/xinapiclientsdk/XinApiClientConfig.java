package com.xinapi.xinapiclientsdk;

import com.xinapi.xinapiclientsdk.client.NameApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("xinapi.client")
@ComponentScan
@Data
public class XinApiClientConfig {
    private String accessKey;

    private String secretKey;

    @Bean
    public NameApiClient xinApiClient(){
        return new NameApiClient(accessKey, secretKey);
    }
}
