package com.zhaoyi.yijiaoyou;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 测试
 *
 * @author zhaoyi
 */
@SpringBootTest
class YiJiaoYouApplicationTests {

	@Resource
	private RedisTemplate redisTemplate;



	@Test
	void contextLoads() {
	}

	@Test
	void test(){
		ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
		valueOperations.set("key","value");
		System.out.println(valueOperations.get("key"));
	}

}
