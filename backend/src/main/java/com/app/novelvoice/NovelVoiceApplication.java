package com.app.novelvoice;

import com.gmrfid.excel.annotation.EnableExcel;
import com.gmrfid.file.storage.annotation.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.app.novelvoice.mapper")
@EnableFileStorage
@EnableExcel
public class NovelVoiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelVoiceApplication.class, args);
    }

}
