package com.yangzhou.frame.domain;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yangzhou.domain.BaseEntityWithID;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_extension_type")
@ApiModel("扩展类型")
public class ExtensionType extends BaseEntityWithID implements Serializable {
  private static final long serialVersionUID = -3858314602436082706L;
  @ApiModelProperty(
        value = "信息所属类型")
  String                    extensionClassName;
  @ApiModelProperty(
        value = "扩展信息字段")
  String fieldName;
  @ApiModelProperty(
        value = "扩展信息类型。允许值：Double、String、Object等")
  String fieldType;
  @ApiModelProperty(
        value = "扩展信息类名称。当fieldType为Object时，需要指定类名称。允许值：Double、String、Object等",
        example = "Organization")
  String                    fieldClassName;
}
