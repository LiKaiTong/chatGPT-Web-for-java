package com.likaitong.chatgptweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.likaitong.chatgptweb.dao")
public class ChatgptWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatgptWebApplication.class, args);
    }

}
