package cn.lisynet.lisyweb.utils;

import cn.hutool.setting.Setting;
import cn.lisynet.lisyweb.config.SettingConfig;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 数据库工具类
 * @author lisy
 * @date 2023/11/11 22:52
 */
public class DbUtils {
    public static Connection getConnection() throws Exception{
        Setting config = SettingConfig.config;
        Class.forName(config.getStr("db_class_name"));
        String db = config.getStr("db_url");
        return DriverManager.getConnection(db);
    }
}
