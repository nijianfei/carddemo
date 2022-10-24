package com.jobcard.demo.bean;

/**
 * 卓越 2022/10/12 19:44:13
 * 【刷卡消息】
 * 	员工ID	userId
 * 	发卡状态	execStatusCls
 * 	批次卡号	cardId
 *
 * 卓越 2022/10/12 19:44:55
 * 发卡状态	execStatusCls：  0开始刷卡 1刷卡成功 2 刷卡失败
 */
public class SoketResultVo {
    /**
     * 工号
     */
    private String userId;
    /**
     * 卡号
     */
    private String cardId;
    /**
     * 0开始刷卡 1刷卡成功 2 刷卡失败
     */
    private String execStatusCls;
    /**
     * 信息
     */
    private String message;

    public SoketResultVo() {
    }

    public SoketResultVo(String userId, String cardId, String execStatusCls, String message) {
        this.userId = userId;
        this.cardId = cardId;
        this.execStatusCls = execStatusCls;
        this.message = message;
    }

    public SoketResultVo(DeviceState ds) {
        this.cardId = ds.getLastCardNo();
        this.userId = ds.getUserId();
        this.execStatusCls = ds.getStateEnum().getCode();
        this.message = ds.getMsg();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getExecStatusCls() {
        return execStatusCls;
    }

    public void setExecStatusCls(String execStatusCls) {
        this.execStatusCls = execStatusCls;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
