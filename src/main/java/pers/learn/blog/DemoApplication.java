/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-05 06:41:04
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-17 19:02:04
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.blog;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableCaching
public class DemoApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		// 启动Springboot的应用，返回一个Spring的IOC容器
		ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
		// Object redisTemplate = context.getBean("x");
		// System.out.println("-----" + redisTemplate);
	}

}
