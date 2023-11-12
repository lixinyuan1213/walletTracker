package cn.lisynet.lisyweb.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.lisynet.lisyweb.config.Constant;
import cn.lisynet.lisyweb.model.UserModel;
import cn.lisynet.lisyweb.utils.DbUtils;
import cn.lisynet.lisyweb.utils.WechatTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 账单记录操作类
 * @author lisy
 * @date 2023/11/11 22:36
 */
@Slf4j
public class WalletLogService {
    private static volatile WalletLogService instance;
    private final Connection connection;

    private final UserService userService;

    private WalletLogService(UserService userService) {
        try {
            connection = DbUtils.getConnection();
            this.userService = userService;
        } catch (Exception e) {
            log.error("数据库连接异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成单例
     * @param userService 用户信息相关
     * @return 单例
     */
    public static WalletLogService getInstance(UserService userService) {
        if (instance == null) {
            synchronized (WalletLogService.class) {
                if (instance == null) {
                    instance = new WalletLogService(userService);
                }
            }
        }
        return instance;
    }

    /**
     * 增加收支记录
     *
     * @param openId         微信的开放id
     * @param replyOpenid    回复给谁
     * @param command        指令：“收入50”，“支出1”
     */
    public String addWalletLog(String openId,String replyOpenid,String command) throws SQLException {
        if (StrUtil.isBlank(command)) {
            log.info("没有指令");
            return WechatTools.createTextMsg(replyOpenid,openId,"没有指令，无需处理");
        }
        String operation = StrUtil.sub(command, 0, 2);
        if (Constant.BUSINESS_MSG_IO_EXPENSES.equals(operation)) {
            BigDecimal amount = getAmount(command);
            if (Objects.isNull(amount)) {
                return WechatTools.createTextMsg(replyOpenid,openId,"金额错误");
            }
            addExpenses(openId, amount);
            return WechatTools.createTextMsg(replyOpenid,openId,"创造消费"+amount);
        } else if (Constant.BUSINESS_MSG_IO_INCOME.equals(operation)) {
            BigDecimal amount = getAmount(command);
            if (Objects.isNull(amount)) {
                return WechatTools.createTextMsg(replyOpenid,openId,"金额异常");
            }
            addIncome(openId,amount);
            return WechatTools.createTextMsg(replyOpenid,openId,"距离财富自由的目标更近了，创造财富"+amount);
        } else {
            log.info("无法处理指令");
            return WechatTools.createTextMsg(replyOpenid,openId,"无法处理指令");
        }
    }

    public String walletStatistics(String openId,String replyOpenid,String command) throws SQLException {
        if (StrUtil.isBlank(command)) {
            log.info("没有指令");
            return WechatTools.createTextMsg(replyOpenid,openId,"没有指令，无需处理");
        }
        // 截取”统计“指令
        String operation = StrUtil.sub(command, 0, 2);
        if (!Constant.STATISTICS_TYPE_COMMAND.equals(operation)) {
            return WechatTools.createTextMsg(replyOpenid,openId,"不是统计指令");
        }
        Date[] dates = parsingStatisticsPeriod(command);
        if (dates == null) {
            return WechatTools.createTextMsg(replyOpenid,openId,"无法解析查询日期，请输入‘统计今天’、‘统计今年’、‘统计本月’、‘统计本周/统计这周’、‘统计上周’、‘统计2311（代表统计23年11月）’、‘统计23012302（代表统计23年01月至23年02月）’");
        }
        UserModel userInfo = userOperation(openId);
        String income = statisticsIncome(dates[0], dates[1], userInfo.getId());
        String expenses = statisticsExpenses(dates[0], dates[1], userInfo.getId());
        return WechatTools.createTextMsg(replyOpenid,openId,"您查询的时间段内（"+DateUtil.format(dates[0], DatePattern.NORM_DATETIME_MINUTE_PATTERN)+"-"+DateUtil.format(dates[1],DatePattern.NORM_DATETIME_MINUTE_PATTERN)+"）：收入"+income+",支出"+expenses);
    }

    /**
     * 从指令中获取金额
     *
     * @param command 指令：“收入50”，“支出1”
     * @return {@link BigDecimal}
     */
    private BigDecimal getAmount(String command) {
        try {
            String sub = StrUtil.sub(command, 2, command.length());
            return new BigDecimal(sub);
        } catch (Exception e) {
            log.error("无法解析数字", e);
            return null;
        }
    }

    /**
     * 增加收入记录
     *
     * @param openId       微信的开放id
     * @param amount       金额
     */
    private void addIncome(String openId, BigDecimal amount) throws SQLException {
        UserModel userInfo = userOperation(openId);
        QueryRunner runner = new QueryRunner();
        Date now = new Date();
        String insertSql = "insert into wallet_log(amount,io_type,user_id,create_time,update_time) values(?,?,?,?,?)";
        runner.execute(connection,insertSql,amount, Constant.IO_TYPE_INCOME,userInfo.getId(),now,now);
    }

    /**
     * 增加支出记录
     *
     * @param openId       微信的开放id
     * @param amount       金额
     */
    private void addExpenses(String openId, BigDecimal amount) throws SQLException {
        UserModel userInfo = userOperation(openId);
        QueryRunner runner = new QueryRunner();
        Date now = new Date();
        String insertSql = "insert into wallet_log(amount,io_type,user_id,create_time,update_time) values(?,?,?,?,?)";
        runner.execute(connection,insertSql,amount, Constant.IO_TYPE_EXPENSES,userInfo.getId(),now,now);
    }

    /**
     * 获取用户信息
     * @param openId  微信开放id
     * @return        用户信息
     * @throws SQLException SQLException
     */
    private UserModel userOperation(String openId) throws SQLException {
        UserModel userInfo = userService.getUserByOpenId(openId);
        if (Objects.isNull(userInfo)) {
            userService.addUser(openId);
            userInfo = userService.getUserByOpenId(openId);
        }
        return userInfo;
    }

    /**
     * 统计收入
     *
     * @param start  开始时间
     * @param end    终止时间
     * @param userId 用户id
     * @return {@link String}
     * @throws SQLException SQLException
     */
    private String statisticsIncome(Date start, Date end, Long userId) throws SQLException {
        MapHandler mapHandler = new MapHandler();
        QueryRunner runner = new QueryRunner();
        String statisticsSql = "select sum(amount) as amount from wallet_log where io_type=2 and user_id=? and del_flag=0 and create_time>=? and create_time<=?";
        Map<String, Object> query = runner.query(connection, statisticsSql, mapHandler, userId, start, end);
        String amount = Objects.toString(query.get("amount"),"");
        if (StrUtil.isEmpty(amount)) {
            return "0";
        } else {
            return amount;
        }
    }

    /**
     * 统计支出
     *
     * @param start  开始
     * @param end    终止
     * @param userId 用户id
     * @return {@link String}
     * @throws SQLException SQLException
     */
    private String statisticsExpenses(Date start, Date end, Long userId) throws SQLException {
        MapHandler mapHandler = new MapHandler();
        QueryRunner runner = new QueryRunner();
        String statisticsSql2 = "select sum(amount) as amount from wallet_log where io_type=1 and user_id=? and del_flag=0 and create_time>=? and create_time<=?";
        Map<String, Object> rs = runner.query(connection, statisticsSql2, mapHandler, userId, start, end);
        String amount = Objects.toString(rs.get("amount"),"");
        if (StrUtil.isEmpty(amount)) {
            return "0";
        } else {
            return amount;
        }
    }

    /**
     * 根据字符串解析统计时段
     *
     * @param command 命令：如“统计今天”，“统计今年”，“统计这个月”、‘统计2311（代表统计23年11月）’、‘统计23012302（代表统计23年01月至23年02月）’
     * @return {@link Date[]}
     */
    private Date[] parsingStatisticsPeriod(String command) {
        // 从第三个字符开始截取（就是去掉“统计”）
        String period = StrUtil.subWithLength(command, 2,command.length());
        Date begin = null;
        Date end = null;
        Date now = new Date();
        if (Objects.equals(period, Constant.STATISTICS_TYPE_TODAY)) {
            // 统计今天
            begin = DateUtil.beginOfDay(now);
            end = DateUtil.endOfDay(now);
        } else if (Objects.equals(period, Constant.STATISTICS_TYPE_YEAR)) {
            // 统计今年
            begin = DateUtil.beginOfYear(now);
            end = DateUtil.endOfYear(now);
        } else if (Objects.equals(period, Constant.STATISTICS_TYPE_MONTH)) {
            // 统计这个月
            begin = DateUtil.beginOfMonth(now);
            end = DateUtil.endOfMonth(now);
        } else if (Objects.equals(period, Constant.STATISTICS_TYPE_WEEK) || Objects.equals(period, Constant.STATISTICS_TYPE_THIS_WEEK))  {
            // 统计本周
            begin = DateUtil.beginOfWeek(now);
            end = DateUtil.endOfWeek(now);
        } else if (Objects.equals(period, Constant.STATISTICS_TYPE_LAST_WEEK))  {
            // 统计上周
            DateTime dateTime = DateUtil.lastWeek();
            begin = DateUtil.beginOfWeek(dateTime);
            end = DateUtil.endOfWeek(dateTime);
        } else if (period.length() == 4) {
            // 处理如”统计2312“
            Date date = parsingDateStr(period);
            begin = DateUtil.beginOfMonth(date);
            end = DateUtil.endOfMonth(date);
        } else if (period.length() == 8) {
            // 处理如”统计23112312“
            String startDateStr = StrUtil.subWithLength(period, 0,4);
            String endDateStr = StrUtil.subWithLength(period, 4,period.length());
            begin = DateUtil.beginOfMonth(parsingDateStr(startDateStr));
            end = DateUtil.endOfMonth(parsingDateStr(endDateStr));
        } else {
            return null;
        }
        // 返回数组
        Date[] rs = new Date[2];
        rs[0] = begin;
        rs[1] = end;
        return rs;
    }

    /**
     * 将字符串类型解析为时间类型
     * @param date 字符串类型的日期
     * @return     时间类型的日期
     */
    private Date parsingDateStr(String date) {
        // 处理如”统计2312“
        String year = StrUtil.subWithLength(date, 0,2);
        year = "20"+year;
        String month = StrUtil.subWithLength(date, 2,4);
        String dateStr = year + month + "01";
        return DateUtil.parse(dateStr);
    }
}
