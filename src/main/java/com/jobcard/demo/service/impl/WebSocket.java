package com.jobcard.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jobcard.demo.DemoApplication;
import com.jobcard.demo.bean.TaskBean;
import com.jobcard.demo.common.DeviceManage;
import com.jobcard.demo.service.CheckAndBuild;
import com.jobcard.demo.util.TemplateSelectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@ServerEndpoint(value = "/webSocket")
@Component
public class WebSocket {

    @Autowired
    private CardServiceImpl csi;
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
     */

    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的WebSocket对象
     */
    public static ConcurrentHashMap<Integer, WebSocket> webSocketMap = new ConcurrentHashMap<>();
    public static Integer hashCode;
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */

    private Session session;


    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("START-来自客户端用户：{} 消息:{}", hashCode, message);
        try {
            //check参数
            List<Map<String, String>> params = checkParams(message);
            if (CollectionUtils.isEmpty(params)) {
                return;
            }
            CheckAndBuild visitorTypeCls = TemplateSelectUtil.getInstansByCode(params.get(0).get("visitorTypeCls"));
            List<Map<String, String>> maps = visitorTypeCls.checkParam(message);
            //制卡
            CardServiceImpl cardService = DemoApplication.ac.getBean(CardServiceImpl.class);
            ThreadPoolTaskExecutor threadPoolTaskExecutor = DemoApplication.ac.getBean("threadPoolTaskExecutor", ThreadPoolTaskExecutor.class);
            int tryMaxCount = 10;
            while (tryMaxCount-- > 0) {
                if (DeviceManage.tryStartTask(true)) {
                    DeviceManage.sleep(2000);
                    DeviceManage.setWord(true);
                    List<TaskBean> taskList = maps.stream().map(m -> new TaskBean(hashCode, m)).collect(Collectors.toList());
                    threadPoolTaskExecutor.execute(()->cardService.make(taskList));
                    break;
                }
                DeviceManage.sleep(2000);
            }
            log.info("END-来自客户端用户：{} 消息:{}", hashCode, message);
        } catch (Exception e) {
            DeviceManage.setWord(false);
            log.error("cardService.make-->", e);
        }
    }

    /**
     * 向客户端发送消息
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
            //this.session.getAsyncRemote().sendText(message);
        } catch (IOException e) {
            log.error("sendMessage_IO异常-->", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        this.hashCode = session.hashCode();
        log.info("WebSocket_onOpen_Session_HashCode:{}", hashCode);
        //加入map
        webSocketMap.put(session.hashCode(), this);
        addOnlineCount();           //在线数加1
//        sendMessage(JSONUtil.toJsonStr(ResultUtil.getResultVo(ResultEnum.SUCCESS)));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        int sessionHashCode = this.session.hashCode();
        webSocketMap.remove(sessionHashCode);
        //在线数减1
        subOnlineCount();
        while (!DeviceManage.tryStartTask(false)) {
            log.info("客户端{}关闭连接！尝试清空任务/设备状态", sessionHashCode);
            DeviceManage.sleep(2000);
        }
        log.info("用户{}关闭连接！当前在线人数为{}", sessionHashCode, getOnlineCount());
    }

    /**
     * 发生错误时调用
     *
     * @OnError
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + hashCode + ",原因:" + error.getMessage());
        error.printStackTrace();
    }


    /**
     * 通过userId向客户端发送消息
     */
    public void sendMessageByUserId(String userId, String message) throws IOException {
        log.info("服务端发送消息到{},消息：{}", userId, message);
        if (StrUtil.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户{}不在线", userId);
        }

    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) throws IOException {
        for (Integer item : webSocketMap.keySet()) {
            webSocketMap.get(item).sendMessage(message);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }

    private List<Map<String, String>> checkParams(String message) {
        if (StringUtils.isNotBlank(message)) {
            try {
                List<Map> maps = JSONUtil.toList(message, Map.class);
                List<Map<String, String>> params = maps.stream().map(o -> {
                    Map<String, String> m = new HashMap<>();
                    o.keySet().forEach(key -> {
                        String ks = key.toString();
                        m.put(ks, o.get(ks).toString());
                    });
                    return m;
                }).collect(Collectors.toList());
                return params;
            } catch (Exception e) {
                log.error("参数解析异常_入参：{}，Exception->", message, e);
            }
        }
        return null;
    }
}
