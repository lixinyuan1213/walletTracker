package cn.lisynet.lisyweb.handlers;

import cn.hutool.core.date.DateUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;


/**
 * home处理器（返回当前时间）
 * @author lisy
 * @date 2023/8/20 10:40
 */
public class HomeHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx){
        String now = DateUtil.now();
        ctx.result("当前时间："+now);
    }
}
