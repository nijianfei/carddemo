package com.jobcard.demo.bean;

import java.util.Map;
import java.util.Optional;

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
     * 姓名
     */
    private String userName;
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


    public Integer sessionHashCode;
    public SoketResultVo() {
    }

    public SoketResultVo(TaskBean taskBean, String execStatusCls, String message) {
        Optional.ofNullable(taskBean).ifPresent(t -> {
            Map<String, String> cardInfo = t.getParam();
            this.userId = cardInfo.get("userId");
            this.userName =cardInfo.get("name");
            this.cardId = t.getCardId();
            this.sessionHashCode = t.getSessionHashCode();
        });
        this.execStatusCls = execStatusCls;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
