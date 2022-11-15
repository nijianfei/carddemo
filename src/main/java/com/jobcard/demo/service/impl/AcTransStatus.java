package com.jobcard.demo.service.impl;

import com.jobcard.demo.bean.DeviceState;
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
    private DeviceState deviceState = null;
    private int rate = 10;
    private int rate2 = 50;
    private int count1 = 1;
    private int count2 = 1;

    public AcTransStatus(JavaRD800 rd) {
        this.rd = rd;
        this.deviceState = DeviceManage.deviceState.get(rd.getlDevice());
    }

    @Override
    public void notifyMessage(String message) {
        if (count1++ % rate == 0) {
            log.debug("AcTransStatus_notifyMessage_卡号：{}，lDevice:{}，写卡信息：{}", cardNum, rd.getlDevice(), message);
        }
        this.message = message;
        deviceState.setMsg(message);
    }

    @Override
    public void progress(double progress) {
        if (count2++ % rate2 == 0) {
            log.debug("AcTransStatus_progress_卡号：{}，lDevice:{}，写卡进度：{}", cardNum, rd.getlDevice(), progress);
        }
    }

    @Override
    public void finish(boolean success) {
        log.info("AcTransStatus_finish_卡号：{}，lDevice:{}，写卡结果：{}", cardNum, rd.getlDevice(), success);
        count1 = 2;
        count2 = 1;
        this.isSuccess = success;
        if (success) {
            deviceState.setStateEnum(DeviceStateEnum.SUCC);
        } else {
            deviceState.setStateEnum(DeviceStateEnum.FAIL);
        }
    }

    @Override
    public void getCardId(String num) {
        this.cardNum = num;
        log.info("AcTransStatus_progress_设备号：{}，获取到卡号：{}", rd.getlDevice(), cardNum);
        deviceState.setLastCardNo(num);
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
