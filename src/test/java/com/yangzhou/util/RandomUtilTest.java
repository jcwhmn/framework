package com.yangzhou.util;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import com.yangzhou.util.RandomUtil;

public class RandomUtilTest {
  @Test
  public void randomUtilTest() {
    int        min  = 0;
    int        max  = 9999;
    RandomUtil util = new RandomUtil(min, max);
    for (int i = 0; i < 100; i++) {
      int value = util.next();
      Assert.assertTrue("" + value, value >= min && value <= max);
    }
  }
}
