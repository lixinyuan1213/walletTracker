package cn.lisynet.lisyweb.handlers;

import cn.lisynet.lisyweb.config.CommonResult;
import cn.lisynet.lisyweb.utils.WechatTools;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;


/**
 * @author lisy
 * @date 2023/8/30 17:56
 */
public class WechatMessageHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) {
        String openId = ctx.formParam("openId");
        String msg = ctx.formParam("msg");
        WechatTools.sendMessage(openId,msg);

        CommonResult<String> success = CommonResult.success("success");
        ctx.json(success);
    }
}
