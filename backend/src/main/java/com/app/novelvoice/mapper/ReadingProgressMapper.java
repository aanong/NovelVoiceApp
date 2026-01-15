package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.ReadingProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 阅读进度 Mapper 接口
 */
@Mapper
public interface ReadingProgressMapper {
    
    /**
     * 插入阅读进度
     */
    int insert(ReadingProgress progress);
    
    /**
     * 更新阅读进度
     */
    int update(ReadingProgress progress);
    
    /**
     * 根据用户ID和小说ID查询进度
     */
    ReadingProgress selectByUserAndNovel(@Param("userId") Long userId, @Param("novelId") Long novelId);
    
    /**
     * 查询用户的所有阅读进度（阅读历史）
     */
    List<ReadingProgress> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 插入或更新阅读进度
     */
    int insertOrUpdate(ReadingProgress progress);
}
