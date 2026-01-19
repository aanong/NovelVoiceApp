package com.app.novelvoice.entity;

import lombok.Data;

/**
 * 用户和角色关联
 */
@Data
public class SysUserRole {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;
}
