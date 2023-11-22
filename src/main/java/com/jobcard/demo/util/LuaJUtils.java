package com.jobcard.demo.util;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaJUtils {
    private LuaJUtils() {}

    /**
     * 加载lua脚本获取LuaJ 的Globals对象，注意Globals不是线程安全的
     * @version 1.0
     * @since jdk1.8
     * @date 2021/1/2
     * @param filePath lua脚本的路径
     * @throws
     * @return org.luaj.vm2.Globals
     */
    public static Globals loadScriptFromFile(String filePath) {
        Globals globals = JsePlatform.standardGlobals();
        globals.loadfile(filePath).call();
        return globals;
    }

    /**
     * 调用lua脚本内的函数，并返回LuaValue值,如果返回为空，返回一个Nil的对象
     * @version 1.0
     * @since jdk1.8
     * @date 2021/1/2
     * @param globals
     * @param funcName 函数名称
     * @param luaValue 参数值，LuaValue对象，可以表示为基本数据类型、数组、对象等
     * @throws
     * @return org.luaj.vm2.LuaValue
     */
    public static LuaValue callFunction(Globals globals, String funcName, LuaValue luaValue) {

        LuaValue func = globals.get(LuaValue.valueOf(funcName));
        LuaValue result = func.call(luaValue);
        return result;
    }

    /**
     * 结果对象luavalue 与java对象的转换
     * @version 1.0
     * @since jdk1.8
     * @date 2021/1/2
     * @param luaValue
     * @param tClass java对象泛型
     * @throws
     * @return T
     */
    public static <T> T fromResult(LuaValue luaValue, Class<T> tClass) {
        return (T) CoerceLuaToJava.coerce(luaValue, tClass);
    }
}
