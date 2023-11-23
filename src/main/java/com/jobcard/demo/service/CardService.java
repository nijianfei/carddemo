package com.jobcard.demo.service;

import com.jobcard.demo.bean.TaskBean;

import java.util.List;

public interface CardService {
    /**
     * 根据人员信息，制作电子卡
     *
     * @param cardInfos 卡信息
     * @return
     */
    void make(List<TaskBean> cardInfos) throws InterruptedException;
}
