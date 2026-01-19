package com.app.novelvoice.service;

import com.app.novelvoice.entity.SysMenu;
import java.util.List;
import java.util.Set;

public interface SysMenuService {
    List<SysMenu> selectMenuList(Long userId);

    List<SysMenu> selectMenuTreeByUserId(Long userId);

    SysMenu selectMenuById(Long menuId);

    int insertMenu(SysMenu menu);

    int updateMenu(SysMenu menu);

    int deleteMenuById(Long menuId);

    Set<String> selectMenuPermsByUserId(Long userId);
}
