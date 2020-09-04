package com.yangzhou.util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateUtilTest {

  @Test void testDateAfterToday() {
    final LocalDate date         = DateUtil.dateAfterToday(20);
    final LocalDate expectdeDate = LocalDate.now().plusDays(20);
    assertTrue(expectdeDate.equals(date));
  }

}
