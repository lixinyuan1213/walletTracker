package cn.lisynet.lisyweb.service;

import cn.hutool.core.util.StrUtil;
import cn.lisynet.lisyweb.config.Constant;
import cn.lisynet.lisyweb.model.UserModel;
import cn.lisynet.lisyweb.utils.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
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
     * @param command        指令：“收入50”，“支出1”
     */
    public void addWalletLog(String openId, String command) throws SQLException {
        if (StrUtil.isBlank(command)) {
            log.info("没有指令");
            return;
        }
        String operation = StrUtil.sub(command, 0, 2);
        if (Constant.BUSINESS_MSG_IO_EXPENSES.equals(operation)) {
            BigDecimal amount = getAmount(command);
            addExpenses(openId, amount);
        } else if (Constant.BUSINESS_MSG_IO_INCOME.equals(operation)) {
            BigDecimal amount = getAmount(command);
            addIncome(openId,amount);
        } else {
            log.info("无法处理指令");
        }
    }

    /**
     * 从指令中获取金额
     *
     * @param command 指令：“收入50”，“支出1”
     * @return {@link BigDecimal}
     */
    private BigDecimal getAmount(String command) {
        String sub = StrUtil.sub(command, 2, command.length());
        return new BigDecimal(sub);
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
}
