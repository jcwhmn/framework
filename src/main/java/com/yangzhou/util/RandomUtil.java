package com.yangzhou.util;

import java.util.Random;
import lombok.AllArgsConstructor;

/**
 * Utility class for generating random Strings.
 */

@AllArgsConstructor
public final class RandomUtil {
  final private int min;
  final private int max;
  final Random      random = new Random(System.currentTimeMillis());

  public int next() {
    int value = (random.nextInt() % (max - min)) + min;
    if (value < min)
      value += (max - min);
    return value;
  }
}
