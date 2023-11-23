package com.jobcard.demo;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jobcard.demo.bean.CustomBlock;
import com.jobcard.demo.service.CheckAndBuild;
import com.jobcard.demo.service.impl.CardServiceImpl;
import com.jobcard.demo.util.TemplateAdapter;
import org.glassfish.jersey.server.wadl.internal.WadlAutoDiscoverable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import sun.misc.BASE64Decoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
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
//            CardServiceImpl bean = ac.getBean(CardServiceImpl.class);
//            String styleData = bean.getStyleData("");
//            TemplateAdapter adapter = TemplateAdapter.setTemplate(styleData);
//            addBlock(adapter);
//            String content = getContent();
//            List<Map> maps = JSONUtil.toList(content, Map.class);
//            BufferedImage image = adapter.getImage(maps.get(0));
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static String getContent() {
        return "[{\n" +
                "  'visitDate': '2023-10-10 14:00',\n" +
                "  'intervieweesId': 'CSE111222',\n" +
                "  'intervieweesName': '刘军熹',\n" +
                "  'interviewees': 'CSC17636-鹿坦',\n" +
                "  'visitReason': '鹿坦邀请刘军熹一',\n" +
                "  'visitReason1': '行人来访中信',\n" +
                "  'userId': 'CSC123456',\n" +
                "  'name': '刘军熹',\n" +
                "  'buildingName': '中信大厦',\n" +
                "  'floorNames': '59F 76F-78F 79F-81F',\n" +
                "  'company': '北京奥普杰特科技发...',\n" +
                "  'depart': '信息技术部',\n" +
                "  'visitorTypeCls': '02'\n" +
                "}]";
    }

    private static void addBlock(TemplateAdapter adapter) {
//        CustomBlock customBlock1 = new CustomBlock();
//        customBlock1.setType(0);
//        customBlock1.setName("buildName");
//        customBlock1.setX(18);
//        customBlock1.setY(250);
//        customBlock1.setWidth(69);
//        customBlock1.setHeight(19);
//        customBlock1.setColor(0);//Color.red.getRGB()
//        customBlock1.setFont("微软雅黑");
//        customBlock1.setFontSize(10.75f);
//        customBlock1.setFontStyle(1);
//        customBlock1.setLayout(0);
//
//        CustomBlock customBlock2 = new CustomBlock();
//        customBlock2.setType(0);
//        customBlock2.setName("floorName");
//        customBlock2.setX(78);
//        customBlock2.setY(252);
//        customBlock2.setWidth(69);
//        customBlock2.setHeight(19);
//        customBlock2.setColor(Color.red.getRGB());//Color.red.getRGB()
//        customBlock2.setFont("隶书");
//        customBlock2.setFontSize(10.75f);
//        customBlock2.setFontStyle(1);
//        customBlock2.setLayout(0);
//
//        adapter.addBlock(customBlock1);
//        adapter.addBlock(customBlock2);
        System.out.println("\r\n"+JSONUtil.toJsonStr(adapter.getBlockList())+"\r\n");
    }

}
