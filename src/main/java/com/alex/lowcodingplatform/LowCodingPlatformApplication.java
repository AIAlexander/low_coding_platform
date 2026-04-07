package com.alex.lowcodingplatform;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.alex.lowcodingplatform.mapper")
public class LowCodingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(LowCodingPlatformApplication.class, args);
    }

}
