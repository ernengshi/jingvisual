/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import cn.com.jingcloud.domain.entity.shiro.User;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author liyong
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestTemplateTest {

    @Autowired
    RestTemplate restTemplate;

    /**
     * *********HTTP GET method************
     */
//    @Test
    public void getTest() {
        String url = "http://localhost:8080/hello";
        User json = restTemplate.getForEntity(url, User.class).getBody();
        System.out.println("#########" + json.getName());
    }

    /**
     * ********HTTP POST method*************
     */
//    @Test
    public void testPost() throws JSONException {
        String url = "http://localhost:8080/postApi";
        JSONObject postData = new JSONObject();
        postData.put("descp", "request for post");
        JSONObject json = restTemplate.postForEntity(url, postData, JSONObject.class).getBody();
        System.out.println("#########" + json.toString());
    }

    /**
     * 添加http  header
     *
     * @throws JSONException
     */
    @Test
    public void testPostWithHeader() throws JSONException {
        //该方法通过restTemplate请求远程restfulAPI  
        HttpHeaders headers = new HttpHeaders();
//        headers.
        headers.set("authorization", "shiro:123456");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity entity = new HttpEntity(headers);
        HttpEntity<String> response = restTemplate.exchange(
                "http://localhost:8080/hello", HttpMethod.GET, entity, String.class);//这里放JSONObject, String 都可以。因为JSONObject返回的时候其实也就是string  
        System.out.println("##############" + response.getBody());
    }

    /**
     * RestTemplate 实现 curl -v -u user:pass http://myapp.com/api
     * https://stackoverflow.com/questions/14383240/basic-authentication-with-resttemplate-3-1
     * httpclient 实现使用
     * https://stackoverflow.com/questions/28366969/java-httpclient-4-3-6-basic-authentication-with-complete-uri-and-scheme
     *
     * @param username
     * @param password
     * @return
     */
    private static RestTemplate createRestTemplate(String username, String password) {
        UsernamePasswordCredentials cred = new UsernamePasswordCredentials(username, password);
        BasicCredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(AuthScope.ANY, cred);
        HttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(cp).build();
//    HttpClient client = HttpClients.custom().setDefaultCredentialsProvider();
//    client.setCredentialsProvider(cp);
        ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(factory);
        // set the media types properly
        return restTemplate;
    }

    @Test
    public void callTest() {
        RestTemplate restTemplate = createRestTemplate("shiro", "123456");
        String res = restTemplate.getForObject("http://localhost:8080/api/hello", String.class);
        System.out.println("**********" + res);
    }

    /**
     * httpclient 校验shiro
     * https://stackoverflow.com/questions/14873628/apache-shiro-authcbasic-authentication-using-java-and-apache-httpclient
     */
    @Test
    public void testHttpClient() {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {

            httpclient.getCredentialsProvider().setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials("shiro", "123456"));

            HttpGet httpget = new HttpGet("http://localhost:8080/api/hello");

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            System.out.println("executing request" + httpget.getRequestLine());

            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");
            System.out.println("Job Done!");
        } catch (Exception ex) {
//        Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

    }
}
