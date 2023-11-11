package cn.lisynet.lisyweb.handlers;

import cn.hutool.setting.Setting;
import cn.lisynet.lisyweb.config.SettingConfig;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;


/**
 * 处理微信code
 * @author lisy
 * @date 2023/8/20 10:40
 */
public class WechatCodeHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx){
        Setting config = SettingConfig.config;
        String appId = config.getStr("appId");
        String redirectUri = "http://notices.lisynet.cn/wechat/openid";
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="+redirectUri+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
        ctx.redirect(url);
    }
}
