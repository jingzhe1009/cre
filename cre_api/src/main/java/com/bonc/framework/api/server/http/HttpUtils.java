package com.bonc.framework.api.server.http;

import com.alibaba.fastjson.JSONObject;
import com.bonc.framework.util.PathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * httpClient Helper
 *
 * @author yangdx
 * @version 1.0.0
 */
public class HttpUtils {

    private static final Log log = LogFactory.getLog(HttpUtils.class);

    // 连接池对象
    private static PoolingHttpClientConnectionManager httpClientPool = null;

    private static final String API_ENGINE_CONFIGURATION = "api_source.properties";
    private static final String POOL_MAX_TOTAL = "api.http.pool.maxTotal";
    private static final String POOL_MAX_PER_ROUTE = "api.http.pool.maxPerRoute";
    private static final String DEFAULT_POOL_MAX_TOTAL = "1000";
    private static final String DEFAULT_POOL_MAX_PER_ROUTE = "100";

    static {
        final Properties properties = loadProperties();

        LayeredConnectionSocketFactory sslsf = null;

        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        // 构建连接池
        httpClientPool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        final Integer maxTotal = Integer.valueOf(properties.getProperty(POOL_MAX_TOTAL, DEFAULT_POOL_MAX_TOTAL));
        final Integer maxPerRoute = Integer.valueOf(properties.getProperty(POOL_MAX_PER_ROUTE, DEFAULT_POOL_MAX_PER_ROUTE));
        log.info("Http pool max total threads: " + maxTotal);
        log.info("Http pool max per route threads: " + maxPerRoute);
        httpClientPool.setMaxTotal(maxTotal);               // 支持的最大并发数
        httpClientPool.setDefaultMaxPerRoute(maxPerRoute);  // 每个url路径的最大并发数
    }

    private HttpUtils() {
    }

    private static Properties loadProperties() {
        final String configFile = "config" + File.separator + API_ENGINE_CONFIGURATION;
        final String path = PathUtil.getConfigPath(configFile);

        Properties prop = new Properties();

        InputStream in = null;
        try {
            if (path == null) {
                in = HttpUtils.class.getClassLoader().getResourceAsStream(API_ENGINE_CONFIGURATION);
            } else {
                log.info("Api engine config file: " + path);
                in = new FileInputStream(path);
            }
            prop.load(in);
        } catch (Exception e) {
            log.error(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn(e);
                }
            }
        }
        return prop;
    }

    /**
     * 获取HttpClient对象
     *
     * @return HttpClient
     */
    private static HttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(httpClientPool)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36")//浏览器版本
                .setRetryHandler(new DefaultHttpRequestRetryHandler(2, true))//请求超时后重发2次
                .build();

        return httpClient;
    }

    /**
     * 执行HTTP请求
     *
     * @param method 请求对象
     * @throws IOException
     */
    public static String doMethod(HttpRequestBase method) throws IOException {
        try {
            HttpResponse response = getHttpClient().execute(method);

            return EntityUtils.toString(response.getEntity());
        } finally {
            method.releaseConnection();
        }
    }

    /**
     * 执行 POST请求
     *
     * @param uri    请求url
     * @param params 参数集合
     * @return 执行结果字符串
     */
    public static String doPost(String uri, Map<String, String> params) throws IOException {
        return doPost(uri, new UrlEncodedFormEntity(map2Pairs(params), "UTF-8"));
    }

    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.warn("send POST Exception！" + e + ";send param:" + param);
            log.error(e);
            return null;
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
                return null;
            }
        }
        return result;
    }
    
    /**
     * 执行 POST请求
     *
     * @param uri    请求url
     * @param params 参数集合
     * @return 执行结果字符串
     */
    
    public static String doPost(String uri, JSONObject json) throws IOException {
        return doPost(uri, new UrlEncodedFormEntity(map2Pairs(json), "UTF-8"));
    }

    /**
     * 执行 POST请求
     *
     * @param uri       请求url
     * @param jsonPrams json参数
     * @return 执行结果字符串
     */
    public static String doPost(String uri, String jsonPrams) throws IOException {
        return doPost(uri, new StringEntity(jsonPrams, "UTF-8"));
    }

    /**
     * 执行POST请求
     *
     * @param uri    url地址
     * @param entity StringEntity 对象
     * @return 执行结果字符串
     */
    public static String doPost(String uri, StringEntity entity) throws IOException {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);

        return doMethod(httpPost);
    }

    /**
     * 执行GET请求
     *
     * @param uri url地址
     * @return 返回字符串
     */
    public static String doGet(String uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);

        return doMethod(httpGet);
    }

    /**
     * 参数集合转换成 NameValuePair
     *
     * @param params 参数集合
     * @return NameValuePair集合
     */
    private static List<NameValuePair> map2Pairs(Map<String, String> params) {
        List<NameValuePair> listPairs = new ArrayList<>();

        if (null != params) {
            Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                listPairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        return listPairs;
    }

    /**
     * 参数集合转换成 NameValuePair
     *
     * @param json 参数集合
     * @return NameValuePair集合
     */
    private static List<NameValuePair> map2Pairs(JSONObject json) {
        List<NameValuePair> listPairs = new ArrayList<>();

        if (null != json) {
            Iterator<Map.Entry<String, Object>> iter = json.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Object> entry = iter.next();
                listPairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
        }

        return listPairs;
    }
}
