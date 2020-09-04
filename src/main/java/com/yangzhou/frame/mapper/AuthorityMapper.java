package com.yangzhou.frame.mapper;

import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangzhou.frame.domain.Authority;

@Mapper
public interface AuthorityMapper extends BaseMapper<Authority> {
	@Select({ "select * from sys_authority" })
	List<Authority> findAll();

	@Select({ "select * from sys_authority where name = #{name}" })
	Authority findOneByName(String name);

	@Select({ "select ua.authority_name as name from sys_user_authority ua where ua.user_login = #{login}" })
	Set<Authority> selectByUserLogin(String login);
	
	@Update(" update sys_user_authority set user_login = #{newLogin} where user_login = #{oldLogin}")
	void updateAuthorityByLogin(@Param(value="newLogin") String newLogin,@Param(value="oldLogin") String oldLogin);
}
