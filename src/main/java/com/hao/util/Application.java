package com.hao.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
//@EnableAspectJAutoProxy(proxyTargetClass = true)
//@EnableAutoConfiguration
public class Application {
	@Autowired
	public static void main(String[] args) {
		ApplicationContext ctx =  SpringApplication.run(Application.class, args);
		String[] beanNames =  ctx.getBeanDefinitionNames();
		System.out.println("beanNames个数："+beanNames.length);
		for(String bn:beanNames){
			System.out.println(bn);
		}
		MyService myService = (MyService)ctx.getBean("myService");
			myService.wrong();
	}
}
