package com.yangzhou.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SearchParameter {
  @ApiModelProperty(value = "当前页数，从1开始", example = "1")
  protected long current = 1;
  @ApiModelProperty(value = "每页记录数", example = "10")
  protected long pageSize = 10;
  @JsonIgnore
  protected String orderByDescColumns;
  @JsonIgnore
  protected String orderByAscColumns;
}
