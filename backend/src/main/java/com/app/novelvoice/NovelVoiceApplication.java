package com.app.novelvoice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.app.novelvoice.mapper")
public class NovelVoiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelVoiceApplication.class, args);
    }

}
