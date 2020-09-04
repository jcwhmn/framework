package com.yangzhou.test;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.yangzhou.service.JacksonJsonService;
import com.yangzhou.service.JsonService;
import com.yangzhou.util.MsgInfo;
import com.yangzhou.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("rawtypes")
public class ControllerTestHandler {
  protected final static JsonService jsonService = new JacksonJsonService();
  private final static String adminToken = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6NDEzMTY4MDA1OH0.5Mh6HWmaqSAlEFIPFuN_SKkmz_p6yb3sYTWGnGRgJF8uMGP9LLz8JmfWJO22_GhrM0cN74bAopOJJQFxBYkeWQ";

  public ResponseEntity<MsgInfo> getAction(RestTemplate testRestTemplate, String url) {
    return httpAction(HttpMethod.GET, testRestTemplate, null, null, url);
  }

  public ResponseEntity<MsgInfo> getAction(RestTemplate testRestTemplate, Map<String, String> parameters, String url) {
    return httpAction(HttpMethod.GET, testRestTemplate, null, parameters, url);
  }

  public ResponseEntity<MsgInfo> postAction(RestTemplate testRestTemplate, Object object, String url) {
    return httpAction(HttpMethod.POST, testRestTemplate, object, null, url);
  }

  public ResponseEntity<MsgInfo> postAction(RestTemplate testRestTemplate, Object object,
        Map<String, String> parameters, String url) {
    return httpAction(HttpMethod.POST, testRestTemplate, object, parameters, url);
  }

  public ResponseEntity<MsgInfo> putAction(RestTemplate testRestTemplate, Object object, String url) {
    return httpAction(HttpMethod.PUT, testRestTemplate, object, null, url);
  }

  public ResponseEntity<MsgInfo> putAction(RestTemplate testRestTemplate, Object object, Map<String, String> parameters,
        String url) {
    return httpAction(HttpMethod.PUT, testRestTemplate, object, parameters, url);
  }

  public ResponseEntity<MsgInfo> deleteAction(RestTemplate testRestTemplate, String url) {
    return httpAction(HttpMethod.DELETE, testRestTemplate, null, null, url);
  }

  public ResponseEntity<MsgInfo> deleteAction(RestTemplate testRestTemplate, Object object,
        Map<String, String> parameters, String url) {
    return httpAction(HttpMethod.DELETE, testRestTemplate, object, parameters, url);
  }

  protected HttpHeaders header() {
    return header(null, null);
  }

  protected HttpHeaders header(String token, Map<String, String> headMap) {
    final HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json; charset=UTF-8");
    if (token == null) {
      headers.add("Authorization", adminToken);
    } else {
      headers.add("Authorization", token);
    }
    if (headMap != null) {
      headMap.forEach((key, value) -> headers.add(key, value));
    }
    return headers;
  }

  public ResponseEntity<MsgInfo> httpAction(HttpMethod method, RestTemplate testRestTemplate, String url) {
    return httpAction(method, testRestTemplate, null, null, null, url);
  }

  public ResponseEntity<MsgInfo> httpAction(HttpMethod method, RestTemplate testRestTemplate, Object object,
        String url) {
    return httpAction(method, testRestTemplate, object, null, null, url);
  }

  public ResponseEntity<MsgInfo> httpAction(HttpMethod method, RestTemplate testRestTemplate,
        Map<String, String> parameters, String url) {
    return httpAction(method, testRestTemplate, null, parameters, null, url);
  }

  public ResponseEntity<MsgInfo> httpAction(HttpMethod method, RestTemplate testRestTemplate, Object object,
        Map<String, String> parameters, String url) {
    return httpAction(method, testRestTemplate, object, parameters, null, url);
  }

  public ResponseEntity<MsgInfo> httpAction(HttpMethod method, RestTemplate testRestTemplate, Object object,
        Map<String, String> parameters, Map<String, String> headMap, String url) {
    return httpAction(method, testRestTemplate, object, parameters, headMap, url, null);
  }

  public ResponseEntity<MsgInfo> httpAction(HttpMethod method, RestTemplate testRestTemplate, Object object,
        Map<String, String> parameters, Map<String, String> headMap, String url, String[] pathVariables) {
    return httpAction(method, testRestTemplate, object, parameters, headMap, url, pathVariables, null);
  }

