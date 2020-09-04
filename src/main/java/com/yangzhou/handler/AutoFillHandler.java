package com.yangzhou.handler;

import java.time.LocalDateTime;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.yangzhou.security.SecurityUtils;

/**
 * 实现Mybatis Plus MetaObjectHandler接口，实现自动插入对象跟踪信息功能
 * 
 * @author Jiang Chuanwei
 *
 */
@Component
public class AutoFillHandler implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    //final String userLogin = "RYD ZZB Team";
    if(metaObject.hasGetter("insertBy")) {
        final String userLogin = SecurityUtils.getCurrentLoginName();
    	metaObject.setValue("insertBy", userLogin);
    	metaObject.setValue("insertAt", LocalDateTime.now());
    }
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    //final String userLogin = "RYD ZZB Team";
    if(metaObject.hasGetter("updateBy")) {
        String userLogin = SecurityUtils.getCurrentLoginName();
    	metaObject.setValue("updateBy", userLogin);
    	metaObject.setValue("updateAt", LocalDateTime.now());
    }
    if(metaObject.hasGetter("et.updateBy")) {
        final String userLogin = SecurityUtils.getCurrentLoginName();
    	metaObject.setValue("et.updateBy", userLogin);
    	metaObject.setValue("et.updateAt", LocalDateTime.now());
    }
  }

}
