package com.jobcard.demo.service.impl;

import cn.hutool.json.JSONUtil;
import com.jobcard.demo.bean.DefaultTemplateBean;
import com.jobcard.demo.service.CheckAndBuild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefaultTemplate implements CheckAndBuild {
    @Override
    public List<Map<String, String>> checkParam(String message) {
        try {
            List<DefaultTemplateBean> defaultTemplateBeans = JSONUtil.toList(message, DefaultTemplateBean.class);
            if (CollectionUtils.isEmpty(defaultTemplateBeans)) {
                throw new RuntimeException("参数不能为空");
            }
            for (DefaultTemplateBean defaultTemplateBean : defaultTemplateBeans) {
                defaultTemplateBean.check();
            }
            String pJsonStr = JSONUtil.toJsonStr(defaultTemplateBeans);
            List<Map> maps = JSONUtil.toList(pJsonStr, Map.class);
            List<Map<String, String>> params = maps.stream().map(o -> {
                Map<String, String> m = new HashMap<>();
                o.keySet().forEach(key -> {
                    String ks = key.toString();
                    m.put(ks, o.get(ks).toString());
                });
                return m;
            }).collect(Collectors.toList());
            return params;
        } catch (Exception e) {
            log.error("参数解析异常_入参：{}，Exception->", message, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getStyleData(String templateType) {
        return null;
    }

    @Override
    public String getTemplateId() {
        return "defaultTemplateId";
    }
}