  public ResponseEntity<MsgInfo> httpAction(HttpMethod method, RestTemplate testRestTemplate, Object object,
        Map<String, String> parameters, Map<String, String> headMap, String url, String[] pathVariables, String token) {
    return doAction(method, testRestTemplate, object, parameters, headMap, url, pathVariables, token, MsgInfo.class);
  }

  /**
   * 下载文件。返回值非MsgInfo，而是byte[]。可是通过判断返回值是否为空、及返回body的长度来进行assert。
   *
   * @param method
   * @param testRestTemplate
   * @param object
   * @param parameters
   * @param headMap
   * @param url
   * @param pathVariables
   * @return ResponseEntity<byte[]>
   */
  public ResponseEntity<byte[]> download(HttpMethod method, RestTemplate testRestTemplate, Object object,
        Map<String, String> parameters, Map<String, String> headMap, String url, String[] pathVariables, String token) {
    return doAction(method, testRestTemplate, object, parameters, headMap, url, pathVariables, token, byte[].class);
  }

  public ResponseEntity<String> json(HttpMethod method, RestTemplate testRestTemplate, Object object,
        Map<String, String> parameters, Map<String, String> headMap, String url, String[] pathVariables, String token) {
    return doAction(method, testRestTemplate, object, parameters, headMap, url, pathVariables, token, String.class);
  }

  /**
   * 上传文件。例：{@code
      final ClassPathResource classPathResource = new ClassPathResource("templates/excel/company.xlsx");
      final MultiValueMap     multiValueMap     = new LinkedMultiValueMap();
      multiValueMap.add("file", classPathResource);
      final Object result = helper.upload(restTemplate, url, multiValueMap);
   * }
   *
   * @param testRestTemplate
   * @param url
   * @param multiValueMap。MultiValueMap。
   * @return
   */
  public ResponseEntity<MsgInfo> upload(RestTemplate testRestTemplate, String url, MultiValueMap multiValueMap) {
    final ResponseEntity<MsgInfo> result = testRestTemplate.postForEntity(url, multiValueMap, MsgInfo.class);
    return result;
  }

  public <T> ResponseEntity<T> doAction(HttpMethod method, RestTemplate testRestTemplate, Object object,
        Map<String, String> parameters, Map<String, String> headMap, String url, String[] pathVariables, String token, Class<T> clazz) {
    final HttpHeaders headers = header(token, headMap);
    final String      jsonStr = object == null ? null : jsonService.toJsonWithoutClassInfo(object);

    log.debug("{} {}, body : {}, parameters : {}, headers: {}, path: {}", method.toString(), url, parameters, headMap, pathVariables);

    if (pathVariables != null && pathVariables.length > 0) {
      url = StringUtil.replacePathVariables(url, pathVariables);
    }

    if (parameters != null) {
      url = addParametersToUrl(url, parameters);
    }

    ResponseEntity<T> result;
    result = testRestTemplate.exchange(url, method, new HttpEntity<>(jsonStr, headers), clazz);

    log.debug(resultLog(result));
    return result;
  }

  protected <T> String resultLog(ResponseEntity<T> result) {
    final StringBuilder sb = new StringBuilder();
    if (result == null) {
      sb.append("No result");
    } else {
      sb.append(result.getStatusCode().toString())
      .append(' ');
      if (result.getBody() == null) {
        sb.append("no body");
      } else {
        if (result.getBody() instanceof MsgInfo) {
          final MsgInfo data = (MsgInfo) result.getBody();
          sb.append(data.getCode())
          .append(" Msg:")
          .append(data.getMsg());
        }
      }
    }
    return sb.toString();
  }
  protected String addParametersToUrl(String url, Map<String, String> parameters) {
    if(parameters == null || parameters.size() == 0)
      return url;

    final StringBuffer sb = new StringBuffer(url);
    sb.append("?");
    parameters.forEach((key, value) -> {
      sb.append(key).append("=").append(value).append("&");
    });
    url = sb.toString();
    return url.substring(0, url.length() - 1); // remove last & char
  }
}
