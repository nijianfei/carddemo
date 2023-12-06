package com.jobcard.demo.service.impl;

import com.jobcard.demo.bean.DeviceState;
import com.jobcard.demo.common.DeviceManage;
import com.jobcard.demo.enums.DeviceStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SyncDeviceStateTask {
    @Value("${syncLogShowRate}")
    private int syncLogShowRate;
    @Value("${expectDeviceQty}")
    private int expectDeviceQty = 5;
    private static int syncLogShowRateTtatic = 1;
    private int initDeviceQty = -1;


    /**
     * 按照标准时间来算，每隔 2s 执行一次
     */
    @Scheduled(cron = "0/2 * * * * ?")
    public void update() {
        if (!DeviceManage.getSetSynStatus(true)) {
            return;
        }
        while (!DeviceManage.deviceState.keySet().contains(180)) {
            initDevice();
            DeviceManage.sleep(500);
        }
        if (!DeviceManage.isWord()) {
            initDevice();
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
            String cardId = "0";
            switch (deviceState.getStateEnum()) {
                case SUCC:
                case FAIL:
                case FREE:
                case READY:
                case EXCEPTION:
                    cardId = deviceState.getRd().readCardId();
                    if (Objects.equals(cardId, "-1")) {//卡号为-1，读卡异常
                        deviceState.setStateEnum(DeviceStateEnum.EXCEPTION);
                    }
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
            sb.append(String.format("\r\n设备号:%s,状态:%s(L %s) -> %s(%s)", deviceNo, sourceState, deviceState.getLastCardNo(), deviceState.getStateEnum().getValue(), cardId));
        }
        List<String> userId = DeviceManage.taskQueueWait.stream().map(t -> t.getParam().get("userId")).collect(Collectors.toList());
        List<String> userIdCurrent = DeviceManage.taskQueueCurrent.stream().map(t -> t.getParam().get("userId")).collect(Collectors.toList());
        if (syncLogShowRateTtatic-- <= 0) {
            log.info(sb.toString());
            log.info("\r\n\r\n任务状态:{}   设备数量:{}   设备号:{}    socket连接数：{}\r\n 【待处理队列】剩余数:{}  UserIdS:{}\r\n 【处理队列】剩余数:{} UserIdS:{}\r\n", DeviceManage.isWord(), deviceNos.size(), getDeviceState(deviceNos), WebSocket.webSocketMap.size(), DeviceManage.taskQueueWait.size(), userId, DeviceManage.taskQueueCurrent.size(), userIdCurrent);
            log.info("\r\n\r\ndeviceState.size():{} isWord():{} isInit():{} isIsClean()：{}  taskQueueCurrent.size()：{}  taskQueueWait.size():{} WebSocket.webSocketMap.size():{}", DeviceManage.deviceState.keySet().size(), DeviceManage.isWord(), DeviceManage.isInit(), DeviceManage.isIsClean(), DeviceManage.taskQueueCurrent.size(), DeviceManage.taskQueueWait.size(), WebSocket.webSocketMap.size());
            syncLogShowRateTtatic = syncLogShowRate;
        }
        DeviceManage.setSynStatus(false);
    }

    private void initDevice() {
        if (!DeviceManage.isWord() && !DeviceManage.isInit() && !DeviceManage.isIsClean() && DeviceManage.taskQueueCurrent.size() == 0 && DeviceManage.taskQueueWait.size() == 0) {
            DeviceManage.initDevice(expectDeviceQty);
            if (initDeviceQty <= 0) {
                initDeviceQty = DeviceManage.deviceState.keySet().size();
            }
        } else if (DeviceManage.isWord() && DeviceManage.taskQueueWait.size() != 0 && DeviceManage.deviceState.keySet().size() == DeviceManage.deviceState.values().stream().filter(o -> Objects.equals(DeviceStateEnum.FREE, o.getStateEnum())).count()) {
            DeviceManage.initDevice(expectDeviceQty);
        } else if (!DeviceManage.isInit() && !DeviceManage.isIsClean() && DeviceManage.deviceState.size() != initDeviceQty) {
            DeviceManage.initDevice(expectDeviceQty);
        }
    }

    private Object getDeviceState(Set<Integer> deviceNos) {
        return deviceNos.stream().map(deviceNo -> String.join("-", String.valueOf(deviceNo), getDeviceStateByDeviceNo(deviceNo), "LastCardNo:", getLastCardNo(deviceNo))).collect(Collectors.joining(","));
    }

    private String getDeviceStateByDeviceNo(Integer deviceNo) {
        DeviceState deviceState = DeviceManage.deviceState.get(deviceNo);
        if (Objects.nonNull(deviceState)) {
            DeviceStateEnum stateEnum = deviceState.getStateEnum();
            if (Objects.nonNull(stateEnum)) {
                return stateEnum.getValue() + "-" + stateEnum.getDetailMsg();
            }
        }
        return "";
    }

    private String getLastCardNo(Integer deviceNo) {
        DeviceState deviceState = DeviceManage.deviceState.get(deviceNo);
        if (Objects.nonNull(deviceState)) {
            return deviceState.getLastCardNo();
        }
        return "";
    }
}
