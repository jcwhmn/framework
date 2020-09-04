package com.yangzhou.frame.mapper;

import java.util.Set;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.FetchType;
import com.yangzhou.frame.domain.User;
import com.yangzhou.mapper.BaseEntityMapper;

@Mapper
public interface UserMapper extends BaseEntityMapper<User> {
  @Select("select * from sys_user where login = #{login} and is_delete = 1")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "login", property = "login"),
    @Result(column = "password", property = "password"),
    @Result(column = "name", property = "name"),
    @Result(column = "state", property = "state"),
        @Result(
              column = "login",
              property = "authorities",
              many = @Many(
                    select = "com.yangzhou.frame.mapper.AuthorityMapper.selectByUserLogin",
                    fetchType = FetchType.EAGER))
  })
  User getUserWithAuthoritiesByLogin(String login);

  @Insert("insert into sys_user_authority(user_login, authority_name) values(#{login}, #{authority})")
  void insertUserAuthority(@Param("login")String login, @Param("authority") String authority);

  @Delete("delete from sys_user_authority where user_login = #{login}")
  void deleteUserAuthorityByUserLogin(@Param("login")String login);

  @Delete("delete from sys_user_authority where user_login = #{login} and authority_name = #{authority}")
  void deleteUserAuthority(@Param("login") String login, @Param("authority") String authority);

  @Select("select authority_name from sys_user_authority where user_login = #{login}")
  Set<String> selectUserAuthorityNameSetByLogin(@Param("login") String login);
}