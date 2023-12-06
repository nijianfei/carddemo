package com.jobcard.demo.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.jobcard.demo.service.CheckAndBuild;
import com.jobcard.demo.util.LuaJUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.*;

@Slf4j
@Service
public class DefaultTemplate implements CheckAndBuild {
    @Override
    public List<Map<String, String>> checkParam(String message) {
        try {
            File luaFile = getLuaFile();
            log.debug("lua path:{}",luaFile.getAbsolutePath());
            Globals globals = LuaJUtils.loadScriptFromFile(luaFile.getAbsolutePath());
            List<Map> param = JSONArray.parseArray(message,Map.class);
            List<Map<String,String>> checkedParam = new ArrayList<>(param.size());
            for (Map map : param) {
                LuaTable luaTable = new LuaTable();
                for (Object key : map.keySet()) {
                    Object value = map.get(key);
                    value = Objects.nonNull(value) ? value : "";
                    luaTable.set(String.valueOf(key),value.toString());
                }
                LuaValue luaValue = LuaJUtils.callFunction(globals,"check",luaTable);
                CoerceLuaToJava.coerce(luaValue, Map.class);
                Map<String,String> pMap = new HashMap<>();
                for (LuaValue key : luaValue.checktable().keys()) {
                    LuaValue value = luaValue.checktable().get(key);
                    System.out.println("key:" + key.toString() + "  value:" +value.toString());
                    pMap.put(String.valueOf(key),String.valueOf(value));
                }
                checkedParam.add(pMap);
            }

            log.info("{}原参数：{} {}CHECK后参数：{}",System.lineSeparator(), JSONObject.toJSONString(param),System.lineSeparator(),JSONObject.toJSONString(checkedParam));
            for (Map<String, String> pMap : checkedParam) {
                String errorMsg = pMap.get("errorMsg");
                if (StringUtils.isNotBlank(errorMsg)) {
                    log.error("写卡信息缺少必填信息userId：{}", JSONObject.toJSONString(pMap, JSONWriter.Feature.PrettyFormat));
                    throw new RuntimeException(errorMsg);
                }
            }

            return checkedParam;
        } catch (Exception e) {
            log.error("参数解析异常_入参：{}，Exception->", message, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private File getLuaFile() {
        URL resource = Class.class.getClass().getResource("/");
        File path = Objects.nonNull(resource)?new File(resource.getPath()):new File(System.getProperty("user.dir"));
        log.debug("当前环境lua path : {}",path.getAbsolutePath());
        File file = new File(path,"lua\\params_check.lua");
        if (!file.exists()) {
            log.error("lua file not in : {}",file.getAbsoluteFile());
            throw new RuntimeException("not exist :" + file.getAbsoluteFile());
        }
        return file;
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
