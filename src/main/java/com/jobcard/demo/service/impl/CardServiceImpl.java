package com.jobcard.demo.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.beust.jcommander.internal.Maps;
import com.jobcard.demo.DemoApplication;
import com.jobcard.demo.bean.TaskBean;
import com.jobcard.demo.common.DeviceManage;
import com.jobcard.demo.enums.CoreCheckStateEnum;
import com.jobcard.demo.enums.DeviceStateEnum;
import com.jobcard.demo.enums.TaskStateEnum;
import com.jobcard.demo.service.CardService;
import dcrf.JavaRD800;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class CardServiceImpl implements CardService {
    @Value("#{${cardTemplateMap}}")
    private Map<String, String> cardTempletMap;
    @Value("#{${templetMappingMap}}")
    private Map<String, String> templetMappingMap;
    @Value("${coreUrl}")
    private String coreUrl;
    @Value("${isCheck}")
    private boolean isCheck;

    /**
     * 根据人员信息，制作电子卡
     *
     * @param cardInfos 卡信息
     * @return
     */
    @Override
    public void make(List<TaskBean> cardInfos) {
        log.info("CardServiceImpl_make_cardInfos.size:{},JSON:{}", cardInfos.size(), JSONUtil.toJsonStr(cardInfos));
        ThreadPoolTaskExecutor threadPoolTaskExecutor = DemoApplication.ac.getBean("threadPoolTaskExecutor", ThreadPoolTaskExecutor.class);
        DeviceManage.initDevice();
        log.info("将任务加入队列，任务队列数：{}", DeviceManage.taskQueueWait.size());
        cardInfos.forEach(c -> DeviceManage.taskQueueWait.addLast(c));
        TaskBean taskBean = null;
        JavaRD800 rd = null;
        int count = 0;
        while (true) {
            DeviceManage.sleep(1000);
            if (DeviceManage.taskQueueWait.size() == 0 && DeviceManage.taskQueueCurrent.size() == 0) {
                break;
            }
            DeviceManage.setIsClean(false);
            if (Objects.isNull(rd = DeviceManage.readyQueue.pollFirst())) {
                continue;
            }
            if (DeviceManage.taskQueueCurrent.size() != 0) {
                continue;
            }
            if (Objects.isNull((taskBean = DeviceManage.taskQueueWait.pollLast()))) {
                break;
            }

            taskBean.setTaskState(TaskStateEnum.BUSY);
            DeviceManage.taskQueueCurrent.add(taskBean);

            try {
                log.info("第{}次執行，写卡信息：{}", ++count, JSONUtil.toJsonStr(taskBean));
                DeviceManage.deviceState.get(rd.getDeviceNo()).setStateEnum(DeviceStateEnum.BUSY);
                final TaskBean tBean = taskBean;
                final JavaRD800 finalRd = rd;
                threadPoolTaskExecutor.submit(() -> {
                    try {
                        DeviceManage.writeCard(finalRd, tBean, this);
                    } finally {
                        DeviceManage.taskQueueCurrent.remove(tBean);
                    }
                });
            } catch (Exception e) {
                log.error("CardServiceImpl_make_Exception:-->", e);
                throw new RuntimeException(e);
            }
        }
        //清除任务状态
        if (!DeviceManage.isIsClean()) {
            DeviceManage.cleanState();
        }
    }

    public CoreCheckStateEnum checkUserIdAndCardId(String cardId, String userId) {
        if (isCheck) {
            Map<String, String> strMap = Maps.newHashMap("cardId", cardId, "userId", userId);
            //调用中台查询人卡是否合法
            try {
                String post = HttpUtil.post(coreUrl, JSONUtil.toJsonStr(strMap));
                log.error("请求中台校验人卡信息_参数：{}，返回结果：{}", JSONUtil.toJsonStr(strMap), post);
                String resultCode = String.valueOf(JSONUtil.parseObj(post).get("result"));
                return CoreCheckStateEnum.getEnumByCode(resultCode);
            } catch (Exception e) {
                log.error("请求中台校验人卡信息异常_参数：{}，异常堆栈信息：-->", JSONUtil.toJsonStr(strMap), e);
                return CoreCheckStateEnum.S98;
            }
        }
        if (Objects.nonNull(DeviceManage.finishCardMap.get(cardId))) {
            log.error("本次制卡任务中已存在此卡成功记录：{}", cardId);
            return CoreCheckStateEnum.S1;
        }
        return CoreCheckStateEnum.S_1;
    }

    public String getStyleData(String templateType) {
        if (StringUtils.isNotBlank(templateType)) {
            //添加映射关系
            return cardTempletMap.get("defaultTemplateId");
        } else {
            return cardTempletMap.get("defaultTemplateId");
        }
    }
}
