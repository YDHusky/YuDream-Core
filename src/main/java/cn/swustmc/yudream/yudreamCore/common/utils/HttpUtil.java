package cn.swustmc.yudream.yudreamCore.common.utils;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * cn.swustmc.yudream.yudreamCore.common.utils.HttpUtil
 *
 * @author xinyihl
 * * @date 2025/5/19
 */
public class HttpUtil {
    private static final Gson GSON = new Gson();
    private static final int connectTimeout = 5000;
    private static final int readTimeout = 5000;

    /**
     * GET请求
     *
     * @param url 请求连接
     * @param header http头
     * @param params 参数
     * @param responseType 返回值类型
     * @return 返回对象
     * @throws IOException
     */
    public static <T> T get(String url, Map<String, String> header, Map<String, String> params, Class<T> responseType) throws IOException {
        return executeRequest(buildUrlWithParams(url, params), "GET", header, null, responseType);
    }

    /**
     * POST请求
     *
     * @param url 请求连接
     * @param header http头
     * @param body 请求体
     * @param responseType 返回值类型
     * @return 返回对象
     * @throws IOException
     */
    public static <T> T post(String url, Map<String, String> header, Object body, Class<T> responseType) throws IOException {
        return executeRequest(url, "POST", header, GSON.toJson(body), responseType);
    }

    /**
     * POST请求
     *
     * @param url 请求连接
     * @param header http头
     * @param params 参数
     * @param body 请求体
     * @param responseType 返回值类型
     * @return 返回对象
     * @throws IOException
     */
    public static <T> T post(String url, Map<String, String> header, Map<String, String> params, Object body, Class<T> responseType) throws IOException {
        return executeRequest(buildUrlWithParams(url, params), "POST", header, GSON.toJson(body), responseType);
    }

    private static <T> T executeRequest(String urlStr, String method, Map<String, String> headers, String requestBody, Class<T> responseType) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            if (headers != null) {
                headers.forEach(connection::setRequestProperty);
            }



            if (requestBody != null && (method.equals("POST") || method.equals("PUT"))) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream();
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                    writer.write(requestBody);
                    writer.flush();
                }
            }

            int statusCode = connection.getResponseCode();
            if (statusCode >= 200 && statusCode < 300 || statusCode == 400) {
                return parseResponse(connection, responseType);
            } else {
                String errorBody = readErrorStream(connection);
                throw new IOException("HTTP Request Failed: " + statusCode + " - " + errorBody);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static <T> T parseResponse(HttpURLConnection connection, Class<T> type) throws IOException {
        try (InputStream is = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return GSON.fromJson(reader, type);
        }
    }

    private static String readErrorStream(HttpURLConnection connection) {
        try (InputStream es = connection.getErrorStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(es, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (IOException e) {
            return "Error reading error stream: " + e.getMessage();
        }
    }

    private static String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) return baseUrl;

        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        if (!baseUrl.contains("?")) {
            urlBuilder.append("?");
        } else if (!baseUrl.endsWith("?")) {
            urlBuilder.append("&");
        }

        params.forEach((key, value) -> {
            if (key != null && value != null) {
                urlBuilder.append(encode(key))
                        .append("=")
                        .append(encode(value))
                        .append("&");
            }
        });

        if (urlBuilder.charAt(urlBuilder.length() - 1) == '&') {
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }

        return urlBuilder.toString();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
