package cn.lisynet.lisyweb.handlers;

import cn.hutool.core.util.XmlUtil;
import cn.lisynet.lisyweb.service.UserService;
import cn.lisynet.lisyweb.service.WalletLogService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathConstants;
import java.sql.SQLException;
import java.util.Objects;


/**
 * 处理微信主动发送过来的信息
 * @author lisy
 * @date 2023/8/20 10:40
 */
public class WechatApiHandler implements Handler {
    private final WalletLogService walletLogService = WalletLogService.getInstance(UserService.getInstance());


    @Override
    public void handle(@NotNull Context ctx) throws SQLException {
        String body = ctx.body();
        Document docResult= XmlUtil.readXML(body);

        Object msgType = XmlUtil.getByXPath("//MsgType", docResult, XPathConstants.STRING);
        String msgTypeStr = Objects.toString(msgType, "");

        Object fromUser = XmlUtil.getByXPath("//FromUserName", docResult, XPathConstants.STRING);
        String fromUserStr = Objects.toString(fromUser, "");

        Object toUser = XmlUtil.getByXPath("//ToUserName", docResult, XPathConstants.STRING);
        String toUserStr = Objects.toString(toUser, "");

        Object xmlContent = XmlUtil.getByXPath("//Content", docResult, XPathConstants.STRING);
        String xmlContentStr = Objects.toString(xmlContent, "");

        String eventStr = Objects.toString(XmlUtil.getByXPath("//Event", docResult, XPathConstants.STRING),"");

        long now = System.currentTimeMillis() / 1000;

        if ("text".equals(msgTypeStr)) {
            walletLogService.addWalletLog(fromUserStr,xmlContentStr);
            String content = "接收到信息:" + xmlContentStr;
            ctx.result(createTextMsg(toUserStr,fromUserStr,content,now));
        } else {
            if ("event".equals(msgTypeStr) && "subscribe".equals(eventStr)) {
                ctx.result(createTextMsg(toUserStr,fromUserStr,"欢迎订阅，本系统仅用于测试",now));
            } else {
                ctx.result(createTextMsg(toUserStr,fromUserStr,"不支持消息处理",now));
            }
        }
    }



    /**
     * 生成微信需要的xml消息
     * @param fromUser    发送方
     * @param toUser      接收方
     * @param content     内容
     * @param createTime  时间
     * @return            xml内容
     */
    private String createTextMsg(String fromUser,String toUser,String content,Long createTime){
        return "<xml>\n" +
                "  <ToUserName><![CDATA[" + toUser + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + fromUser + "]]></FromUserName>\n" +
                "  <CreateTime>" + createTime + "</CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA[" + content + "]]></Content>\n" +
                "</xml>";
    }
}
