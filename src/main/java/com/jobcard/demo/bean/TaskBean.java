package com.jobcard.demo.bean;

import com.jobcard.demo.enums.TaskStateEnum;

import java.util.Map;

public class TaskBean {
    private Integer sessionHashCode;
    private Map<String, String> param;

    private TaskStateEnum taskState = TaskStateEnum.READY;

    private String cardId;

    public TaskBean() {
    }

    public TaskBean(Integer sessionHashCode, Map<String, String> param) {
        this.sessionHashCode = sessionHashCode;
        this.param = param;
    }

    public Integer getSessionHashCode() {
        return sessionHashCode;
    }

    public void setSessionHashCode(Integer sessionHashCode) {
        this.sessionHashCode = sessionHashCode;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }

    public TaskStateEnum getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskStateEnum taskState) {
        this.taskState = taskState;
    }


    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }
}
