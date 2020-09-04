package com.yangzhou.service;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yangzhou.domain.LayeredEntity;
import com.yangzhou.mapper.BaseEntityMapper;
import com.yangzhou.vm.LayeredEntityVM;
import com.yangzhou.vmmapper.LayeredVMMapper;

/**
 * 层次结构数据服务。
 * @author Jiang Chuanwei
 *
 * @param <T> 任意层次结构数据实体
 * @param <TVM> 和T相关的VM
 */
public interface LayeredService<T extends LayeredEntity, TVM extends LayeredEntityVM<T>> extends IService<T> {
  String LAYER_COLUMN = "layer";

  /**
   * 在{@link com.yangzhou.service.BaseServiceImpl}中有缺省实现。在业务实现类中覆盖该方法可使用用户自定义cache服务
   * @return cacheService
   */
  CacheService cacheService();

  /**
   * 在{@link com.yangzhou.service.BaseServiceImpl}中有缺省实现。在实现类中覆盖该方法可使用用户自定义businessType
   * @return cacheService
   */
  String businessType();

  /**
   * 设置treeCacheName为 businessType + _Layer。
   * 业务项目中覆盖该方法可使用用户自定义treeCacheName
   * @return 存储Tree的cache name
   */
  default String treeCacheName() {
    return businessType() + "_Layer";
  }

  /**
   *  在{@link com.yangzhou.service.BaseServiceImpl}中有缺省实现。在实现类中覆盖该方法可使用用户自定义Mapper
   * @return 继承于BaseEntityMapper的Mybatis db Mapper。
   */
  @SuppressWarnings("rawtypes")
  BaseEntityMapper getMapper();

  /**
   *  在{@link com.yangzhou.service.BaseServiceImpl}中有缺省实现。在实现类中覆盖该方法可使用用户自定义VMMapper
   *
   * @return 继承于BaseVMMapper的VMMapper
   */
  LayeredVMMapper getVMMapper();

  /**
   * 获取当前节点的兄弟节点的列表
   * @param curren
   * @return
   */
  default List<T> getSiblingList(T curren) {
    String parentLayer = "";
    if(curren != null) {
      parentLayer = curren.parent();
    }

    return getDirectChildrenList(parentLayer);
  }

  /**
   * 获取当前节点的父节点
   * @param current
   * @return
   */
  default T getParent(T current) {
    if(current == null)
      return null;

    final String parentLayer = current.parent();
    return (T) getMapper().selectOne(new QueryWrapper<T>().eq(LAYER_COLUMN, parentLayer));
  }

  /**
   * 获取当前节点的直接子节点列表
   *
   * @param current
   * @return
   */
  default List<T> getDirectChildrenList(T current) {
    String layer = "";
    if(current != null) {
      layer = current.getLayer();
    }

    return getDirectChildrenList(layer);
  }

  /**
   * 获取当前节点所有层级子节点列表
   * @param current
   * @return
   */
  default List<T> getAllChildrenList(T current) {
    String layer = "";
    if(current != null) {
      layer = current.getLayer();
    }

    return getAllChildrenList(layer);
  }

  /**
   * 获取当前节点所有层级父节点列表
   * @param current
   * @return
   */
  default List<T> getAllParents(T current) {
    if(current != null) {
      final String[] parentLayers = current.parents();
      return getMapper().selectList(new QueryWrapper<T>().in(LAYER_COLUMN, parentLayers));
    } else return new ArrayList<>();
  }

  /**
   * 获取当前业务类型的所有节点的树形列表
   * @return
   */
  default List<TVM> getEntireTree() {
    return getVMMapper().toTreeVM(getEntireList());
  }

  /**
   * 获取当前节点的所有子节点的树形列表
   * @param current
   * @return
   */
  default List<TVM> getChildrenTree(T current) {
    if(current == null)
      return getEntireTree();

    return getVMMapper().toTreeVM(getAllChildrenList(current));
  }

  /**
   * 获取当前layer的直接子节点列表
   * @param layer
   * @return
   */
  default List<T> getDirectChildrenList(String layer) {
    return getMapper().selectList(new QueryWrapper<T>().apply(String.format("%s like '%s___'", LAYER_COLUMN, layer)).orderByAsc(LAYER_COLUMN));
  }

  /**
   * 获取当前layer的所有层级子节点列表
   * @param layer
   * @return
   */
  default List<T> getAllChildrenList(String layer) {
    return getMapper().selectList(new QueryWrapper<T>().likeRight(LAYER_COLUMN, layer).ne(LAYER_COLUMN, layer).orderByAsc(LAYER_COLUMN));
  }

  /**
   * 获得当前业务类型所有节点列表
   * @return
   */
  default List<T> getEntireList() {
    return getMapper().selectList(new QueryWrapper<LayeredEntity>().orderByAsc(LAYER_COLUMN));
  }

  /**
   * 移动current节点，使其成为moveTo节点的子节点
   * @param current
   * @param moveTo
   */
  default void move(T current, T moveTo) {
    if(current == null)
      throw new RuntimeException("不能移动根节点！");

    if(moveTo != null && moveTo.isChildOf(current))
      throw new RuntimeException("不能移到当前节点的子节点中！");

    //当前节点是moveTo节点的直接子节点时，无需移动
    if(current.isChildOf(moveTo))
      return;

    final String oldLayer = current.getLayer();
    final String nextChildLayer = nextChildLayer(moveTo);

    final UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
    updateWrapper.likeRight("layer", oldLayer);
    updateWrapper.setSql(String.format("layer = concat('%s', right(layer, length(layer) - length('%s')))", nextChildLayer, oldLayer));
    getMapper().update(null, updateWrapper);

    cacheService().delete(treeCacheName());
  }

  /**
   * 获取当前节点的下一个子节点layer
   * @param current
   * @return
   */
  default String nextChildLayer(T current) {
    String parentLayer = "";
    if(current != null) {
      parentLayer = current.getLayer();
    }
    return nextChildLayer(parentLayer);
  }

  /**
   * 获取当前layer所指向节点的下一个子节点layer
   * @param layer
   * @return
   */
  default String nextChildLayer(String layer) {
    if(layer == null) {
      layer = "";
    }
    final List<T> childOfParen = getDirectChildrenList(layer);
    int index = 1;
    if(childOfParen != null && childOfParen.size() > 0) {
      final String currentChildLastLayer = childOfParen.get(childOfParen.size() - 1).getLayer();
      index = Integer.parseInt(currentChildLastLayer.substring(currentChildLastLayer.length() -3)) + 1;
    }
    return String.format("%s%03d", layer, index);
  }
}
