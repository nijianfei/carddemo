package com.jobcard.demo.service;

import java.util.List;
import java.util.Map;

public interface CheckAndBuild {
    List<Map<String, String>> checkParam(String params);
    String getStyleData(String templateType);

    String getTemplateId();
}
