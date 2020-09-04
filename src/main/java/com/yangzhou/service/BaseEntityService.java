package com.yangzhou.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yangzhou.domain.BaseEntityWithID;
import com.yangzhou.domain.SearchParameter;
import com.yangzhou.domain.StringPair;

public interface BaseEntityService<T extends BaseEntityWithID> extends IService<T>, AssertService {

  /**
   *
   * @param <V>
   * @param searchParameter
   * @return
   * @throws Throwable
   */
  <V extends SearchParameter> Page<T> listPage(V searchParameter);

  /**
   *
   * @param <VM>
   * @param <V>
   * @param searchParameter
   * @return
   * @throws Throwable
   */
  <VM, V extends SearchParameter> Page<VM> listVMPage(V searchParameter);

  boolean existById(Long id);

  boolean exist(String field, String value);

  boolean exist(String table, String field, String value);

  <V extends SearchParameter> int searchCount(V searchParameter);

  T assertExistById(Long id);

  boolean exist(String table, StringPair... pairs);

  <V extends SearchParameter> List<StringPair> searchConditionPairs(V searchParameter);

  List<T> list(StringPair... pairs);

}