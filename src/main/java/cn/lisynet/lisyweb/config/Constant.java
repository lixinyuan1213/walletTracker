package cn.lisynet.lisyweb.config;

/**
 * @author lisy
 * @date 2023/11/11 22:29
 */

/**
 * 常量
 */
public class Constant {
    /**
     * 收入指令
     */
    public static final String BUSINESS_MSG_IO_INCOME = "收入";
    /**
     * 支出指令
     */
    public static final String BUSINESS_MSG_IO_EXPENSES = "支出";
    /**
     * 支出
     */
    public static final Integer IO_TYPE_EXPENSES = 1;
    /**
     * 收入
     */
    public static final Integer IO_TYPE_INCOME = 2;

    /**
     * 消息类型-收入类型
     */
    public static final Integer MSG_TYPE_IO = 1;
    /**
     * 消息类型-统计类型
     */
    public static final Integer MSG_TYPE_STATISTICS = 2;
    /**
     * 消息类型-其他类型
     */
    public static final Integer MSG_TYPE_OTHER = -1;

    /**
     * 触发统计的指令
     */
    public static final String STATISTICS_TYPE_COMMAND = "统计";

    /**
     * 统计类型-今天
     */
    public static final String STATISTICS_TYPE_TODAY = "今天";
    /**
     * 统计类型-今年
     */
    public static final String STATISTICS_TYPE_YEAR = "今年";
    /**
     * 统计类型-这个月
     */
    public static final String STATISTICS_TYPE_MONTH = "本月";
    /**
     * 统计类型-本周
     */
    public static final String STATISTICS_TYPE_WEEK = "本周";
    /**
     * 统计类型-这周
     */
    public static final String STATISTICS_TYPE_THIS_WEEK = "这周";
    /**
     * 统计类型-上周
     */
    public static final String STATISTICS_TYPE_LAST_WEEK = "上周";
}
