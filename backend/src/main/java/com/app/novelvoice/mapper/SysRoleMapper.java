package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysRoleMapper {
    int insert(SysRole role);

    int update(SysRole role);

    int deleteById(Long id);

    SysRole selectById(Long id);

    List<SysRole> selectAll();

    List<SysRole> selectRolesByUserId(Long userId);
}
