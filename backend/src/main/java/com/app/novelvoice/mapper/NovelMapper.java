package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.Novel;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface NovelMapper {
    int insert(Novel novel);

    List<Novel> selectAll();

    Novel selectById(Long id);

    int deleteById(Long id);
}
