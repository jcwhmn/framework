package com.yangzhou.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangzhou.annotation.SearchParameterAnnotation;
import com.yangzhou.domain.BaseEntityWithID;
import com.yangzhou.domain.SearchParameter;
import com.yangzhou.domain.StringPair;
import com.yangzhou.errors.ObjectNotFoundException;
import com.yangzhou.mapper.BaseEntityMapper;
import com.yangzhou.util.SpringUtil;
import com.yangzhou.util.StringUtil;
import com.yangzhou.vmmapper.BaseVMMapper;

/**
 * 所有Service的基类。它扩展自Mybatis Plus的ServiceImpl, 实现了Mybatis Plus的基本方法，并在这些基本方法上
 * 加入了Cache信息
 *
 * @author Jiang Chuanwei
 *
 * @param <S> 继承于{@link com.yangzhou.mapper.BaseEntityMapper}的Mybatis Mapper接口
 * @param <T> 继承与{@link com.yangzhou.domain.BaseEntityWithID} 的实体类
 */
public abstract class BaseServiceImpl<S extends BaseEntityMapper<T>, T extends BaseEntityWithID> extends ServiceImpl<BaseMapper<T>, T> implements BaseEntityService<T> {
  @Autowired protected CacheService cacheService;
  protected JsonService  jsonService = new JacksonJsonService();
  protected final static EntityExtensionManager extensionManager = new EntityExtensionManager();
  @SuppressWarnings("rawtypes")
  protected BaseEntityMapper                    mapper           = null;
  /**
   * 定义cache服务。 若业务实现类中使用了自定义cache服务，可覆盖该方法。
   *
   * @return cache服务
   */
  public CacheService cacheService() {
    return cacheService;
  }

  /**
   * 从服务名获取的业务类型。如DepartmentServiceImpl的业务类型是Department.
   * 若业务类型不能从服务名中自动获取，可覆盖该方法，赋予业务名
   * @return 业务类型
   */
  public String businessType() {
    return StringUtil.getBusinessType(this);
  }

  /**
   * 获取容器中名为businessType + Mapper的Mapper。 若实际业务中不能使用该规则获取Mapper，可覆盖该方法，赋予Mapper.
   *
   * @return 继承于BaseEntityMapper的Mybatis db Mapper。
   */
  @SuppressWarnings("rawtypes")
  public BaseEntityMapper getMapper() {
    if (mapper == null) {
      mapper = (BaseEntityMapper) SpringUtil.getBean(businessType() + "Mapper");
    }
    return mapper;
  }

  protected String tableName() {
    return StringUtil.getTableName(this);
  }

  /**
   * 获取容器中名为businessType + VMMapperImpl的VMMapper。
   * 若实际业务中不能使用该规则获取VMMapper，可覆盖该方法，赋予VMMapper.
   *
   * @return 继承于BaseVMMapper的VMMapper
   */
  @SuppressWarnings("rawtypes")
  public BaseVMMapper getVMMapper() {
    return (BaseVMMapper) SpringUtil.getBean(businessType() + "VMMapperImpl");
  }

  /**
   *
   * @param <V>
   * @param searchParameter
   * @return
   * @throws Throwable
   */
  @SuppressWarnings("unchecked")
  @Override
  public <V extends SearchParameter> Page<T> listPage(V searchParameter) {
    final Page<T> page = new Page<>(searchParameter.getCurrent(), searchParameter.getPageSize());
    if (!StringUtil.isEmpty(searchParameter.getOrderByAscColumns())) {
      final String[] orderTokens = searchParameter.getOrderByAscColumns().split(",");
      page.setAsc(orderTokens);
    }
    if (!StringUtil.isEmpty(searchParameter.getOrderByDescColumns())) {
      final String[] orderTokens = searchParameter.getOrderByDescColumns().split(",");
      page.setDesc(orderTokens);
    }
    QueryWrapper<T> qWrapper = new QueryWrapper<>();
    qWrapper = assembleQueryWrapperFromSearchParameter(searchParameter, qWrapper);
    return (Page<T>) super.page(page, qWrapper);
  }

