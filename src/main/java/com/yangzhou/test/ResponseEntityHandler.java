package com.yangzhou.test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yangzhou.service.JacksonJsonService;
import com.yangzhou.service.JsonService;
import com.yangzhou.util.MsgInfo;

@SuppressWarnings("rawtypes")
public class ResponseEntityHandler {
  protected final static JsonService jsonService = new JacksonJsonService();

  public static <T> Map<String, T> getMapData(ResponseEntity<MsgInfo> msgInfo, Class<T> clazz) {
    validateMsgInfo(msgInfo.getBody());
    return jsonService.toMap(getJsonString(msgInfo), clazz);
  }

  private static void validateMsgInfo(MsgInfo msgInfo) {
    if (msgInfo.getCode() != 1)
      throw new RuntimeException(String.format("运行不成功。错误代码为：%d. 错误消息：%s", msgInfo.getCode(), msgInfo.getMsg()));
  }

  public static <S, T> Map<S, T> getMapData(ResponseEntity<MsgInfo> msgInfo, Class<S> classS, Class<T> classT) {
    validateMsgInfo(msgInfo.getBody());
    return jsonService.toMap(getJsonString(msgInfo), classS, classT);
  }

  public static <T> Map<String, List<T>> getListMapData(ResponseEntity<MsgInfo> msgInfo, Class<T> classT) {
    validateMsgInfo(msgInfo.getBody());
    return jsonService.toListMap(getJsonString(msgInfo), classT);
  }

  public static <T> Set<T> getSetData(ResponseEntity<MsgInfo> msgInfo, Class<T> clazz) {
    validateMsgInfo(msgInfo.getBody());
    return jsonService.toSet(getJsonString(msgInfo), clazz);
  }

  public static <T> List<T> getListData(ResponseEntity<MsgInfo> msgInfo, Class<T> clazz) {
    validateMsgInfo(msgInfo.getBody());
    return jsonService.toList(getJsonString(msgInfo), clazz);
  }

  public static <T> T getObjectData(ResponseEntity<MsgInfo> msgInfo, Class<T> clazz) {
    validateMsgInfo(msgInfo.getBody());
    return jsonService.toBean(getJsonString(msgInfo), clazz);
  }

  public static <T> Page<T> getPageData(ResponseEntity<MsgInfo> msgInfo, Class<T> clazz) {
    validateMsgInfo(msgInfo.getBody());
    return jsonService.to2ClassObject(getJsonString(msgInfo), Page.class, clazz);
  }

  public static int getCode(ResponseEntity<MsgInfo> msgInfo) {
    return msgInfo.getBody().getCode();
  }

  public static Object getObject(ResponseEntity<MsgInfo> msgInfo) {
    if (msgInfo == null || msgInfo.getBody() == null || msgInfo.getBody().getData() == null)
      return null;
    return msgInfo.getBody().getData();
  }

  private static String getJsonString(ResponseEntity<MsgInfo> msgInfo) {
    if (msgInfo == null || msgInfo.getBody() == null || msgInfo.getBody().getData() == null)
      return null;
    return jsonService.toJsonWithoutClassInfo(msgInfo.getBody().getData());
  }
}
