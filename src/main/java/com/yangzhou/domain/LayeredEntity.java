package com.yangzhou.domain;

/**
 * 层次结构实体接口
 *
 * @author Jiang Chuanwei
 *
 */
public interface LayeredEntity {
  /** 每层信息使用3个数字标识 */
  int     layerBit = 3;
  /** 每层信息的标识从101开始 */
  int     startNo  = 101;
  /** 决定是否需要根节点 */
  boolean needRoot = false;

  /** 当前记录的层数 */
  default int layerLevel() {
    return getLayer().length() / layerBit;
  }

  /** 获取当前记录的Layer信息。 */
  String getLayer();

  /** 设置当前记录的Layer信息 */
  void setLayer(String layer);

  /** 获取当前记录的父节点的layer */
  default String parent() {
    return getLayer().substring(0, getLayer().length() - layerBit);
  }

  /** 获取当前记录的所有祖先节点layer。低层节点在前，高层节点在后 */
  default String[] parents() {
    final int      length  = getLayer().length() / layerBit;

    final String[] parents = new String[length - 1];

    for (int i = 1; i < length; i++) {
      parents[i - 1] = getLayer().substring(0, (length - i) * layerBit);
    }
    return parents;
  }

  /** 判断当前节点是否为给定记录的子节点 */
  default boolean isChildOf(LayeredEntity other) {
    if (other == null) return layerLevel() == 1;
    final String parentLayer = getLayer().substring(0, getLayer().length() - layerBit);
    return parentLayer.equals(other.getLayer());
  }

  /** 判断给定节点是否为当前记录的后代节点 */
  default boolean isAncestorOf(LayeredEntity other) {
    if (other == null) return false;
    if(other.getLayer().length() <= getLayer().length()) return false;
    return other.getLayer().startsWith(getLayer());
  }
}
