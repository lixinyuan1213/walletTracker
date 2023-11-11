package cn.lisynet.lisyweb.model;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 用户模型
 * @author lisy
 * @date 2023/11/11 22:36
 */
@Data
public class UserModel {
    private Long id;
    private String userName;
    /**
     * 微信端唯一标识
     */
    private String openid;
    private String nickName;
    private String email;
    private Timestamp lastLogin;
    private Timestamp createTime;
    private Timestamp updateTime;
}
