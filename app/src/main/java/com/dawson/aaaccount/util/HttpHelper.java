package com.dawson.aaaccount.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.CoreConnectionPNames;
//import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

@Deprecated
public class HttpHelper {
    //    private volatile HttpClient httpClient = null;
    public static final String HOST = "127.0.0.1";
    //	public  static  final String HOST="192.168.2.102";
//	public  static  final String HOST="hellojiangdong.xicp.net";
    // 服务器地址
    public static final String BASE_URL = "http://" + HOST + ":80/api/";
    // public static final String BASE_URL =
    // "http://hellojiangdong.xicp.net:27105/api/";
    // 服务器文件地址
    public static final String BASE_URL_FILE = "http://" + HOST + ":80/UserImages/";
    // public static final String BASE_URL_FILE =
    // "http://hellojiangdong.xicp.net:27105/UserImages/";
    // 推送代理服务器地址
    public static final String MQTT_BROKER_URL = "tcp://" + HOST + ":1883";
    // public static final String MQTT_BROKER_URL =
    // "tcp://hellojiangdong.xicp.net:26154";

    private static int timeout = 5000;// 超时时间 单位毫秒

    private static HttpHelper instance;

    public static HttpHelper getInstance() {
        if (instance == null) {
            instance = new HttpHelper();

        }
        return instance;
    }

    private HttpHelper() {
//        initHttpClient();
    }
//
//    private void initHttpClient() {
//        if (httpClient == null) {
//            httpClient = new DefaultHttpClient();
//            httpClient.getParams().setParameter(
//                    CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
//            httpClient.getParams().setParameter(
//                    CoreConnectionPNames.SO_TIMEOUT, timeout);
//        }
//    }
//
//    public synchronized String getRequest(final String url) {
//        initHttpClient();
//        HttpGet get = new HttpGet(BASE_URL + url);
//
//        try {
//            HttpResponse response = httpClient.execute(get);
//            if (response.getStatusLine().getStatusCode() == 200) {
//                return EntityUtils.toString(response.getEntity());
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            get.abort();
//        }
//        return "";
//    }
//
//    public synchronized String postRequest(String url, String params) {
//
//        HttpPost post = new HttpPost(BASE_URL + url);
//
//        try {
//            StringEntity se = new StringEntity(params, "utf-8");
//            se.setContentEncoding("utf-8");
//            se.setContentType("application/json");
//            post.setEntity(se);
//            HttpResponse response = httpClient.execute(post);
//            if (response.getStatusLine().getStatusCode() == 200) {
//                return EntityUtils.toString(response.getEntity());
//
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            post.abort();
//        }
//        return "";
//    }
//
//    /**
//     * 上传文件
//     *
//     * @param url
//     * @param data     文件数据
//     * @param fileName 文件名字
//     * @return
//     */
//    public synchronized String postFile(String url, byte[] data, String fileName) {
//
//        HttpPost post = new HttpPost(BASE_URL + url);
//
//        try {
//            MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
//
//            meBuilder
//                    .addBinaryBody("image", data,
//                            org.apache.http.entity.ContentType.DEFAULT_BINARY,
//                            fileName);
//            post.setEntity(meBuilder.build());
//            HttpResponse response = httpClient.execute(post);
//            if (response.getStatusLine().getStatusCode() == 200) {
//                return EntityUtils.toString(response.getEntity());
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            post.abort();
//        }
//        return "";
//    }

    /**
     * 下载图片
     *
     * @param urlString 图片地址
     * @return
     */
    public static Bitmap getPicture(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream inStream = conn.getInputStream();
            return BitmapFactory.decodeStream(inStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
