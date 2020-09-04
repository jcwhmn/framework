package com.yangzhou.frame.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.yangzhou.domain.BaseEntityWithID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * An authority (a security role) used by Spring Security.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Authority extends BaseEntityWithID implements Serializable {
  private static final long serialVersionUID = 1L;

  @NotNull
  @Size(max = 50)
  private String name;
}
