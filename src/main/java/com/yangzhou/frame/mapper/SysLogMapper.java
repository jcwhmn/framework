package com.yangzhou.frame.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangzhou.frame.domain.SysLog;

@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {

  @Select("select * from sys_log limit 0,20")
  List<SysLog> selectTest();
}