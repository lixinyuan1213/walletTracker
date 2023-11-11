package cn.lisynet.lisyweb.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

/**
 * 默认返回格式为 text/html; charset=utf-8
 * @author lisy
 * @date 2023/8/20 11:17
 */
public class CommonHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx){
        ctx.contentType("text/html; charset=utf-8");
    }
}
