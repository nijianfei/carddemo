package com.jobcard.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class EnvironmentBean {

    @Resource
    private Environment env;


    public String getValue(String configKey) {
        String configValue = env.getProperty(configKey);
        log.info("Environment 配置获取 {}", configKey);
        return configValue;
    }
}
