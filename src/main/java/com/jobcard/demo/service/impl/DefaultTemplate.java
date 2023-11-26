package com.jobcard.demo.service.impl;

import cn.hutool.json.JSONUtil;
import com.jobcard.demo.bean.DefaultTemplateBean;
import com.jobcard.demo.service.CheckAndBuild;
import com.jobcard.demo.util.LuaJUtils;
import lombok.extern.slf4j.Slf4j;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
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
            File file = ResourceUtils.getFile("classpath:\\lua\\test.lua");
            Globals globals = LuaJUtils.loadScriptFromFile(file.getAbsolutePath());
            LuaJUtils.callFunction(globals,"hello",null);
            LuaJUtils.callFunction(globals,"test", LuaValue.valueOf("测试字符串传参"));
            LuaTable luaTable = new LuaTable();
            luaTable.set(1,"测试数组传参");
            LuaJUtils.callFunction(globals,"test2",luaTable);
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
