package com.jobcard.demo.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.beust.jcommander.internal.Maps;
import com.jobcard.demo.DemoApplication;
import com.jobcard.demo.bean.CoreResultBean;
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

import java.util.HashMap;
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
    @Value("${expectDeviceQty}")
    private int expectDeviceQty;

    /**
     * 根据人员信息，制作电子卡
     *
     * @param cardInfos 卡信息
     * @return
     */
    public void make(List<TaskBean> cardInfos) {
        log.info("CardServiceImpl_make_cardInfos.size:{},JSON:{}", cardInfos.size(), JSONUtil.toJsonStr(cardInfos));
        ThreadPoolTaskExecutor threadPoolTaskExecutor = DemoApplication.ac.getBean("threadPoolTaskExecutor", ThreadPoolTaskExecutor.class);
        DeviceManage.initDevice(expectDeviceQty);
        log.info("将任务加入队列，任务队列数：{}", DeviceManage.taskQueueWait.size());
        cardInfos.forEach(c -> DeviceManage.taskQueueWait.addLast(c));
        TaskBean taskBean = null;
        JavaRD800 rd = null;
        int count = 0;
        DeviceManage.setIsClean(false);
        while (true) {
            try {
                if (DeviceManage.taskQueueWait.size() == 0 && DeviceManage.taskQueueCurrent.size() == 0) {
                    break;
                }
                if (Objects.isNull(rd = DeviceManage.readyQueue.pollFirst())) {
                    DeviceManage.sleep(500);
                    continue;
                }
                if (Objects.isNull((taskBean = DeviceManage.taskQueueWait.poll()))) {
                    DeviceManage.sleep(500);
                    continue;
                }
                DeviceManage.deviceState.get(rd.getlDevice()).setStateEnum(DeviceStateEnum.BUSY);
                if (WebSocket.isTryStart()) {
                    DeviceManage.taskQueueWait.clear();
                    DeviceManage.sleep(500);
                    continue;
                }
                taskBean.setTaskState(TaskStateEnum.BUSY);
                DeviceManage.taskQueueCurrent.add(taskBean);
                log.info("第{}次執行，写卡信息：{}", ++count, JSONUtil.toJsonStr(taskBean));

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

    public CoreCheckStateEnum checkUserIdAndCardId(String cardId, String userId,String buildingId) {
        if (isCheck) {
            Map<String, String> strMap = Maps.newHashMap("cardId", cardId, "userId", userId,"buildingId",buildingId);
            //调用中台查询人卡是否合法
            String paramsStr = null;
            try {
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("field", JSONUtil.toJsonStr(strMap));
                paramMap.put("userID", "engine");
                paramMap.put("sign", "1");
                paramMap.put("method", "32013-IF04");
                paramsStr = HttpUtil.toParams(paramMap);
                String post = HttpUtil.post(coreUrl, paramsStr, 5000);
                log.error("请求中台[{}]校验人卡信息_参数：{}，返回结果：{}", coreUrl, paramsStr, post);
                Object invokeCls = JSONUtil.parseObj(post).get("invokeCls");
                Object invokeRes = JSONUtil.parseObj(post).get("invokeRes");
                JSONObject entries = JSONUtil.parseObj(invokeRes);
                if ("70".equals(invokeCls)) {
                    CoreResultBean coreResultBean = JSONUtil.toBean(entries.toString(), CoreResultBean.class);
                    String resultCode = coreResultBean.getResult();
                    return CoreCheckStateEnum.getEnumByCode(resultCode);
                }
                return CoreCheckStateEnum.S99;
            } catch (Exception e) {
                log.error("请求中台[{}]校验人卡信息异常_参数：{}，异常堆栈信息：-->", coreUrl, paramsStr, e);
                return CoreCheckStateEnum.S98;
            }
        }
//        if (Objects.nonNull(DeviceManage.finishCardMap.get(cardId))) {
//            log.error("本次制卡任务中已存在此卡成功记录：{}", cardId);
//            return CoreCheckStateEnum.S1;
//        }
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
