package com.jobcard.demo.controller;

import cn.hutool.json.JSONUtil;
import com.jobcard.demo.bean.ResultVo;
import com.jobcard.demo.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Slf4j
//@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping("/make")
    public ResultVo make(@RequestBody List<Map<String, String>> cardInfos) {
        log.info("CardController_make_param:{}", JSONUtil.toJsonStr(cardInfos));
//        return cardService.make(cardInfos);
        return null;
    }
}
