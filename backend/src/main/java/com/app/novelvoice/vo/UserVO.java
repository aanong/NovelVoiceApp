package com.app.novelvoice.vo;

import lombok.Data;
import java.util.Date;

/**
 * 用户视图对象
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String token;
    private Date createTime;
    private Date lastLoginTime;
    
    /**
     * 是否在线（用于用户列表展示）
     */
    private Boolean online;
}
