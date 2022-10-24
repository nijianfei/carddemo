package com.jobcard.demo.util;

import com.jobcard.demo.bean.ResultVo;
import com.jobcard.demo.enums.ResultEnum;

public class ResultUtil {
    public static ResultVo getResultVo(ResultEnum resultEnum) {
        return new ResultVo(resultEnum.getCode(), resultEnum.getMsg());
    }
}
