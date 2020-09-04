package com.yangzhou.util;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SetUtil {
  /**
   * 计算从源集合到结果集合中增加和删除的集合的内容
   *
   * @param <T>
   * @param pre
   * @param post
   * @return
   */
  public static <T> AddDelete<T> calculateAddDeleteSet(final Set<T> pre, Set<T> post) {
    // 存在于target但不存在于source的部分
    final Set<T> addSet    = post.stream().filter(e -> !pre.contains(e)).collect(Collectors.toSet());
    // 存在于source但不存在于target的部分
    final Set<T> deleteSet = pre.stream().filter(e -> !post.contains(e)).collect(Collectors.toSet());
    return new AddDelete<>(addSet, deleteSet);
  }

  @Getter
  @RequiredArgsConstructor
  public static class AddDelete<T> {
    final private Set<T> addSet;
    final private Set<T> deleteSet;
  }

  public static<T> boolean equal(Set<T> first, Set<T> second) {
    if (first.size() != second.size())
      return false;

    final AddDelete<T> addDelete = calculateAddDeleteSet(first, second);
    return addDelete.addSet.size() == 0 && addDelete.deleteSet.size() == 0;
  }

  public static boolean isEmpty(Set set) {
    if (set == null || set.size() == 0)
      return true;
    return false;
  }
}
