package bobo.shanche.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import bobo.shanche.R;
import bobo.shanche.base.BaseApp;

/**
 * Created by bobo1 on 2017/1/25.
 */
public class NetUtils {
    @Nullable
    public static String post(Context context,String urlPath, Map<String, String> params) {

        if (params == null || params.size() == 0) {
            return get(urlPath);
        }
        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection connection = null;
        StringBuffer body = getParamString(params);
        byte[] data = body.toString().getBytes();
        try {
            URL url = new URL(urlPath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android "+android.os.Build.VERSION.RELEASE+"; "+android.os.Build.MODEL+" AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36 Html5Plus/1.0");
            connection.setRequestProperty("Accept","application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Accept-Language","zh-CN,en-US;q=0.8");
            connection.setRequestProperty("X-Requested-With","XMLHttpRequest");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));
            os = connection.getOutputStream();
            os.write(data);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,BaseApp.getAppContext().getString(R.string.noNet),Toast.LENGTH_LONG).show();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();

            }
        }
        return null;
    }

    private static StringBuffer getParamString(Map<String, String> params) {
        StringBuffer result = new StringBuffer();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            String key = param.getKey();
            String value = param.getValue();
            result.append(key).append('=').append(value);
            if (iterator.hasNext()) {
                result.append('&');
            }
        }
        return result;
    }

    @Nullable
    public static String get(String urlPath) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            URL url = new URL(urlPath);
            //获得URL对象
            connection = (HttpURLConnection) url.openConnection();
            //获得HttpURLConnection对象
            connection.setRequestMethod("GET");
            // 默认为GET
            connection.setUseCaches(false);
            //不使用缓存
            connection.setConnectTimeout(5000);
            //设置超时时间
            connection.setReadTimeout(5000);
            //设置读取超时时间
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android "+android.os.Build.VERSION.RELEASE+"; "+android.os.Build.MODEL+" AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36 Html5Plus/1.0");
            connection.setRequestProperty("Accept","application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Accept-Language","zh-CN,en-US;q=0.8");
            connection.setRequestProperty("X-Requested-With","XMLHttpRequest");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setDoInput(true);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //相应码是否为200
                is = connection.getInputStream();
                //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //包装字节流为字符流
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) BaseApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}
