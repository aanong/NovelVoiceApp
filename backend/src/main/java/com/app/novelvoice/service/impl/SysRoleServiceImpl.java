package com.app.novelvoice.service.impl;

import com.app.novelvoice.entity.SysRole;
import com.app.novelvoice.entity.SysRoleMenu;
import com.app.novelvoice.mapper.SysRoleMapper;
import com.app.novelvoice.mapper.SysRoleMenuMapper;
import com.app.novelvoice.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<SysRole> selectRoleList() {
        return roleMapper.selectAll();
    }

    @Override
    public SysRole selectRoleById(Long roleId) {
        return roleMapper.selectById(roleId);
    }

    @Override
    @Transactional
    public int insertRole(SysRole role) {
        int rows = roleMapper.insert(role);
        insertRoleMenu(role);
        return rows;
    }

    @Override
    @Transactional
    public int updateRole(SysRole role) {
        roleMenuMapper.deleteByRoleId(role.getId());
        insertRoleMenu(role);
        return roleMapper.update(role);
    }

    @Override
    @Transactional
    public int deleteRoleById(Long roleId) {
        roleMenuMapper.deleteByRoleId(roleId);
        return roleMapper.deleteById(roleId);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    public void insertRoleMenu(SysRole role) {
        if (role.getMenuIds() != null && role.getMenuIds().length > 0) {
            List<SysRoleMenu> list = new ArrayList<>();
            for (Long menuId : role.getMenuIds()) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(role.getId());
                rm.setMenuId(menuId);
                list.add(rm);
            }
            if (list.size() > 0) {
                roleMenuMapper.batchInsert(list);
            }
        }
    }
}
