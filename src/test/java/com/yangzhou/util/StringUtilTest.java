package com.yangzhou.util;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class StringUtilTest {

  @Test
  public void humpToLineTest() {
    final String test = "myTestString";
    final String expected = "my_test_string";
    final String actual = StringUtil.humpToLine(test);
    assertThat(actual, Matchers.is(expected));
  }

  @Test
  public void humpToLine2Test() {
    final String test = "myTestString";
    final String expected = "my_test_string";
    final String actual = StringUtil.humpToLine2(test);
    assertThat(actual, Matchers.is(expected));
  }

  @Test
  public void lineToHumpTest() {
    final String expected = "myTestString";
    final String test = "my_test_string";
    final String actual = StringUtil.lineToHump(test);
    assertThat(actual, Matchers.is(expected));
  }

  @Test public void emptyStringToListTest() {
    final String       empty = "";
    final List<String> list  = StringUtil.stringToList(empty);
    assertEquals(0, list.size());
  }

  @Test public void replacePathVariablesTest() {
    String url = "/api/v1/roles/{}/{}/rights";
    url = StringUtil.replacePathVariables(url, "1", "2");
    assertTrue("/api/v1/roles/1/2/rights".equals(url));
  }
}
