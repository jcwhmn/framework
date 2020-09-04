package com.yangzhou.frame.mapper;

import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.yangzhou.frame.domain.Dictionary;
import com.yangzhou.mapper.BaseEntityMapper;

public interface DictionaryMapper extends BaseEntityMapper<Dictionary> {
  @Select("select type from sys_dictionary_type")
  Set<String> getAllTypes();

  @Select("select value from sys_dictionary where type = #{type} order by seq")
  List<String> getDictionarieValuesOfType(@Param("type") String type);
}
