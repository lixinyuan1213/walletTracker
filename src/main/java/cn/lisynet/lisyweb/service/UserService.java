package cn.lisynet.lisyweb.service;

import cn.hutool.core.util.StrUtil;
import cn.lisynet.lisyweb.config.BusinessException;
import cn.lisynet.lisyweb.model.UserModel;
import cn.lisynet.lisyweb.utils.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

/**
 * 用户操作相关
 * @author lisy
 * @date 2023/11/11 22:47
 */
@Slf4j
public class UserService {
    private static volatile UserService instance;
    private final Connection connection;

    private UserService() {
        try {
            connection = DbUtils.getConnection();
        } catch (Exception e) {
            log.error("数据库连接异常", e);
            throw new RuntimeException(e);
        }
    }

    private static class SingletonHelper {
        private static final UserService INSTANCE = new UserService();
    }
    public static UserService getInstance() {
        return SingletonHelper.INSTANCE;
    }


    /**
     * 添加用户
     *
     * @param openId 微信的开放id
     */
    public void addUser(String openId) throws SQLException {
        checkOpenId(openId);
        UserModel userInfo = getUserByOpenId(openId);
        if (userInfo != null) {
            log.info("用户信息已经存在，不做处理");
            return;
        }

        Date now = new Date();

        QueryRunner runner = new QueryRunner();
        String insertSql = "insert into user(openid,last_login,create_time,update_time) values(?,?,?,?)";
        runner.execute(connection,insertSql,openId,now,now,now);
    }

    public UserModel getUserByOpenId(String openId) throws SQLException {
        checkOpenId(openId);
        QueryRunner runner = new QueryRunner();
        BeanHandler<UserModel> beanHandler = new BeanHandler<>(UserModel.class);
        String sql = "select "+getSelectField()+" from user where openid = ?";
        return runner.query(connection,sql,beanHandler,openId);
    }

    /**
     * 判断openid不能为空
     * @param openId 微信开放id
     */
    private void checkOpenId(String openId) {
        if (StrUtil.isBlank(openId)) {
            throw BusinessException.error("无法获取微信身份");
        }
    }

    /**
     * 获取查询的字段
     *
     * @return {@link String}
     */
    private String getSelectField() {
        return "id,user_name as userName,openid,nick_name as nickName,email,last_login as lastLogin,create_time as createTime,update_time as updateTime";
    }
}
