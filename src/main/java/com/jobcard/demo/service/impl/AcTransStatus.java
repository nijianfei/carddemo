package com.jobcard.demo.service.impl;

import com.jobcard.demo.common.DeviceManage;
import com.jobcard.demo.enums.DeviceStateEnum;
import com.jobcard.demo.util.CardReader;
import dcrf.JavaRD800;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AcTransStatus implements CardReader.TransStatus {
    private final JavaRD800 rd;
    private String cardNum;
    private boolean isSuccess = false;
    private String message = null;

    public AcTransStatus(JavaRD800 rd) {
        this.rd = rd;
    }

    @Override
    public void notifyMessage(String message) {
        log.debug("AcTransStatus_notifyMessage_卡号：{}，写卡信息：{}" ,cardNum, message);
        this.message = message;
        DeviceManage.deviceState.get(rd.getlDevice()).setMsg(message);
    }

    @Override
    public void progress(double progress) {
        log.debug("AcTransStatus_progress_卡号：{}，写卡进度：{}" ,cardNum, progress);
    }

    @Override
    public void finish(boolean success) {
        log.info("AcTransStatus_progress_卡号：{}，写卡结果：{}" ,cardNum, success);
        this.isSuccess = success;
        if (success) {
            DeviceManage.deviceState.get(rd.getlDevice()).setStateEnum(DeviceStateEnum.SUCC);
            DeviceManage.deviceState.get(rd.getlDevice()).setLastCardNo(cardNum);
        } else {
            DeviceManage.deviceState.get(rd.getlDevice()).setStateEnum(DeviceStateEnum.FAIL);
        }
    }

    @Override
    public void getCardId(String num) {
        log.info("AcTransStatus_progress_设备号：{}，获取到卡号：{}" ,rd.getlDevice(),cardNum);
        this.cardNum = num;
        DeviceManage.deviceState.get(rd.getlDevice()).setLastCardNo(num);
    }

    @Override
    public boolean isSuccess() {
        return this.isSuccess;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
