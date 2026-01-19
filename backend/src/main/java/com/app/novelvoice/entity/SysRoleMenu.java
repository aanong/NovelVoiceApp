package com.app.novelvoice.entity;

import lombok.Data;

/**
 * 角色和菜单关联
 */
@Data
public class SysRoleMenu {
    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;
}
