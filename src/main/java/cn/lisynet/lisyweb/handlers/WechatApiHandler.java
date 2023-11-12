package cn.lisynet.lisyweb.handlers;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.lisynet.lisyweb.config.Constant;
import cn.lisynet.lisyweb.service.UserService;
import cn.lisynet.lisyweb.service.WalletLogService;
import cn.lisynet.lisyweb.utils.WechatTools;
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

        if ("text".equals(msgTypeStr)) {
            Integer commandType = parsingMsgType(xmlContentStr);
            String rs;
            if (Objects.equals(commandType, Constant.MSG_TYPE_IO)) {
                rs = walletLogService.addWalletLog(fromUserStr, toUserStr, xmlContentStr);
            } else if (Objects.equals(commandType,Constant.MSG_TYPE_STATISTICS)) {
                rs = walletLogService.walletStatistics(fromUserStr, toUserStr, xmlContentStr);
            } else {
                rs = WechatTools.createTextMsg(toUserStr,fromUserStr,"消息类型不支持");
            }
            ctx.result(rs);
        } else {
            if ("event".equals(msgTypeStr) && "subscribe".equals(eventStr)) {
                ctx.result(WechatTools.createTextMsg(toUserStr,fromUserStr,"欢迎订阅"));
            } else {
                ctx.result(WechatTools.createTextMsg(toUserStr,fromUserStr,"不支持消息处理"));
            }
        }
    }

    /**
     * 分析消息类型
     *
     * @param command 命令
     * @return {@link Integer}
     */
    private Integer parsingMsgType(String command) {
        // 截取前两个字符，判断是什么类型的操作，比如”收入“、”支出“、”统计“
        String operation = StrUtil.sub(command, 0, 2);
        if (Constant.BUSINESS_MSG_IO_EXPENSES.equals(operation) || Constant.BUSINESS_MSG_IO_INCOME.equals(operation)) {
            return Constant.MSG_TYPE_IO;
        } else if (Constant.STATISTICS_TYPE_COMMAND.equals(operation)) {
            return Constant.MSG_TYPE_STATISTICS;
        } else {
            return Constant.MSG_TYPE_OTHER;
        }
    }
}
