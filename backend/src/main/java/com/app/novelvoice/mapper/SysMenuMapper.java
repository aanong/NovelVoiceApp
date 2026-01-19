package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysMenuMapper {
    int insert(SysMenu menu);

    int update(SysMenu menu);

    int deleteById(Long id);

    SysMenu selectById(Long id);

    List<SysMenu> selectAll();

    List<String> selectPermsByUserId(Long userId);

    List<SysMenu> selectMenusByUserId(Long userId);
}
