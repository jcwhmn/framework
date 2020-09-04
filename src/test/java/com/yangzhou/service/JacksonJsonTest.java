package com.yangzhou.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonJsonTest {
  JacksonJsonService service = new JacksonJsonService();

  // @Test
  public void testToListMap() {
    final String   json1   = service.toJsonWithoutClassInfo(new TestData(1, "name11", "code11"));
    final TestData aaaData = service.toBean(json1, TestData.class);

    final Map<String, List<TestData>> testMap = new HashMap<>();
    List<TestData>                    datas   = new ArrayList<>();
    datas.add(new TestData(1, "name11", "code11"));
    datas.add(new TestData(2, "name12", "code12"));
    testMap.put("1", datas);

    datas = new ArrayList<>();
    datas.add(new TestData(1, "name21", "code21"));
    datas.add(new TestData(2, "name22", "code22"));
    testMap.put("2", datas);

    final String json = service.toJsonWithoutClassInfo(testMap);

    final Map<String, List<TestData>> testMap1 = service.toListMap(json, TestData.class);
    assertEquals(testMap.size(), testMap1.size());
  }
}
