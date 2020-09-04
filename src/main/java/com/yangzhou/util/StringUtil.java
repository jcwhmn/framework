package com.yangzhou.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.CollectionUtils;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yangzhou.errors.BadRequestException;

public class StringUtil {
  /**
   * 将字符串中的所有符合from表达式的子串替换为to，并返回首字母小写的串。
   * 例如：replaceName("com.yangzhou.service.User","Service","domain")
   * 返回com.yangzhou.domain.User
   *
   * @param str  对其进行变换的类。
   * @param from
   * @param to
   * @return
   */
  public static String replaceName(String str, String from, String to) {
    str = str.replaceAll(from, to);
    return str.substring(0, 1).toLowerCase() + str.substring(1);
  }

  /**
   * 返回将所有from替换为to后的对象名
   *
   * @param obj
   * @param from
   * @param to
   * @return
   */
  public static String replaceName(Object obj, String from, String to) {
    String str = obj.getClass().getSimpleName();
    if(str.endsWith(from)) {
      str = str.replaceAll(from, to);
      return str.substring(0, 1).toLowerCase() + str.substring(1);
    } else return null;
  }

  /**
   * 从Service、Mapper等服务、Mapper等Component名称中查找业务对象的命令。 \r\n 例如：ProjectServiceImpl
   * -> project \r\n LocationVMMapper -> location
   *
   * @param obj
   * @return
   */
  public static String getBusinessType(Object obj) {
    final String[] types = { "ServiceImpl", "Service", "VMMapperImpl", "VMMapper", "Mapper", "Controller" };
    final String         str         = obj.getClass().getSimpleName();
    for (final String type : types) {
      if (str.endsWith(type)) return str.substring(0, 1).toLowerCase() + str.substring(1, str.length() - type.length());
    }
    return "";
  }

  /**
   * 从Service名称获取相应的表名。
   *
   * @param obj
   * @return
   */

  public static String getTableName(Object obj) {
    // 获取去掉ServiceImpl或Service的类全名
    String         str   = obj.getClass().getName();
    final String[] types = { "ServiceImpl", "Service" };
    for (final String type : types) {
      if (str.endsWith(type)) {
        final int pos = str.lastIndexOf(type);
        str = str.substring(0, pos);
        break;
      }
    }
    // 将包路径中的service替换为domain，获取实际的业务对象的类全名
    str = StringUtil.replaceName(str, "service", "domain");

    // 实例化业务对象类
    Class clz = null;
    try {
      clz = Class.forName(str);
    } catch (final ClassNotFoundException e) {
      e.printStackTrace();
      throw new BadRequestException(0, String.format("类型错误：%s不存在。", str));
    }

    // 如果业务对象类有tableName注解，使用注解的表名，否则，类名就是表名
    final Annotation annotation = clz.getAnnotation(TableName.class);
    if (annotation == null) return obj.getClass().getSimpleName();
    else {
      final TableName tableName = (TableName) annotation;
      return tableName.value();
    }
  }

  /**
   * 将Collection（List，Set等）的内容转为以delimiter分隔的字符串
   *
   * @param list
   * @param delimiter
   * @return
   */
  public static String listToString(Collection<String> list, CharSequence delimiter) {
    if (CollectionUtils.isEmpty(list)) return null;
    return String.join(delimiter, list);
  }

  /**
   * 将List的内容转为以逗号分隔的字符串
   *
   * @param list
   * @return
   */
  public static String listToString(List<String> list) {
    return listToString(list, ",");
  }

  /**
   * 将Collection（List，Set等）的内容转为以逗号分隔的字符串
   *
   * @param list
   * @return
   */
  public static String collectionToString(Collection<String> list) {
    return listToString(list, ",");
  }

  /**
   * 将以delimiter分隔的字符串转换为List<String>
   *
   * @param str
   * @param delimiter
   * @return
   */
  public static List<String> stringToList(String str, String delimiter) {
    if (str == null) return null;
    if (str.length() == 0) return new ArrayList<>();

    final String[] strToken = str.split(delimiter);
    return Arrays.asList(strToken);
  }

  /**
   * 将以逗号分隔的字符串转换为List<String>
   *
   * @param str
   * @return
   */
  public static List<String> stringToList(String str) {
    return stringToList(str, ",");
  }

  public static boolean hasToken(String source, String token) {
    if (isEmpty(source)) return false;
    if (isEmpty(token)) return true;

    final String delimeter = ",";

    final String[] strToken = source.split(delimeter);
    for (final String str : strToken) {
      if (token.equals(str)) return true;
    }
    return false;
  }

  public static String removeToken(String source, String token) {
    if(source == null) return null;
    if(token == null) return source;

    final String delimeter = ",";

    final String[] strToken = source.split(delimeter);
    final StringBuilder sb = new StringBuilder();
    boolean isFirst = true;
    for(final String str: strToken) {
      if (!token.equals(str)) {
        if(!isFirst) {
          sb.append(delimeter);
        }
        sb.append(str);
        isFirst = false;
      }
    }
    return sb.toString();
  }

  public static String replaceToken(String source, String token, String newToken) {
    if (isEmpty(source)) return newToken;

    final String delimeter = ",";
    if (isEmpty(token)) return source + delimeter + newToken;

    final String[]      strToken = source.split(delimeter);
    final StringBuilder sb       = new StringBuilder();
    boolean             isFirst  = true;
    for (final String str : strToken) {
      if (!isFirst) {
        sb.append(delimeter);
      }
      if (!token.equals(str)) {
        sb.append(str);
      } else {
        sb.append(newToken);
      }
      isFirst = false;
    }
    return sb.toString();
  }

  public static boolean isEmpty(String source) {
    return source == null || source.length() == 0;
  }

  private static Pattern humpPattern = Pattern.compile("[A-Z]");
  private static Pattern linePattern = Pattern.compile("_(\\w)");

  /**驼峰转下划线(简单写法，效率低于{@link #humpToLine2(String)})*/
  public static String humpToLine(String str){
    return str.replaceAll("[A-Z]", "_$0").toLowerCase();
  }
  /**驼峰转下划线,效率比上面高*/
  public static String humpToLine2(String str){
    final Matcher matcher = humpPattern.matcher(str);
    final StringBuffer sb = new StringBuffer();
    while(matcher.find()){
      matcher.appendReplacement(sb, "_"+matcher.group(0).toLowerCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }
  /**下划线转驼峰*/
  public static String lineToHump(String str){
    str = str.toLowerCase();
    final Matcher matcher = linePattern.matcher(str);
    final StringBuffer sb = new StringBuffer();
    while(matcher.find()){
      matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  public static String replacePathVariables(String url, String... pathVariables) {
    for (final String pathVariable : pathVariables) {
      final int index = url.indexOf("{}");
      url = url.substring(0, index) + pathVariable + url.substring(index + 2);
    }
    return url;
  }
}
