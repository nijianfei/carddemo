package com.jobcard.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
//@Component
public class SendMakeInfoTask {
    /**
     * 按照标准时间来算，每隔 2s 执行一次
     */
    @Scheduled(cron = "0/2 * * * * ?")
    public void syncState() {
//        log.info("【发送SoketMsg】开始执行：{}", DateUtil.formatDateTime(new Date()));
//        Collection<DeviceState> deviceStates = DeviceManage.deviceState.values();
//        List<SoketResultVo> srList = deviceStates.stream().map(o -> new SoketResultVo(o)).filter(sr -> !DeviceStateEnum.READY.getCode().equals(sr.getExecStatusCls()))
//                .filter(sr -> Objects.isNull(DeviceManage.noticedUserIdMap.get(sr.getUserId() + sr.getExecStatusCls()))).collect(Collectors.toList());
//        Optional<WebSocket> first = WebSocket.webSocketMap.values().stream().findFirst();
//        first.ifPresent(ws -> {
//            if (!CollectionUtils.isEmpty(srList)) {
//                log.info("【发送SoketMsg】开始执行：{}", JSONUtil.toJsonStr(srList));
//                ws.sendMessage(JSONUtil.toJsonStr(srList));
//                srList.forEach(s -> {
//                    DeviceManage.noticedUserIdMap.put(s.getUserId() + s.getExecStatusCls(), s.getExecStatusCls());
//                });
//            }
//        });
//        log.info("【发送SoketMsg】执行结束：{},msgSize:{}", DateUtil.formatDateTime(new Date()), srList.size());
    }
}
