package com.app.novelvoice.service;

import com.app.novelvoice.entity.SysRole;
import java.util.List;

public interface SysRoleService {
    List<SysRole> selectRoleList();

    SysRole selectRoleById(Long roleId);

    int insertRole(SysRole role);

    int updateRole(SysRole role);

    int deleteRoleById(Long roleId);

    List<SysRole> selectRolesByUserId(Long userId);
}
