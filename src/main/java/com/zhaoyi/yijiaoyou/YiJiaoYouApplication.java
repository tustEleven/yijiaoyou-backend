package com.zhaoyi.yijiaoyou;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * 启动项
 *
 * @author zhaoyi
 */
@SpringBootApplication
@Slf4j
@MapperScan("com.zhaoyi.yijiaoyou.mapper")
public class YiJiaoYouApplication {

	public static void main(String[] args) {
		SpringApplication.run(YiJiaoYouApplication.class, args);
		log.info("项目启动成功");
	}
}
