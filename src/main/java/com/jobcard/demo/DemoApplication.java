package com.jobcard.demo;

import cn.hutool.Hutool;
import com.jobcard.demo.service.CheckAndBuild;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.Map;

@EnableWebSocket
@EnableScheduling
@SpringBootApplication
public class DemoApplication {
    public static ConfigurableApplicationContext ac;
    public static Map<String, CheckAndBuild> CheckAndBuildImplMap;
    public static void main(String[] args) {
        ac = SpringApplication.run(DemoApplication.class, args);
        CheckAndBuildImplMap = ac.getBeansOfType(CheckAndBuild.class);
    }

}
