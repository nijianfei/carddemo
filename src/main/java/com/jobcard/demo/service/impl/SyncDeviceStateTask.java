package com.jobcard.demo.service.impl;

import com.jobcard.demo.bean.DeviceState;
import com.jobcard.demo.common.DeviceManage;
import com.jobcard.demo.enums.DeviceStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SyncDeviceStateTask {
    @Value("${syncLogShowRate}")
    private  int syncLogShowRate ;
    private static int syncLogShowRateTtatic = 1 ;


    /**
     * 按照标准时间来算，每隔 2s 执行一次
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void update() {
        if (DeviceManage.deviceState.keySet().size() == 0 && !DeviceManage.isWord() && !DeviceManage.isInit() && !DeviceManage.isIsClean() && DeviceManage.taskQueueCurrent.size() == 0 && DeviceManage.taskQueueWait.size() == 0) {
            DeviceManage.initDevice();
        } else if (DeviceManage.isWord() && DeviceManage.taskQueueWait.size() != 0 && DeviceManage.deviceState.keySet().size() == DeviceManage.deviceState.values().stream().filter(o -> Objects.equals(DeviceStateEnum.FREE, o.getStateEnum())).count()) {
            DeviceManage.initDevice();
        }

        Set<Integer> deviceNos = DeviceManage.deviceState.keySet();
        StringBuilder sb = new StringBuilder();
        for (Integer deviceNo : deviceNos) {
            if (DeviceManage.isInit()) {
                break;
            }
            if (DeviceManage.isIsClean()) {
                break;
            }
            DeviceState deviceState = DeviceManage.deviceState.get(deviceNo);
            if (Objects.isNull(deviceState)) {
                break;
            }
            String sourceState = deviceState.getStateEnum().getValue();
            String lastCardNo = deviceState.getLastCardNo();
            switch (deviceState.getStateEnum()) {
                case SUCC:
                case FAIL:
                case FREE:
                case READY:
                    String cardId = deviceState.getRd().readCardId();
                    if (Objects.equals(cardId, "0")) {//卡号为0，代表设备上无卡片，设置设备为空闲状态
                        deviceState.setStateEnum(DeviceStateEnum.FREE);
                        DeviceManage.readyQueue.remove(deviceState.getRd());
                        deviceState.setLastCardNo(null);
                    }
                    //卡号不为0，代表设备上有卡片 并且 卡号和设备上次处理的卡号不相同，设置设备为就绪状态
                    if (!Objects.equals(cardId, "0") && !Objects.equals(cardId, lastCardNo)) {
                        DeviceManage.pushReadyQueue(deviceState.getRd());
                        deviceState.setStateEnum(DeviceStateEnum.READY);
                    }
                    break;
            }
            sb.append(String.format("\r\n设备号:%s,状态:%s -> %s", deviceNo, sourceState, deviceState.getStateEnum().getValue()));
        }
        List<String> userId = DeviceManage.taskQueueWait.stream().map(t -> t.getParam().get("userId")).collect(Collectors.toList());
        List<String> userIdCurrent = DeviceManage.taskQueueCurrent.stream().map(t -> t.getParam().get("userId")).collect(Collectors.toList());
        if (syncLogShowRateTtatic -- <= 0) {
            log.info(sb.toString());
            log.info("\r\n\r\n任务状态:{},设备数量:{}，设备号:{}，socket连接数：{}\r\n 【待处理队列】剩余数:{},UserIdS:{}，\r\n 处理队列】剩余数:{}，UserIdS:{}\r\n", DeviceManage.isWord(), deviceNos.size(), getDeviceState(deviceNos),WebSocket.webSocketMap.size(), DeviceManage.taskQueueWait.size(), userId, DeviceManage.taskQueueCurrent.size(), userIdCurrent);
            log.info("\r\n\r\ndeviceState.size():{},isWord():{},isInit():{},isIsClean()：{}，taskQueueCurrent.size()：{}，taskQueueWait.size():{}，WebSocket.webSocketMap.size():{}",
                    DeviceManage.deviceState.keySet().size(), DeviceManage.isWord(), DeviceManage.isInit(), DeviceManage.isIsClean(), DeviceManage.taskQueueCurrent.size(), DeviceManage.taskQueueWait.size());
            syncLogShowRateTtatic = syncLogShowRate;
        }

    }

    private Object getDeviceState(Set<Integer> deviceNos) {
        return deviceNos.stream().map(deviceNo -> String.join("-", String.valueOf(deviceNo), DeviceManage.deviceState.get(deviceNo).getStateEnum().getValue(), DeviceManage.deviceState.get(deviceNo).getStateEnum().getDetailMsg(), "LastCardNo:", DeviceManage.deviceState.get(deviceNo).getLastCardNo())).collect(Collectors.joining(","));
    }
}
