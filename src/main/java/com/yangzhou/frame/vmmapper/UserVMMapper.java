package com.yangzhou.frame.vmmapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.yangzhou.frame.domain.User;
import com.yangzhou.frame.vm.UserVM;
import com.yangzhou.vmmapper.BaseVMMapper;

/**
 * 在VM类UserVM和实体类User之间的转换
 */
@Mapper(componentModel = "spring", uses = {
    AuthorityToStringConverter.class })
public interface UserVMMapper extends BaseVMMapper<UserVM, User> {
  UserVMMapper instance = Mappers.getMapper(UserVMMapper.class);

}
