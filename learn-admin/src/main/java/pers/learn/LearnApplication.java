/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-05 06:41:04
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-26 10:22:53
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn;
import org.apache.shiro.codec.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import pers.learn.common.util.security.CipherUtils;

@SpringBootApplication
@EnableCaching
public class LearnApplication {

	@Bean
	public ModelMapper modelMapper(){
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper;
	}

	public static void main(String[] args) {
		// 启动Springboot的应用，返回一个Spring的IOC容器
		ConfigurableApplicationContext context = SpringApplication.run(LearnApplication.class, args);
		System.out.println("Have a nice day.");
//		System.out.println(Base64.encodeToString(CipherUtils.generateNewKey(128, "AES").getEncoded()));
		// Object redisTemplate = context.getBean("x");
		// System.out.println("-----" + redisTemplate);
	}

}
