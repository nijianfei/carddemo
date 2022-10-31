package com.jobcard.demo.common;

import cn.hutool.json.JSONUtil;
import com.jobcard.demo.bean.DeviceState;
import com.jobcard.demo.bean.SoketResultVo;
import com.jobcard.demo.bean.TaskBean;
import com.jobcard.demo.enums.CoreCheckStateEnum;
import com.jobcard.demo.enums.DeviceStateEnum;
import com.jobcard.demo.enums.TaskStateEnum;
import com.jobcard.demo.service.impl.AcTransStatus;
import com.jobcard.demo.service.impl.CardServiceImpl;
import com.jobcard.demo.service.impl.WebSocket;
import com.jobcard.demo.util.CardReader;
import com.jobcard.demo.util.TemplateAdapter;
import dcrf.JavaRD800;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class DeviceManage {
    private static boolean isInit = false;
    private static boolean isClean = false;
    private static boolean isWord = false;
    /**
     * 任务等待队列
     */
    public static final LinkedBlockingDeque<TaskBean> taskQueueWait = new LinkedBlockingDeque<>();
    /**
     * 任务当前处理队列
     */
    public static final LinkedBlockingDeque<TaskBean> taskQueueCurrent = new LinkedBlockingDeque<>();
    /**
     * 所有设备的集合
     */
    public static final Map<Integer, DeviceState> deviceState = new ConcurrentHashMap<>();
    /**
     * 空闲设备队列
     */
    public static final LinkedBlockingDeque<JavaRD800> readyQueue = new LinkedBlockingDeque<>();
    /**
     * 记录制卡完成的 卡号：工号
     */
    public static final Map<String, String> finishCardMap = new ConcurrentHashMap<>();

    private static final int maxDeviceCount = 5;

    public static synchronized boolean isInit() {
        return isInit;
    }

    public static synchronized boolean SetInit(boolean init) {
        return isInit = init;
    }

    public static synchronized void initDevice() {
        SetInit(true);
        sleep(1000);
//        DeviceManage.deviceState.clear();
        for (int i = 0; i < maxDeviceCount; i++) {
            int deviceNo = 100 + i;
            JavaRD800 rd = new JavaRD800();
            int lDevice = rd.dc_init(deviceNo, 115200);
            if (lDevice <= 0) {
                System.out.println("打开读卡器端口失败!" + deviceNo);
                continue;
            } else {
                System.out.print(String.format("dc_init ok! %s\n", deviceNo));
            }
            if (rd.dc_reset(lDevice, 1) != 0) {
                System.out.print(String.format("dc_reset error! %s\n", deviceNo));
                rd.dc_exit(lDevice);
                continue;
            }
            System.out.print(String.format("dc_reset ok! %s\n", deviceNo));
            rd.setlDevice(lDevice);
            rd.setDeviceNo(deviceNo);
            DeviceState deviceState = new DeviceState();
            deviceState.setRd(rd);
            deviceState.setStateEnum(DeviceStateEnum.FREE);
            DeviceManage.deviceState.put(lDevice, deviceState);
        }
        SetInit(false);
    }

    public static void writeCard(JavaRD800 rd, TaskBean taskBean, CardServiceImpl cardServiceImpl) {
        Integer deviceNo = null;
        String cardId = null;
        Map<String, String> cardInfo = taskBean.getParam();
        String userId = cardInfo.get("userId");
        try {
            cardId = rd.readCardId();
            deviceNo = rd.getlDevice();
            taskBean.setCardId(cardId);
            log.info("获取可用设备：{},读取卡号：{}", deviceNo, cardId);
            DeviceManage.deviceState.get(deviceNo).setUserId(userId);
            taskBean.setTaskState(TaskStateEnum.BUSY);
            CoreCheckStateEnum coreCheckStateEnum = cardServiceImpl.checkUserIdAndCardId(cardId, userId);
            log.info("人卡校验参数：cardId:{},userId:{}，校验结果：{}", cardId, userId, coreCheckStateEnum);
            //通知开始写卡
            sendMsg(new SoketResultVo(taskBean, DeviceStateEnum.BUSY.getCode(), DeviceStateEnum.BUSY.getValue()));
            //可写卡
            if (coreCheckStateEnum.isUsable()) {
                CardReader cardReader = new CardReader(rd, new AcTransStatus(rd));
                TemplateAdapter adapter = TemplateAdapter.setTemplate(cardServiceImpl.getStyleData(cardInfo.get("visitorTypeCls")));
                //分辨率：240x416
                BufferedImage image = adapter.getImage(cardInfo);
                //设备写卡
                CardReader.TransStatus start = cardReader.start(image);
                if (start.isSuccess()) {
                    log.info("记录本次写卡任务已成功的卡号:{},工号:{}", cardReader.getCardNum(), cardInfo.get("userId"));
                    DeviceManage.finishCardMap.put(cardReader.getCardNum(), cardInfo.get("userId"));
                    taskBean.setTaskState(TaskStateEnum.SUCC);
                    //通知写卡成功
                    sendMsg(new SoketResultVo(taskBean, DeviceStateEnum.SUCC.getCode(), DeviceStateEnum.SUCC.getValue()));
                    return;
                } else {
                    DeviceState deviceState = DeviceManage.deviceState.get(deviceNo);
                    deviceState.setStateEnum(DeviceStateEnum.FAIL.setDetailMsg(start.getMessage()));
                    log.info("写卡异常,将失败任务重新加入任务队列:{}", JSONUtil.toJsonStr(taskBean));
                    taskBean.setTaskState(TaskStateEnum.FAIL);
                    sendMsg(new SoketResultVo(taskBean, DeviceStateEnum.FAIL.getCode(), DeviceStateEnum.FAIL.getValue()));
                }
            } else if (Objects.equals(CoreCheckStateEnum.S1.getCode(), coreCheckStateEnum.getCode())) {//卡已绑定，
                DeviceState deviceState = DeviceManage.deviceState.get(deviceNo);
                deviceState.setStateEnum(DeviceStateEnum.FAIL.setDetailMsg(CoreCheckStateEnum.S1.getName()));
                deviceState.setLastCardNo(cardId);
                log.info("写卡异常（卡已绑定）,将失败任务重新加入任务队列:{}", JSONUtil.toJsonStr(taskBean));
                taskBean.setTaskState(TaskStateEnum.FAIL);
                sendMsg(new SoketResultVo(taskBean, DeviceStateEnum.FAIL.getCode(), coreCheckStateEnum.getName()));
            } else if (Objects.equals(CoreCheckStateEnum.S2.getCode(), coreCheckStateEnum.getCode())) {//人已发卡,通知失败，跳过任务
                DeviceState deviceState = DeviceManage.deviceState.get(deviceNo);
                deviceState.setStateEnum(DeviceStateEnum.FAIL.setDetailMsg(CoreCheckStateEnum.S2.getName()));
                deviceState.setLastCardNo(cardId);
                taskBean.setTaskState(TaskStateEnum.FAIL);
                sendMsg(new SoketResultVo(taskBean, DeviceStateEnum.FAIL.getCode(), coreCheckStateEnum.getName()));
                return;
            } else if (Objects.equals(CoreCheckStateEnum.S3.getCode(), coreCheckStateEnum.getCode())) {//占用
                taskBean.setTaskState(TaskStateEnum.RETRY);
                DeviceState deviceState = DeviceManage.deviceState.get(deviceNo);
                deviceState.setStateEnum(DeviceStateEnum.FAIL.setDetailMsg(CoreCheckStateEnum.S3.getName()));
                deviceState.setLastCardNo(cardId);
                sendMsg(new SoketResultVo(taskBean, DeviceStateEnum.FAIL.getCode(), coreCheckStateEnum.getName()));
            }
        } catch (Exception e) {
            log.error("工号：{}，写卡异常-->", cardInfo.get("userId"), e);
            DeviceState deviceState = DeviceManage.deviceState.get(deviceNo);
            deviceState.setStateEnum(DeviceStateEnum.FAIL);
            taskBean.setTaskState(TaskStateEnum.FAIL);
            sendMsg(new SoketResultVo(taskBean, DeviceStateEnum.FAIL.getCode(), DeviceStateEnum.FAIL.getValue()));
        }
        if (!WebSocket.isTryStart()) {
            DeviceManage.taskQueueWait.addLast(taskBean);
        }

    }

    public static synchronized boolean tryStartTask(boolean setWord) {
        if (isWord || isClean || isInit) {
            log.info("DeviceManage_tryStartTask_isWord:{}，isClean：{}，isInit：{}", isWord, isClean, isInit);
            if (isClean || isInit) {
                return false;
            }
            taskQueueWait.clear();
            long wordCount = taskQueueCurrent.stream().filter(t -> TaskStateEnum.BUSY.equals(t.getTaskState())).count();
            if (wordCount == 0) {
                isClean = true;
                cleanState(setWord);
                log.info("DeviceManage_tryStartTask_wordCount:{},成功清空任务/设备状态,存在工作设备", wordCount);
                return true;
            }
            log.info("DeviceManage_tryStartTask_wordCount:{},未清空任务/设备状态,存在工作设备", wordCount);
            return false;
        }
        log.info("DeviceManage_tryStartTask_isWord:{}，工作状态为false，无需清空", isWord);
        DeviceManage.setWord(setWord);
        DeviceManage.setIsClean(true);
        return true;
    }

    public static synchronized boolean isWord() {
        return isWord;
    }

    public static synchronized void setWord(boolean isWord) {
        DeviceManage.isWord = isWord;
    }

    public static synchronized void cleanState() {
        cleanState(false);
    }

    public static synchronized void cleanState(boolean isWord) {
        taskQueueWait.clear();
        taskQueueCurrent.clear();
        readyQueue.clear();
        deviceState.clear();
        finishCardMap.clear();
        setWord(isWord);
        log.info("cleanState_清空任务/设备状态！\r\n taskQueueWait.size:{},taskQueueCurrent.size:{},readyQueue.size:{},deviceState.size:{},finishCardMap.size:{}"
                , taskQueueWait.size(), taskQueueCurrent.size(), readyQueue.size(), deviceState.size(), finishCardMap.size());
    }


    private static void sendMsg(SoketResultVo srVo) {
        Integer currentHashCode = WebSocket.getCurrentHashCode();
        if (currentHashCode.equals(srVo.sessionHashCode)) {
            WebSocket webSocket = WebSocket.webSocketMap.get(currentHashCode);
            log.info("【发送Socket通知信息】：{}", JSONUtil.toJsonStr(Arrays.asList(srVo)));
            webSocket.sendMessage(JSONUtil.toJsonStr(Arrays.asList(srVo)));
        }else{
            log.info("【发送Socket通知信息——源连接 currentHashCode：{}，消息sessionHashCode：{}】不一致，跳过通知！！！ 内容：{}",
                    currentHashCode, srVo.sessionHashCode, JSONUtil.toJsonStr(Arrays.asList(srVo)));
        }
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized static boolean pushReadyQueue(JavaRD800 rd) {
        if (!DeviceManage.readyQueue.contains(rd)) {
            DeviceManage.readyQueue.push(rd);
            return true;
        }
        return false;
    }

    public static boolean isIsClean() {
        return isClean;
    }

    public static void setIsClean(boolean isClean) {
        DeviceManage.isClean = isClean;
    }

}
