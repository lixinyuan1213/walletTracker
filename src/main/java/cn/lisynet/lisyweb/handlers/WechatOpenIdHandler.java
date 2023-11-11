package cn.lisynet.lisyweb.handlers;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
public class WechatOpenIdHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx){
        String code = ctx.queryParam("code");
        Setting config = SettingConfig.config;
        String appId = config.getStr("appId");
        String secret = config.getStr("secret");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
        String json = HttpUtil.get(url);
        JSONObject jsonObject = JSONUtil.parseObj(json);
        ctx.result(jsonObject.getStr("openid"));
    }
}
