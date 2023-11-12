package cn.lisynet.lisyweb;

import cn.lisynet.lisyweb.config.BusinessException;
import cn.lisynet.lisyweb.config.CommonResult;
import cn.lisynet.lisyweb.handlers.*;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lisy
 * @date 2023/8/20 9:30
 */
@Slf4j
public class App {

    public static void main(String[] args) {
        Javalin app = Javalin.create();
        app.before(new CommonHandler());

        // 自定义异常
        app.exception(BusinessException.class, (e, ctx) -> {
            String message = e.getErrMsg();
            CommonResult<String> error = CommonResult.error(message);
            log.error(message,e);
            ctx.json(error);
        });
        // 其他异常
        app.exception(Exception.class, (e, ctx) -> {
            String message = "系统异常";
            CommonResult<String> error = CommonResult.error(message);
            log.error(e.getMessage(),e);
            ctx.json(error);
        });

        // home
        app.get("/", new HomeHandler());
        // 发送微信通知消息
        app.post("/sendMsg", new WechatMessageHandler());
        // 处理微信接口消息
        app.post("/wechat/base", new WechatApiHandler());
        // 微信认证获取code
        app.get("/wechat/code", new WechatCodeHandler());
        // 微信认证获取openid
        app.get("/wechat/openid", new WechatOpenIdHandler());


        log.info("启动成功");
        app.start(37070);
    }
}
