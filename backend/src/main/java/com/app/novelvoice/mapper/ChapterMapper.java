package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChapterMapper {
    int insert(Chapter chapter);

    List<Chapter> selectByNovelId(@Param("novelId") Long novelId);

    Chapter selectById(@Param("id") Long id);
}
