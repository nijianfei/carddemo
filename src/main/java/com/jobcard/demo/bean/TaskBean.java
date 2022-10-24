package com.jobcard.demo.bean;

import com.jobcard.demo.enums.TaskStateEnum;

import java.util.Map;

public class TaskBean {
    private Integer hashCode;
    private Map<String,String> param;

    private TaskStateEnum taskState = TaskStateEnum.READY;

    public TaskBean() {
    }

    public TaskBean(Integer hashCode, Map<String, String> param) {
        this.hashCode = hashCode;
        this.param = param;
    }

    public Integer getHashCode() {
        return hashCode;
    }

    public void setHashCode(Integer hashCode) {
        this.hashCode = hashCode;
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
}
