package com.jobcard.demo;

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
//        try {
//            BufferedImage read = ImageIO.read(new File("C:\\Users\\njf\\Desktop\\背景new.jpg"));
//            File file = new File("C:\\Users\\njf\\Desktop\\背景new111.jpg");
//            ImageIO.write(read, "jpeg", file);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

}
