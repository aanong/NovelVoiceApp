package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysUserRoleMapper {
    int insert(SysUserRole userRole);

    int deleteByUserId(Long userId);

    int deleteByRoleId(Long roleId);

    List<Long> selectRoleIdsByUserId(Long userId);

    int batchInsert(@Param("userRoles") List<SysUserRole> userRoles);
}
