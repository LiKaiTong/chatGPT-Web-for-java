package com.likaitong.chatgptweb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author LKT
 * @create 2023-06-16 14:55
 */

@ConfigurationProperties(prefix = "gpt")
@Component
@Data
public class GPTConfig {
    String url;
    String apiKey;
    String model;
    boolean proxy=false;
    int proxyPort=7890;
}
