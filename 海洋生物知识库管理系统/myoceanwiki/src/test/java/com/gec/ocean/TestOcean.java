package com.gec.ocean;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.gec.ocean.entity.User;
import com.gec.ocean.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

@SpringBootTest
public class TestOcean {

    @Autowired(required = false)
    private UserMapper userMapper;


    @Test
    public void contextLoads() {
        String url = "jdbc:mysql://localhost:3306/obwiki?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";

        FastAutoGenerator.create(url, "root", "123456")
                .globalConfig(builder -> {
                    builder.author("lqy") // 设置作者
                            //.enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
//                            .outputDir("E://Code//"); // 指定输出目录
                            .outputDir("D:\\YueQian2\\code"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.gec") // 设置父包名
                            .moduleName("ocean") // 设置父包模块名
//                            .pathInfo(Collections.singletonMap(OutputFile.xml, "E://Code//")); // 设置mapperXml生成路径
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D:\\YueQian2\\code")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("category,content,doc,ebook,ebook_snapshot,user"); // 设置需要生成的表名
                    //.addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }


    @Test
    public void testUser1(){
        List<User> list = userMapper.selectList(null);
        for (User user : list) {
            System.out.println(user);
        }
    }

}
