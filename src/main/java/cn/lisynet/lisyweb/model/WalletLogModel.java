package cn.lisynet.lisyweb.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 账单记录模型
 * @author lisy
 * @date 2023/11/11 22:36
 */
@Data
public class WalletLogModel {
    private Long id;
    private BigDecimal amount;
    /**
     * 收支类型
     * @see cn.lisynet.lisyweb.config.Constant
     * IO_TYPE_EXPENSES 1 支出
     * IO_TYPE_INCOME   2 收入
     */
    private Integer ioType;
    private Long userId;
    private Integer delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
}
