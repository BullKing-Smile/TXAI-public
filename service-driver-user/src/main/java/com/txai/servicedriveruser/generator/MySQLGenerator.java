package com.txai.servicedriveruser.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class MySQLGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/your_db?useSSL=false", "username", "password")
                .globalConfig(builder -> {
                    builder.author("yourname") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java") // 指定输出目录
                            .disableOpenDir(); // 禁止打开输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example") // 设置父包名
                            .moduleName("system") // 设置模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")); // 设置mapper.xml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("table1", "table2") // 设置需要生成的表名
                            .addTablePrefix("t_", "sys_") // 设置过滤表前缀
                            .entityBuilder()
                            .enableLombok() // 启用Lombok
                            .controllerBuilder()
                            .enableRestStyle(); // 启用RestController
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板
                .execute();
    }
}
