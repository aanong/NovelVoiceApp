package com.app.novelvoice.entity;

import com.app.novelvoice.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleKey;

    /**
     * 显示顺序
     */
    private Integer roleSort;

    /**
     * 角色状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单组
     */
    private Long[] menuIds;
}
