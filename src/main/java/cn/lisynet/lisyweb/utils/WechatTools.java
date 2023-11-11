package cn.lisynet.lisyweb.utils;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import cn.lisynet.lisyweb.config.SettingConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lisy
 * @date 2023/8/30 17:59
 */
@Slf4j
final public class WechatTools {
    /**
     * 缓存工具类，过期时间5分钟
     */
    private static final TimedCache<String, String> TIMED_CACHE = CacheUtil.newTimedCache(300 * 1000);
    /**
     * 缓存key
     */
    private static final String CACHE_KEY = "wechat_token";

    /**
     * 微信appid
     */
    private static final String APP_ID;
    /**
     * 微信secret
     */
    private static final String SECRET;
    /**
     * 消息模板
     */
    private static final String TEMPLATE_ID;


    static {
        Setting config = SettingConfig.config;
        APP_ID = config.getStr("appId");
        SECRET = config.getStr("secret");
        TEMPLATE_ID = config.getStr("templateId");
    }

    /**
     * 发送微信消息
     * @param openId 用户微信的openId
     * @param msg   消息
     */
    public static void sendMessage(String openId,String msg){
        // 构建发送给微信的消息体
        String message2Wechat = "{\"touser\":\"" + openId + "\",\"template_id\":\"" + TEMPLATE_ID + "\",\"url\":\"\",\"topcolor\":\"#FF0000\",\"data\":{\"news\":{\"value\":\"" + msg + "\"}}}";
        // 获取token
        String token = getWechatToKen();
        // 发送请求给微信
        String rs = HttpRequest.post("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token).body(message2Wechat).execute().body();
        log.debug("发送微信消息，微信服务器的返回内容：{}",rs);

        JSONObject jsonObject = JSONUtil.parseObj(rs);
        Integer errCode = jsonObject.getInt("errcode");
        if (errCode == 0) {
            log.info("信息发送成功");
        } else {
            log.info("信息发送失败");
        }
    }
















    /**
     * 获取微信的token
     * @return 微信的token
     */
    private static String getWechatToKen(){
        String token = TIMED_CACHE.get(CACHE_KEY);
        if (StrUtil.isBlank(token)) {
            token = getWechatTokenFromServer();
            if (StrUtil.isNotBlank(token)) {
                TIMED_CACHE.put(CACHE_KEY, token);
                log.debug("通过接口拿到微信token，并缓存");
            }
        } else {
            log.debug("通过缓存拿到微信token");
        }
        return token;
    }

    /**
     * 调用微信接口获取token
     * @return token
     */
    private static String getWechatTokenFromServer(){
        String rs = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APP_ID + "&secret=" + SECRET);
        log.info("微信服务器返回的token:{}", rs);
        JSONObject jsonObject = JSONUtil.parseObj(rs);
        return jsonObject.getStr("access_token");
    }
}
