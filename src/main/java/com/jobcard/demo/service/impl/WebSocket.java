package com.jobcard.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jobcard.demo.DemoApplication;
import com.jobcard.demo.bean.SoketResultVo;
import com.jobcard.demo.bean.TaskBean;
import com.jobcard.demo.common.DeviceManage;
import com.jobcard.demo.enums.TaskStateEnum;
import com.jobcard.demo.service.CheckAndBuild;
import com.jobcard.demo.util.TemplateSelectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Slf4j
@ServerEndpoint(value = "/webSocket")
@Component
public class WebSocket {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
     */
    private static int onlineCount = 0;
    private static Integer currentHashCode = 0;
    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的WebSocket对象
     */
    public static ConcurrentSkipListMap<Integer, WebSocket> webSocketMap = new ConcurrentSkipListMap<>();
    public static ConcurrentHashMap<Integer, Long> threadMap = new ConcurrentHashMap<>();
    private Integer hashCode = 0;
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
        log.info("START---来自客户端用户：{} 消息:{}", hashCode, message);
        try {
            //check参数
            List<Map<String, String>> params = checkParams(message);
            if (CollectionUtils.isEmpty(params)) {
                return;
            }
            CheckAndBuild visitorTypeCls = TemplateSelectUtil.getInstansByCode(params.get(0).get("visitorTypeCls"));
            List<Map<String, String>> maps = visitorTypeCls.checkParam(message);
            ThreadPoolTaskExecutor threadPoolTaskExecutor = DemoApplication.ac.getBean("threadPoolTaskExecutor", ThreadPoolTaskExecutor.class);
            threadPoolTaskExecutor.submit(() -> executeTask(maps,session.hashCode()));
            log.info("END---来自客户端用户：{} 消息:{}", hashCode, message);
        } catch (Exception e) {
            DeviceManage.setWord(false);
            log.error("cardService.make-->", e);
            SoketResultVo soketResultVo = new SoketResultVo(null, TaskStateEnum.FAIL.getCode(), e.getMessage());
            sendMessage(JSONUtil.toJsonStr(Arrays.asList(soketResultVo)));
        }
    }

    private void executeTask(List<Map<String, String>> maps, final int sessionHashCode) {
        long time = new Date().getTime();
        Integer tHashCode = Thread.currentThread().hashCode();
        threadMap.put(tHashCode, time);
        //制卡
        CardServiceImpl cardService = DemoApplication.ac.getBean(CardServiceImpl.class);
        int tryMaxCount = 10;
        while (tryMaxCount-- > 0) {
            Long maxLong = threadMap.values().stream().max(Comparator.comparing(Long::longValue)).get();
            log.info("当前线程时间戳：{}，threadMap最大时间戳：{},对比结果：{}，threadMapSize：{}", time, maxLong, time < maxLong, threadMap.size());
            if (time < maxLong) {
                break;
            }
            if (DeviceManage.tryStartTask(true)) {
                log.info("tHashCode：{}-开始任务_isWord:{}，isClean：{}，isInit：{}", tHashCode, DeviceManage.isWord(), DeviceManage.isIsClean(), DeviceManage.isInit());
                List<TaskBean> taskList = maps.stream().map(m -> new TaskBean(sessionHashCode, m)).collect(Collectors.toList());
                cardService.make(taskList);
                break;
            }
            DeviceManage.sleep(2000);
        }
        threadMap.remove(tHashCode);
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
        WebSocket.currentHashCode = this.hashCode;
        log.info("WebSocket_onOpen_Session_HashCode:{}", hashCode);
        //加入map
        webSocketMap.put(session.hashCode(), this);
        addOnlineCount();           //在线数加1
//        sendMessage(JSONUtil.toJsonStr(ResultUtil.getResultVo(ResultEnum.SUCCESS)));
    }

    /**
     * 连接关闭调用的方法任务状态
     */
    @OnClose
    public void onClose() {
        int sessionHashCode = this.session.hashCode();
        webSocketMap.remove(sessionHashCode);
        //在线数减1
        subOnlineCount();
        int tryMaxCount = 10;
        while (tryMaxCount-- > 0) {
            if (webSocketMap.size() != 0) {
                break;
            }
            if (DeviceManage.tryStartTask(false)) {
                DeviceManage.setIsClean(false);
                break;
            }
            log.info("客户端{}关闭连接！连接数：{},尝试清空任务/设备状态", sessionHashCode, onlineCount);
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

    public static boolean isTryStart() {
        return threadMap.size() > 1;
    }

    public Integer getHashCode() {
        return hashCode;
    }

    public static Integer getCurrentHashCode() {
        return currentHashCode;
    }
}
