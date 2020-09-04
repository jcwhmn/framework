package com.yangzhou.frame.service;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangzhou.frame.domain.Dictionary;
import com.yangzhou.frame.mapper.DictionaryMapper;
import com.yangzhou.service.BaseServiceImpl;

@Service("dictionaryService")
public class DictionaryService extends BaseServiceImpl<DictionaryMapper, Dictionary> {
  DictionaryMapper dictionaryMapper;

  public List<Dictionary> getDictionariesOfType(String type) {
    final QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("type", type);
    return super.list(queryWrapper);
  }

  public List<String> getDictionarieValuesOfType(String type) {
    return dictionaryMapper.getDictionarieValuesOfType(type);
  }

  public Set<String> getAllTypes() {
    return dictionaryMapper.getAllTypes();
  }
}
