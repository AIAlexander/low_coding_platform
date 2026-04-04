package com.alex.lowcodingplatform.generate;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Map;

/**
 * @author wsh
 * @date 2026/4/4
 */
public class CodeGenerate {

    private static final String[] TABLE_NAMES = {"user"};


    public static void main(String[] args) {

        Dict dict = YamlUtil.loadByPath("application.yml");
        Map<String, Object> map = dict.getByPath("spring.datasource");

        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(String.valueOf(map.get("url")));
        dataSource.setUsername(String.valueOf(map.get("username")));
        dataSource.setPassword(String.valueOf(map.get("password")));

        //创建配置内容，两种风格都可以。
        GlobalConfig globalConfig = createGlobalConfig();
        //GlobalConfig globalConfig = createGlobalConfigUseStyle2();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfig() {
        // 创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        // 设置根包，建议先生成到一个临时目录下，生成代码之后，再移动到对应的项目目录
        globalConfig.getPackageConfig()
                .setBasePackage("com.alex.lowcodingplatform.genresult");

        // 设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig()
                .setGenerateTable(TABLE_NAMES)
                // 设置逻辑删除的默认字段名称
                .setLogicDeleteColumn("isDelete");

        // 设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true)
                .setJdkVersion(21);

        // 设置生成 mapper
        globalConfig.enableMapper();
        globalConfig.enableMapperXml();

        // 设置生成 Service
        globalConfig.enableService();
        globalConfig.enableServiceImpl();

        // 设置生成 Controller
        globalConfig.enableController();

        // 设置生成注释，比如生成的时间和作者，避免后续多余的代码改动
        globalConfig.getJavadocConfig()
                .setAuthor("wsh")
                .setSince("");
        return globalConfig;
    }
}
