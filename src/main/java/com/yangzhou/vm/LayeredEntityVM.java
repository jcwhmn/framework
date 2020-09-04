package com.yangzhou.vm;

import java.util.ArrayList;
import java.util.List;
import com.yangzhou.domain.LayeredEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayeredEntityVM<T extends LayeredEntity> {
  protected String                   layer;
  protected List<LayeredEntityVM<T>> children;

  public void add(LayeredEntityVM<T> child) {
    if (children == null) {
      children = new ArrayList<>();
    }
    children.add(child);
  }
}
