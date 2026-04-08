package com.alex.lowcodingplatform.ai.facade;

import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author wangshuhao
 * @date 2026/4/8
 */
@SpringBootTest
class GenerateCodeFacadeTest {

    @Resource
    private GenerateCodeFacade facade;

    @Test
    public void genereteCode() {
        Flux<String> stream = facade.generateCodeAndSaveStream("帮我生成一个任务记录的网站", CodeGenerateType.VUE_PROJECT, 0L);
        List<String> block = stream.collectList().block();
        Assertions.assertNotNull(block);
        String join = String.join("", block);
        Assertions.assertNotNull(join);
    }

}