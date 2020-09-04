package com.yangzhou.test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yangzhou.domain.StringPair;
import com.yangzhou.frame.vm.LoginVM;
import com.yangzhou.util.MsgInfo;
import lombok.AllArgsConstructor;

/**
 * Http Action 测试帮助类。取代原ControllerTestAssertionHelper类
 *
 * <br/>
 * <br/>
 * 使用方法： <br/>
 * 1 在测试类声明 HttpActionTestHelper helper; <br/>
 * 2 在@BeforeEach的setup方法中，构造helper <br/>
 * {@code @BeforeEach public void setup() { helper = new
 * HttpActionTestHelper(testRestTemplate.getRestTemplate()); }} <br/>
 * 3 分别使用url(url)、body(body)、parameters(map)、headers(map)方法设置url、body、parameters
 * 、headers <br/>
 * 4 执行get()、post()、delete()、put()等方法执行http action <br/>
 * 5 使用failCode()方法获取fail action的fail code <br/>
 * 5
 * 使用successObject(Class)、successList(Class)、successMap、successPage、successSet等方法获取success
 * action结果 <br/>
 *
 * <br/>
 * 示例 <br/>
 * 示例1：List<LocationVM> object =
 * helper.url(url).get().successList(LocationVM.class);
 *
 * <br/>
 * 示例2：{@code final Map<String, String> parameters = new HashMap<>();
 * parameters.put("currentPage", "0");
 * parameters.put("pageSize", "10");
 * Page<LocationVM> result =
 * helper.url(url).parameters(parameters).post().successPage(LocationVM.class);
 * } * <br/>
 * 示例3 int result = helper.url(url).body(user).post().failCode();
 *
 *
 * @author Jiang chuanwei
 *
 */
public class HttpActionTestHelper {
  private final RestTemplate        restTemplate;
  private Object              body;
  private Map<String, String> parameters;
  private Map<String, String> headers;
  private String              url;
  private String[]            pathVariables;
  private String              token;

  private final static ControllerTestHandler handler = new ControllerTestHandler();

  public HttpActionTestHelper(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public HttpActionTestHelper login(String username, String password) {
    final String originUrl = url;
    url  = "/api/v1/authenticate";
    final Object originBody = body;
    body = new LoginVM(username, password);
    token = post().token();
    url   = originUrl;
    body  = originBody;
    return this;
  }
  public HttpActionTestHelper(RestTemplate restTemplate, String url) {
    this.restTemplate = restTemplate;
    this.url          = url;
  }

  public HttpActionTestHelper url(String url) {
    this.url = url;
    return this;
  }

  public HttpActionTestHelper token(String token) {
    this.token = token;
    return this;
  }

  public HttpActionTestHelper body(Object body) {
    this.body = body;
    return this;
  }

  public HttpActionTestHelper parameters(Map<String, String> parameters) {
    this.parameters = parameters;
    return this;
  }

  public HttpActionTestHelper parameters(StringPair... pairs) {
    parameters = StringPair.toStringMap(pairs);
    return this;
  }

  public HttpActionTestHelper headers(Map<String, String> headers) {
    this.headers = headers;
    return this;
  }

  public HttpActionTestHelper pathVariables(final String... pathVariables) {
    this.pathVariables = pathVariables;
    return this;
  }

  public TestAction get() {
    return action(HttpMethod.GET);
  }

  public TestAction put() {
    return action(HttpMethod.PUT);
  }

  public TestAction post() {
    return action(HttpMethod.POST);
  }

  public TestAction patch() {
    return action(HttpMethod.PATCH);
  }

  public TestAction head() {
    return action(HttpMethod.HEAD);
  }

  public TestAction options() {
    return action(HttpMethod.OPTIONS);
  }

  public TestAction trace() {
    return action(HttpMethod.TRACE);
  }

  public TestAction delete() {
    return action(HttpMethod.DELETE);
  }

  public TestAction upload(MultiValueMap multiValueMap) {
    return new TestAction(handler.upload(restTemplate, url, multiValueMap));
  }

  public TestAction action(HttpMethod method) {
    return new TestAction(handler.httpAction(method, restTemplate, body, parameters, headers, url, pathVariables, token));
  }

  public byte[] download(HttpMethod method) {
    return handler.download(method, restTemplate, body, parameters, headers, url, pathVariables, token).getBody();
  }

  public String json(HttpMethod method) {
    return handler.json(method, restTemplate, body, parameters, headers, url, pathVariables, token).getBody();
  }

  @AllArgsConstructor
  public final static class TestAction {
    ResponseEntity<MsgInfo> responseEntity;

    public Integer successInteger() {
      return Integer.parseInt(ResponseEntityHandler.getObject(responseEntity).toString());
    }

    public <T> T successObject(Class<T> clazz) {
      return ResponseEntityHandler.getObjectData(responseEntity, clazz);
    }

    public <T> List<T> successList(Class<T> clazz) {
      return ResponseEntityHandler.getListData(responseEntity, clazz);
    }

    public <T> Page<T> successPage(Class<T> clazz) {
      return ResponseEntityHandler.getPageData(responseEntity, clazz);
    }

    public <T> Set<T> successSet(Class<T> clazz) {
      return ResponseEntityHandler.getSetData(responseEntity, clazz);
    }

    public <T> Map<String, T> successMap(Class<T> clazz) {
      return ResponseEntityHandler.getMapData(responseEntity, clazz);
    }

    public <U, V> Map<U, V> successMap(Class<U> clazzU, Class<V> clazzV) {
      return ResponseEntityHandler.getMapData(responseEntity, clazzU, clazzV);
    }

    public <T> Map<String, List<T>> successListMap(Class<T> clazz) {
      return ResponseEntityHandler.getListMapData(responseEntity, clazz);
    }

    public int failCode() {
      return ResponseEntityHandler.getCode(responseEntity);
    }

    public int code() {
      return ResponseEntityHandler.getCode(responseEntity);
    }

    public int statusCode() {
      return responseEntity.getStatusCodeValue();
    }

    public String message() {
      return responseEntity.getBody().getMsg();
    }

    public Object body() {
      return responseEntity.getBody();
    }

    public Object bodyData() {
      return responseEntity.getBody().getData();
    }

    public String token() {
      final Object obj = header("Authorization");
      if (obj != null)
        return ((List) obj).get(0).toString();
      else return null;
    }

    public HttpHeaders headers() {
      return responseEntity.getHeaders();
    }

    public Object header(String key) {
      return responseEntity.getHeaders().get(key);
    }

    public boolean isSuccess() {
      return ResponseEntityHandler.getCode(responseEntity) == 1;
    }

    public boolean isFailure() {
      return ResponseEntityHandler.getCode(responseEntity) == 1;
    }
  }
}
