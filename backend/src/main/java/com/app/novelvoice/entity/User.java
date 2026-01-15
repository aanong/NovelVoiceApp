package com.app.novelvoice.entity;

import com.app.novelvoice.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 登录Token
     */
    private String token;
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
}
