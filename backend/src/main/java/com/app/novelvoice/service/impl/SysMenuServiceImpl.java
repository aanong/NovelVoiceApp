package com.app.novelvoice.service.impl;

import com.app.novelvoice.entity.SysMenu;
import com.app.novelvoice.mapper.SysMenuMapper;
import com.app.novelvoice.mapper.SysRoleMenuMapper;
import com.app.novelvoice.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Override
    public List<SysMenu> selectMenuList(Long userId) {
        return selectMenuList(new SysMenu(), userId);
    }

    public List<SysMenu> selectMenuList(SysMenu menu, Long userId) {
        List<SysMenu> menuList = null;
        // Admin usually has all access, but let's just query by user for now.
        // If userId is admin (e.g., 1), maybe return all?
        if (userId != null && userId == 1L) {
            menuList = menuMapper.selectAll();
        } else {
            menuList = menuMapper.selectMenusByUserId(userId);
        }
        return menuList;
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = null;
        if (userId != null && userId == 1L) {
            menus = menuMapper.selectAll();
        } else {
            menus = menuMapper.selectMenusByUserId(userId);
        }
        return getChildPerms(menus, 0);
    }

    @Override
    public SysMenu selectMenuById(Long menuId) {
        return menuMapper.selectById(menuId);
    }

    @Override
    public int insertMenu(SysMenu menu) {
        return menuMapper.insert(menu);
    }

    @Override
    public int updateMenu(SysMenu menu) {
        return menuMapper.update(menu);
    }

    @Override
    public int deleteMenuById(Long menuId) {
        // Should check children
        return menuMapper.deleteById(menuId);
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = menuMapper.selectPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (perm != null && !perm.isEmpty()) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * Build tree
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId) {
        List<SysMenu> returnList = new ArrayList<>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext();) {
            SysMenu t = (SysMenu) iterator.next();
            // Assuming root is 0
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    private void recursionFn(List<SysMenu> list, SysMenu t) {
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = (SysMenu) it.next();
            if (n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }
}
