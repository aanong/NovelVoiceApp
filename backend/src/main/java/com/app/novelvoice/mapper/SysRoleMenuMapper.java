package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysRoleMenuMapper {
    int insert(SysRoleMenu roleMenu);

    int deleteByRoleId(Long roleId);

    int deleteByMenuId(Long menuId);

    List<Long> selectMenuIdsByRoleId(Long roleId);

    int batchInsert(@Param("roleMenus") List<SysRoleMenu> roleMenus);
}
