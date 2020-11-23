package com.lucky.web.httpclient;

import com.lucky.web.conf.WebConfig;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.exception.HttpClientRequestException;
import com.lucky.web.exception.NotFindRequestException;
import com.lucky.web.webfile.MultipartFile;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientCall {

    private static final Logger log = LogManager.getLogger(HttpClientCall.class);
    /** Web配置类*/
    private static WebConfig webConfig = WebConfig.getWebConfig();

    /**
     * 方法体说明：向远程接口发起请求，返回字符串类型结果
     *
     * @param url           接口地址
     * @param requestMethod 请求类型
     * @param params        传递参数
     * @param auth          访问凭证(username,password)
     * @return String类型返回结果
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String call(String url, RequestMethod requestMethod, Map<String, Object> params, String... auth) throws IOException, URISyntaxException {
        //创建HttpClient连接对象
        CloseableHttpClient client = HttpClients.createDefault();
        HttpRequestBase method = getHttpRequestObject(url, params, requestMethod);
        method.setConfig(getRequestConfig());
        method.addHeader("Content-Type", "application/x-www-form-urlencoded");
        HttpResponse response = client.execute(method, getHttpClientContext(auth));
        String methodResult = responseToString(response);
        client.close();
        return methodResult;
    }

    /**
     * 方法体说明：向远程接口发起请求，返回byte[]类型结果
     *
     * @param url           接口地址
     * @param requestMethod 请求类型
     * @param params        传递参数
     * @param auth          访问凭证(username,password)
     * @return byte[]类型返回结果
     * @throws IOException
     * @throws URISyntaxException
     */
    public static byte[] callByte(String url, RequestMethod requestMethod, Map<String, Object> params, String... auth) throws IOException, URISyntaxException {
        //创建HttpClient连接对象
        CloseableHttpClient client = HttpClients.createDefault();
        HttpRequestBase method = getHttpRequestObject(url, params, requestMethod);
        method.setConfig(getRequestConfig());
        method.addHeader("Content-Type", "application/x-www-form-urlencoded");
        HttpResponse response = client.execute(method, getHttpClientContext(auth));
        byte[] methodResult = responseToByte(response);
        client.close();
        return methodResult;
    }

    /**
     * 方法体说明：向远程接口发起GET请求，返回字符串类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param auth   访问凭证(username,password)
     * @return String 返回结果
     */
    public static String getCall(String url, Map<String, Object> params, String... auth) throws IOException, URISyntaxException {
        return call(url, RequestMethod.GET, params, auth);
    }

    /**
     * 方法体说明：向远程接口发起GET请求，返回Byte byte[]类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param auth   访问凭证(username,password)
     * @return byte[]类型返回结果
     */
    public static byte[] getCallByte(String url, Map<String, Object> params, String... auth) throws IOException, URISyntaxException {
        return callByte(url, RequestMethod.GET, params, auth);
    }

    /**
     * 方法体说明：向远程接口发起GET请求，返回字符串类型结果
     *
     * @param url  接口地址
     * @param auth 访问凭证(username,password)
     * @return String 返回结果
     */
    public static String getCall(String url, String... auth) throws IOException, URISyntaxException {
        return call(url, RequestMethod.GET, new HashMap<>(), auth);
    }

    /**
     * 方法体说明：向远程接口发起POST请求，返回字符串类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param auth   访问凭证(username,password)
     * @return String 返回结果
     */
    public static String postCall(String url, Map<String, Object> params, String... auth) throws IOException, URISyntaxException {
        return call(url, RequestMethod.POST, params, auth);
    }

    /**
     * 方法体说明：向远程接口发起POST请求，返回byte[]类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param auth   访问凭证(username,password)
     * @return byte[]类型返回结果
     */
    public static byte[] postCallByte(String url, Map<String, Object> params, String... auth) throws IOException, URISyntaxException {
        return callByte(url, RequestMethod.POST, params, auth);
    }

    /**
     * 方法体说明：向远程接口发起PUT请求，返回字符串类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param auth   访问凭证(username,password)
     * @return String 返回结果
     */
    public static String putCall(String url, Map<String, Object> params, String... auth) throws IOException, URISyntaxException {
        return call(url, RequestMethod.PUT, params, auth);
    }

    /**
     * 方法体说明：向远程接口发起PUT请求，返回byte[]类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param auth   访问凭证(username,password)
     * @return byte[]类型返回结果
     */
    public static byte[] putCallByte(String url, Map<String, Object> params, String... auth) throws IOException, URISyntaxException {
        return callByte(url, RequestMethod.PUT, params, auth);
    }

    /**
     * 方法体说明：向远程接口发起DELETE请求，返回字符串类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param auth   访问凭证(username,password)
     * @return String 返回结果
     */
    public static String deleteCall(String url, Map<String, Object> params, String... auth) throws IOException, URISyntaxException {
        return call(url, RequestMethod.DELETE, params, auth);
    }

    /**
     * 方法体说明：向远程接口发起DELETE请求，返回byte[]类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param auth   访问凭证(username,password)
     * @return byte[]类型返回结果
     */
    public static byte[] deleteCallByte(String url, Map<String, Object> params, String... auth) throws IOException, URISyntaxException {
        return callByte(url, RequestMethod.DELETE, params, auth);
    }

    /**
     * 方法体说明：向远程接口发起DELETE请求，返回字符串类型结果
     *
     * @param url  接口地址
     * @param auth 访问凭证(username,password)
     * @return String 返回结果
     */
    public static String deleteCall(String url, String... auth) throws IOException, URISyntaxException {
        return call(url, RequestMethod.DELETE, new HashMap<>(), auth);
    }


    /**
     * 注：只有返回数据为JSON格式时才有效
     * 向远程接口发起GET请求，返回Object类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param type   转换的目的类型
     * @param auth   访问凭证(username,password)
     * @return 返回对象类型的结果
     * @throws IOException
     */
    public static Object getCall(String url, Map<String, Object> params, Type type, String... auth) throws Exception {
        String result = call(url, RequestMethod.GET, params, auth);
        return webConfig.getJsonSerializationScheme().deserialization(type,result);
    }


    /**
     * 注：只有返回数据为JSON格式时才有效
     * 向远程接口发起GET请求，返回Object类型结果
     *
     * @param url  接口地址
     * @param type 转换的目的类型
     * @param auth 访问凭证(username,password)
     * @return 返回对象类型的结果
     * @throws IOException
     */
    public static Object getCall(String url, Type type, String... auth) throws Exception {
        String result = call(url, RequestMethod.GET, new HashMap<>(), auth);
        return webConfig.getJsonSerializationScheme().deserialization(type,result);
    }


    /**
     * 注：只有返回数据为JSON格式时才有效
     * 向远程接口发起POST请求，返回Object类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param type   转换的目的类型
     * @param auth   访问凭证(username,password)
     * @return 返回对象类型的结果
     * @throws IOException
     */
    public static Object postCall(String url, Map<String, Object> params, Type type, String... auth) throws Exception {
        String result = call(url, RequestMethod.POST, params, auth);
        return webConfig.getJsonSerializationScheme().deserialization(type,result);
    }


    /**
     * 注：只有返回数据为JSON格式时才有效
     * 向远程接口发起PUT请求，返回Object类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param type   转换的目的类型
     * @param auth   访问凭证(username,password)
     * @return 返回对象类型的结果
     * @throws IOException
     */
    public static Object putCall(String url, Map<String, Object> params, Type type, String... auth) throws Exception {
        String result = call(url, RequestMethod.PUT, params, auth);
        return webConfig.getJsonSerializationScheme().deserialization(type,result);
    }


    /**
     * 注：只有返回数据为JSON格式时才有效
     * 向远程接口发起DELETE请求，返回Object类型结果
     *
     * @param url    接口地址
     * @param params 传递参数
     * @param type   转换的目的类型
     * @param auth   访问凭证(username,password)
     * @return 返回对象类型的结果
     * @throws IOException
     */
    public static Object deleteCall(String url, Map<String, Object> params, Type type, String... auth) throws Exception {
        String result = call(url, RequestMethod.DELETE, params, auth);
        return webConfig.getJsonSerializationScheme().deserialization(type,result);
    }


    /**
     * 注：只有返回数据为JSON格式时才有效
     * 向远程接口发起DELETE请求，返回Object类型结果
     *
     * @param url  接口地址
     * @param type 转换的目的类型
     * @param auth 访问凭证(username,password)
     * @return 返回对象类型的结果
     * @throws IOException
     */
    public static Object deleteCall(String url, Type type, String... auth) throws Exception {
        String result = call(url, RequestMethod.DELETE, new HashMap<>(), auth);
        return webConfig.getJsonSerializationScheme().deserialization(type,result);
    }

    /**
     * 获取httpClient本地上下文
     *
     * @param auth
     * @return HttpClientContext对象
     */
    private static HttpClientContext getHttpClientContext(String... auth) {
        HttpClientContext context = null;
        if (!(auth == null || auth.length == 0)) {
            String username = auth[0];
            String password = auth[1];
            UsernamePasswordCredentials credt = new UsernamePasswordCredentials(username, password);
            //凭据提供器
            CredentialsProvider provider = new BasicCredentialsProvider();
            //凭据的匹配范围
            provider.setCredentials(AuthScope.ANY, credt);
            context = HttpClientContext.create();
            context.setCredentialsProvider(provider);
        }
        return context;
    }

    /**
     * 处理响应结果，将响应结果转化为String类型
     *
     * @param response HttpResponse对象
     * @return String结果
     * @throws IOException
     */
    private static String responseToString(HttpResponse response) throws IOException {
        int code = response.getStatusLine().getStatusCode();
        log.debug("Response Status ==> " + code);
        if (code == 200) {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } else {
            log.error("远程服务异常，未能正常响应..");
            throw new HttpClientRequestException("远程服务异常，访问失败");
        }
    }

    /**
     * 处理响应结果，将响应结果转化为byte[]类型
     *
     * @param response HttpResponse对象
     * @return byte[]
     * @throws IOException
     */
    private static byte[] responseToByte(HttpResponse response) throws IOException {
        int code = response.getStatusLine().getStatusCode();
        log.debug("Response Status ==> " + code);
        if (code == 200) {
            return EntityUtils.toByteArray(response.getEntity());
        } else {
            log.error("远程服务异常，未能正常响应..");
            throw new HttpClientRequestException("远程服务异常，访问失败");
        }
    }

    /**
     * 得到Request的配置对对象
     *
     * @return
     */
    private static RequestConfig getRequestConfig() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(webConfig.getConnectTimeout()).setConnectionRequestTimeout(webConfig.getRequestTimeout())
                .setSocketTimeout(webConfig.getSocketTimeout()).build();
        return requestConfig;
    }

    /**
     * 得到HttpGet/HttpPost/HttpDelete/HttpPut对象
     *
     * @param url           url地址
     * @param params        参数列表
     * @param requestMethod 请求类型
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private static HttpRequestBase getHttpRequestObject(String url, Map<String, Object> params, RequestMethod requestMethod) throws IOException, URISyntaxException {
        if (requestMethod == RequestMethod.GET) {
            log.debug("HttpClient Request => [-GET-] " + url);
            if (isNullParam(params)) {
                return new HttpGet(url);
            } else {
                URIBuilder builder = new URIBuilder(url);
                for (String key : params.keySet())
                    builder.addParameter(key, params.get(key).toString());
                return new HttpGet(builder.build());
            }
        } else if (requestMethod == RequestMethod.DELETE) {
            log.debug("HttpClient Request => [-DELETE-] " + url);
            if (isNullParam(params)) {
                return new HttpDelete(url);
            } else {
                URIBuilder builder = new URIBuilder(url);
                for (String key : params.keySet())
                    builder.addParameter(key, params.get(key).toString());
                return new HttpDelete(builder.build());
            }
        } else if (requestMethod == RequestMethod.POST) {
            log.debug("HttpClient Request => [-POST-] " + url);
            if (isNullParam(params)) {
                return new HttpPost(url);
            } else {
                HttpPost post = new HttpPost(url);
                post.setEntity(getUrlEncodedFormEntity(params));
                return post;
            }
        } else if (requestMethod == RequestMethod.PUT) {
            log.debug("HttpClient Request => [-PUT-] " + url);
            if (isNullParam(params)) {
                return new HttpPut(url);
            } else {
                HttpPut put = new HttpPut(url);
                put.setEntity(getUrlEncodedFormEntity(params));
                return put;
            }
        } else {
            log.error("Lucky目前不支持该请求 [-" + requestMethod + "-]");
            throw new NotFindRequestException("Lucky目前不支持该请求 [-" + requestMethod + "-]");
        }
    }

    /**
     * 判断是否有参数
     *
     * @param params map
     * @return
     */
    private static boolean isNullParam(Map<String, Object> params) {
        if (params == null || params.isEmpty())
            return true;
        return false;
    }

    /**
     * 得到POST、PUT请求的参数
     *
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private static UrlEncodedFormEntity getUrlEncodedFormEntity(Map<String, Object> params) throws UnsupportedEncodingException {
        List<BasicNameValuePair> list = new ArrayList<>();
        for (String key : params.keySet())
            list.add(new BasicNameValuePair(key, params.get(key).toString()));
        return new UrlEncodedFormEntity(list);
    }

    /**
     * 上传文件到远程服务
     *
     * @param url                 url地址
     * @param multipartFileParams 包含文件参数的参数列表
     * @return String类型的返回结果
     */
    public static String uploadFile(String url, Map<String, Object> multipartFileParams) {
        return uploadFile(url, multipartFileParams, new HashMap<>());
    }


    /**
     * 上传文件到远程服务
     *
     * @param url                 url地址
     * @param multipartFileParams 包含文件参数的参数列表
     * @param headerParams        添加请求头(header)
     * @return String类型的返回结果
     */
    public static String uploadFile(String url, Map<String, Object> multipartFileParams, Map<String, String> headerParams) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            //添加header
            for (Map.Entry<String, String> e : headerParams.entrySet()) {
                httpPost.addHeader(e.getKey(), e.getValue());
            }
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//加上此行代码解决返回中文乱码问题
            for (Map.Entry<String, Object> e : multipartFileParams.entrySet()) {
                Class<?> paramValueClass = e.getValue().getClass();
                if (File.class == paramValueClass) {
                    File file = (File) e.getValue();
                    builder.addBinaryBody(e.getKey(), new FileInputStream(file), ContentType.MULTIPART_FORM_DATA, file.getName());//文件参数-File
                } else if (File[].class == paramValueClass) {
                    File[] files = (File[]) e.getValue();
                    for (File file : files)
                        builder.addBinaryBody(e.getKey(), new FileInputStream(file), ContentType.MULTIPART_FORM_DATA, file.getName());//文件参数-File[]
                } else if (MultipartFile.class == paramValueClass) {
                    MultipartFile mf = (MultipartFile) e.getValue();
                    builder.addBinaryBody(e.getKey(), mf.getInputStream(), ContentType.MULTIPART_FORM_DATA, mf.getFileName());//文件参数-MultipartFile
                } else if (MultipartFile[].class == paramValueClass) {
                    MultipartFile[] mfs = (MultipartFile[]) e.getValue();
                    for (MultipartFile mf : mfs)
                        builder.addBinaryBody(e.getKey(), mf.getInputStream(), ContentType.MULTIPART_FORM_DATA, mf.getFileName());//文件参数-MultipartFile[]
                } else {
                    builder.addTextBody(e.getKey(), e.getValue().toString(), ContentType.APPLICATION_JSON);// 设置请求中String类型的参数
                }

            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