  /**
   *
   * @param <VM>
   * @param <V>
   * @param searchParameter
   * @return
   * @throws Throwable
   */
  @SuppressWarnings("unchecked")
  @Override
  public <VM, V extends SearchParameter> Page<VM> listVMPage(V searchParameter) {
    Page<T>         page     = new Page<>(searchParameter.getCurrent(), searchParameter.getPageSize());
    QueryWrapper<T> qWrapper = new QueryWrapper<>();
    qWrapper = assembleQueryWrapperFromSearchParameter(searchParameter, qWrapper);
    page     = (Page<T>) super.page(page, qWrapper);

    return pageToPageVM(page);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V extends SearchParameter> int searchCount(V searchParameter) {
    QueryWrapper<T> qWrapper = new QueryWrapper<>();
    qWrapper = assembleQueryWrapperFromSearchParameter(searchParameter, qWrapper);
    return super.count(qWrapper);
  }

  @SuppressWarnings("unchecked")
  protected <V extends SearchParameter, VM> Page<VM> pageToPageVM(Page<T> page) {
    final Page<VM> pageVM = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
    pageVM.setRecords(getVMMapper().toVms(page.getRecords()));
    return pageVM;
  }

  @Override
  public <V extends SearchParameter> List<StringPair> searchConditionPairs(V searchParameter) {
    final List<StringPair> pairs  = new ArrayList<>();
    final Field[]       fields = getAllFields(searchParameter.getClass());
    for (final Field field : fields) {
      field.setAccessible(true);
      final String columnName = StringUtil.humpToLine2(field.getName());
      try {
        final Object fieldValue = field.get(searchParameter);
        if (fieldValue != null) {
          pairs.add(new StringPair(columnName, fieldValue.toString()));
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
    return pairs;
  }

  @SuppressWarnings("rawtypes")
  protected Field[] getAllFields(Class clz) {
    final List<Field> filList = new ArrayList<>();
    while (!clz.equals(SearchParameter.class)) {
      final Field[] fields = clz.getDeclaredFields();
      for (final Field field : fields) {
        filList.add(field);
      }
      clz = clz.getSuperclass();
    }
    return filList.toArray(new Field[filList.size()]);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected <V extends SearchParameter> QueryWrapper assembleQueryWrapperFromSearchParameter(V searchParameter, QueryWrapper qWrapper) {
    final Field[] fields = getAllFields(searchParameter.getClass());

    for(final Field field: fields) {
      field.setAccessible(true);
      String columnName = StringUtil.humpToLine2(field.getName());
      try {
        final Object fieldValue = field.get(searchParameter);
        if(fieldValue != null) {
          final SearchParameterAnnotation annotation = AnnotationUtils.findAnnotation(field, SearchParameterAnnotation.class);
          if(annotation != null) {
            if(!StringUtils.isEmpty(annotation.columnName())) {
              columnName = annotation.columnName();
            }
            final String type = annotation.type();
            final Method method = qWrapper.getClass().getMethod(type, Object.class, Object.class);
            if(method != null) {
              qWrapper = (QueryWrapper) method.invoke(qWrapper, columnName, fieldValue);
            }else {
              qWrapper = (QueryWrapper) qWrapper.eq(columnName, fieldValue);
            }
          } else {
            qWrapper = (QueryWrapper) qWrapper.eq(columnName, fieldValue);
          }
        }
      }catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | IllegalAccessException e) {
        throw new RuntimeException(String.format("Search parameter : %s, please verify!", searchParameter.toString()));
      }
    }
    return qWrapper;
  }

  @Override
  public T assertExistById(Long id) {
    final T t = getById(id);

    if (t == null)
      throw new ObjectNotFoundException(id, businessType());
    return t;
  }

  @Override public T getById(Serializable id) {
    //      return super.getById(id);
    return cacheService.getOrSet(businessType(), id.toString(), () -> super.getById(id));
  }

  // 插入新纪录
  @Override public boolean save(T entity) {
    final boolean result = super.save(entity);
    if (result) {
      cacheService.set(businessType(), entity.getId().toString(), entity);
    }
    EventBusCenter.post(entity);
    return result;
  }

  // Id为空，插入新纪录。否则，修改记录
  //  @Override public boolean saveOrUpdate(T entity) {
  //    final Long id = entity.getId();
  //    final boolean result = super.saveOrUpdate(entity);
  //    if (result) {
  //      if (id != null) { // update entity
  //        cacheService.delete(businessType(), id.toString());
  //      } else {
  //        cacheService.set(businessType(), entity.getId().toString(), entity);
  //      }
  //    }
  //    return result;
  //  }

  @Override public boolean removeById(Serializable id) {
    final boolean result = super.removeById(id);
    if (result) {
      cacheService.delete(businessType(), id.toString());
    }
    return result;
  }

  //  @Override public boolean remove(Wrapper<T> wrapper) {
  //    // 由于无法确定哪些id的对象被删除，需要删除整个name的cache
  //    final boolean result = super.remove(wrapper);
  //    if (result) {
  //      cacheService.delete(businessType());
  //    }
  //    return result;
  //  }

  //  @Override
  //  public boolean removeByIds(Collection<? extends Serializable> idList) {
  //    final boolean result = super.removeByIds(idList);
  //    if (result) {
  //      cacheService.delete(businessType(), idList);
  //    }
  //    return result;
  //  }

  @Override public boolean updateById(T entity) {
    final boolean result = super.updateById(entity);
    if (result) {
      cacheService.delete(businessType(), entity.getId().toString());
    }
    return result;
  }

  //  @Override
  //  public boolean update(T entity, Wrapper<T> updateWrapper) {
  //    final boolean result = super.update(entity, updateWrapper);
  //    if (result) {
  //      cacheService.delete(businessType(), entity.getId().toString());
  //    }
  //    return result;
  //  }

  //  @Override
  //  public boolean updateBatchById(Collection<T> entityList, int batchSize) {
  //    final boolean result = super.updateBatchById(entityList, batchSize);
  //    if (result) {
  //      cacheService.delete(businessType(), entityList.parallelStream().map(BaseEntityWithID::getId).collect(Collectors.toList()));
  //    }
  //    return result;
  //  }

  protected void deleteByIds(Collection<T> source) {
    super.removeByIds(BaseEntityWithID.toIdSet(source));
  }

  @Override
  public List<T> list(StringPair... pairs) {
    return super.list(StringPair.toQueryWrapper(pairs));
  }

  @Override
  public boolean existById(Long id) {
    return getMapper().exist(tableName(), "id", id.toString()) != null;
  }

  @Override
  public boolean exist(String field, String value) {
    final Integer exist = getMapper().exist(tableName(), field, value);
    return exist != null;
  }

  @Override
  public boolean exist(String table, String field, String value) {
    return getMapper().exist(table, field, value) != null;
  }

  @SuppressWarnings("unchecked")
  @Override public boolean exist(String table, StringPair... pairs) {
    final List<StringPair> list = Arrays.asList(pairs);
    return getMapper().existPair(table, list) != null;
  }

}
