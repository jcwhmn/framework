package com.yangzhou.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangzhou.domain.BaseEntityWithID;
import com.yangzhou.domain.StringPair;

/**
 * 继承Mybase plus BaseMapper,包含Mybatis Plus自带的所有方法，并进行自定义扩展
 *
 * @author Jiang Chuanwei
 *
 * @param <T> 所有继承了{@link com.yangzhou.domain.BaseEntityWithID}的类
 */
@Mapper
public interface BaseEntityMapper<T extends BaseEntityWithID> extends BaseMapper<T> {
  @Select("select count(1) from ${tableName} where ${columnName} = #{columnValue}")
  int count(@Param("tableName") String tableName, @Param("columnName") String columnName,
        @Param("columnValue") String columnValue);

  /**
   * 获取当前表最大ID
   *
   * @return 最大ID值
   */
  Long selectMaxId();

  /**
   * 获取指定表的Auto increment值，
   *
   * @param tableName
   * @return 表的当前Auto increment值，该值是该表下一个自动生成的ID值。
   */
  Long selectTableAutoIncrement(String tableName);

  @Select("select count(*) from information_schema.TABLES where table_name = #{tableName}")
  Long tableCount(@Param("tableName") String tableName);

  @Select("select 1 from ${tableName} where ${columnName} = #{columnValue} limit 1")
  Integer exist(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("columnValue") String columnValue);

  @Select("<script>select 1 from ${tableName} where <foreach collection = 'pairs' item = 'pair' separator = ' and '>${pair.key} = #{pair.value}</foreach> limit 1</script>")
  Integer existPair(@Param("tableName") String tableName, @Param("pairs") List<StringPair> pairs);
}
