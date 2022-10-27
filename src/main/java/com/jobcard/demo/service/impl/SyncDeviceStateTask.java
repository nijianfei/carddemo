package com.jobcard.demo.service.impl;

import com.jobcard.demo.bean.DeviceState;
import com.jobcard.demo.common.DeviceManage;
import com.jobcard.demo.enums.DeviceStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SyncDeviceStateTask {
    private final int lDevice = 179;


    /**
     * 按照标准时间来算，每隔 2s 执行一次
     */
    @Scheduled(cron = "0/2 * * * * ?")
    public void update() {
        if (DeviceManage.deviceState.keySet().size() == 0 && !DeviceManage.isWord() && !DeviceManage.isInit() && !DeviceManage.isIsClean() && DeviceManage.taskQueueCurrent.size() == 0 && DeviceManage.taskQueueWait.size() == 0) {
            DeviceManage.initDevice();
        }
//        } else {
//            long count = DeviceManage.deviceState.values().stream().filter(o -> Objects.equals(DeviceStateEnum.FREE, o.getStateEnum())).count();
//            if (DeviceManage.isWord() && DeviceManage.deviceState.keySet().size() == count) {
//                DeviceManage.initDevice();
//            }
//        }

        Set<Integer> deviceNos = DeviceManage.deviceState.keySet();
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
                        deviceState.setLastCardNo(null);
                    }
                    //卡号不为0，代表设备上有卡片 并且 卡号和设备上次处理的卡号不相同，设置设备为就绪状态
                    if (!Objects.equals(cardId, "0") && !Objects.equals(cardId, lastCardNo)) {
                        boolean b = DeviceManage.pushReadyQueue(deviceState.getRd());
                        deviceState.setStateEnum(DeviceStateEnum.READY);
//                        DeviceManage.deviceState.put(deviceNo, deviceState);
                    }
                    break;
            }
            log.info("\r\n设备号:{},状态:{} -> {}", deviceNo, sourceState, DeviceManage.deviceState.get(deviceNo).getStateEnum().getValue());
        }
        List<String> userId = DeviceManage.taskQueueWait.stream().map(t -> t.getParam().get("userId")).collect(Collectors.toList());
        List<String> userIdCurrent = DeviceManage.taskQueueCurrent.stream().map(t -> t.getParam().get("userId")).collect(Collectors.toList());
        log.info("\n\r\n任务状态:{},设备数量:{}，设备号:{}，\r\n 【待处理队列】剩余数:{},UserIdS:{}，\r\n 处理队列】剩余数:{}，UserIdS:{}\r\n", DeviceManage.isWord(), deviceNos.size(), getDeviceState(deviceNos), DeviceManage.taskQueueWait.size(), userId, DeviceManage.taskQueueCurrent.size(), userIdCurrent);
    }

    private Object getDeviceState(Set<Integer> deviceNos) {
        return deviceNos.stream().map(deviceNo -> String.join("-", String.valueOf(deviceNo), DeviceManage.deviceState.get(deviceNo).getStateEnum().getValue(), DeviceManage.deviceState.get(deviceNo).getStateEnum().getDetailMsg(), "LastCardNo:", DeviceManage.deviceState.get(deviceNo).getLastCardNo())).collect(Collectors.joining(","));
    }
}
