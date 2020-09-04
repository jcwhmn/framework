package com.yangzhou.domain;


import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 标识主键ID的基类。所有实体类都需要直接继承该类，或继承继承了该类的类 在数据库中需要有字段：
 *
 * `id` int(13) NOT NULL AUTO_INCREMENT,
 *
 * @author Jiang Chuanwei
 *
 */
@Data
public abstract class BaseEntityWithID {
  /** 主键插入值为null时，mysql自动生成值插入 */
  @TableId
  @ApiModelProperty(value = "实体ID，唯一标识对象",example = "123456")
  protected Long id;

  /** 用于指定validation时的分类策略，在新增记录时使用该类validation策略 */
  public interface Insert {}

  /** 用于指定validation时的分类策略，在修改记录时使用该类validation策略 */
  public interface Update {}

  public static <T extends BaseEntityWithID> Set<Long> toIdSet(Collection<T> source) {
    return source.stream().map(T::getId).collect(Collectors.toSet());
  }
}
