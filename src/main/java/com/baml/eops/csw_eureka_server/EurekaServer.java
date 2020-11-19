package com.baml.eops.csw_eureka_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import brave.context.log4j2.ThreadContextScopeDecorator;
import brave.propagation.CurrentTraceContext.ScopeDecorator;

@SpringBootApplication
@EnableEurekaServer
@PropertySource("classpath:properties/db.properties")

public class EurekaServer {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServer.class, args);
	}
	 @Bean
	 ScopeDecorator log4jScopeDecorator(){
	 	return ThreadContextScopeDecorator.create();
	 }
}
