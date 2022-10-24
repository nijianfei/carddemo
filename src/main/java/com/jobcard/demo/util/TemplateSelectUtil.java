package com.jobcard.demo.util;

import com.jobcard.demo.DemoApplication;
import com.jobcard.demo.service.CheckAndBuild;

import java.util.Objects;

public class TemplateSelectUtil {
    private static final String DEFAULT_TEMPLATE_ID = "defaultTemplate";

    public static CheckAndBuild getInstansByCode(String tCode) {
        CheckAndBuild checkAndBuild = DemoApplication.CheckAndBuildImplMap.get(tCode);
        if (Objects.isNull(checkAndBuild)) {
            return DemoApplication.CheckAndBuildImplMap.get(DEFAULT_TEMPLATE_ID);
        }
        return checkAndBuild;
    }
}
