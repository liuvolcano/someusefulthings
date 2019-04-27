package com.volcano.usefulthings.base.util;


import com.volcano.usefulthings.base.util.exception.ServiceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by liu-can on 2018/1/24.
 */
public class HttpUtil {

    private static String charset="utf-8";

    private static Integer connectTimeout = null;

    private static Integer socketTimeout = null;

    private static String proxyHost = null;

    private static Integer proxyPort = null;

    /**
     * 处理Get请求
     * @param url
     * @return
     * @throws Exception
     * @throws IOException
     */
    public static String doGet(String url) throws Exception {
        //获取HttpURLConnection对象，用于提交http请求
        HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection(new URL(url));
        httpURLConnection.setRequestProperty("Accept-Charset", charset);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        if (httpURLConnection.getResponseCode() >= 300) {
            throw new ServiceException("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }
        //获取从百度服务器发送过来的数据
        String resultBuffer=readData(httpURLConnection);
        return resultBuffer.toString();
    }

    /**
     * 处理Post请求
     * @param url
     * @param parameterMap
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String,Object> parameterMap) throws Exception {
        String parameterBuffer=mapToString(parameterMap);     //将parameterMap转成对应String
        //获取HttpURLConnection对象，用于提交http请求
        HttpURLConnection httpURLConnection = getHttpURLConnection(url,parameterBuffer);
        //提交参数
        submitData(httpURLConnection,parameterBuffer.toString());
        if (httpURLConnection.getResponseCode() >= 300) {
            throw new ServiceException("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }
        String resultBuffer=readData(httpURLConnection);
        return resultBuffer;
    }

    /**
     * 获取httpURLConnection
     * @param url
     * @param parameter
     * @return
     * @throws Exception
     */
    private static HttpURLConnection getHttpURLConnection(String url,String parameter) throws Exception{
        HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection(new URL(url));
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept-Charset", charset);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameter.length()));
        return httpURLConnection;
    }

    /**
     * 将Map转成字符串
     * @param parameterMap
     * @return String
     */
    private static String mapToString(Map<String,Object> parameterMap){
        StringBuffer parameterBuffer = new StringBuffer();
        if (parameterMap != null) {
            Iterator<String> iterator = parameterMap.keySet().iterator();
            String key = null;
            String value = null;
            while (iterator.hasNext()) {
                key = iterator.next();
                if (parameterMap.get(key) != null) {
                    value =parameterMap.get(key).toString();
                } else {
                    value = "";
                }
                parameterBuffer.append(key).append("=").append(value);
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
        }
        return parameterBuffer.toString();
    }

    /**
     * 获取输出流,提交数据
     * @param httpURLConnection
     * @param parameter
     */
    private static void submitData(HttpURLConnection httpURLConnection,String parameter) throws IOException{
        OutputStreamWriter outputStreamWriter=null;//输出流，用于提交参数
        try{
            outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
            outputStreamWriter.write(parameter);   //提交参数
            outputStreamWriter.flush();
        }catch(IOException e){
            throw e;
        }finally {
            if (outputStreamWriter != null) {
                try{
                    outputStreamWriter.close();//关闭输出流
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取从百度服务器发送过来的数据
     * @param httpURLConnection
     * @return
     */
    private static String readData(HttpURLConnection httpURLConnection) throws IOException{
        BufferedReader reader = null;//输入流，用于获取从百度服务器发送过来的数据
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        try{
            reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"utf-8"));
            while ((tempLine = reader.readLine()) != null) {
//                System.out.println("tempLine:"+tempLine);
                resultBuffer.append(tempLine);//将获取到的数据保存成String字符串
//                tempLine = reader.readLine();
            }
        }catch (IOException e){
            throw e;
        }finally {
            if(reader!=null){
                try{
                    reader.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultBuffer.toString();
    }

    /**
     * 获取URLConnection实例对象
     * @param localURL
     * @return
     * @throws IOException
     */
    private static URLConnection openConnection(URL localURL) throws IOException {
        URLConnection connection;
        if (proxyHost != null && proxyPort != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            connection = localURL.openConnection(proxy);
        } else {
            connection = localURL.openConnection();
        }
        return connection;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
