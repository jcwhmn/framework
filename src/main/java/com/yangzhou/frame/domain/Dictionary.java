package com.yangzhou.frame.domain;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yangzhou.domain.BaseEntityWithID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
final @EqualsAndHashCode(
      callSuper = true) @TableName("sys_dictionary")
public class Dictionary extends BaseEntityWithID implements Serializable{
  private static final long serialVersionUID = -5844625108181307463L;

  private String type;
  private String code;
  private String value;
  private String seq;
  private String description;
}
