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
     * @param url 目标URL
     * @param header 请求头
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
     * @param url 目标URL
     * @param header 请求头
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
     * @param url 目标URL
     * @param header 请求头
     * @param params 参数
     * @param body 请求体
     * @param responseType 返回值类型
     * @return 返回对象
     * @throws IOException
     */
    public static <T> T post(String url, Map<String, String> header, Map<String, String> params, Object body, Class<T> responseType) throws IOException {
        return executeRequest(buildUrlWithParams(url, params), "POST", header, GSON.toJson(body), responseType);
    }

    /**
     * 文件下载
     *
     * @param url 目标URL
     * @param header 请求头
     * @param params 参数
     * @param outputPath 保存路径
     * @throws IOException
     */
    public static void downloadFile(String url, Map<String, String> header, Map<String, String> params, String outputPath) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            String fullUrl = buildUrlWithParams(url, params);
            connection = (HttpURLConnection) new URL(fullUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            if (header != null) {
                header.forEach(connection::setRequestProperty);
            }
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Server returned HTTP " + statusCode);
            }
            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(outputPath);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 下载文件到内存
     *
     * @param url 目标URL
     * @param header 请求头
     * @param params 查询参数
     * @return 包含文件内容的字节数组
     * @throws IOException
     */
    public static byte[] downloadToMemory(String url, Map<String, String> header, Map<String, String> params) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            String fullUrl = buildUrlWithParams(url, params);
            connection = (HttpURLConnection) new URL(fullUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            if (header != null) {
                header.forEach(connection::setRequestProperty);
            }
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("服务器返回 HTTP " + statusCode);
            }
            inputStream = connection.getInputStream();
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 下载文件到内存（带进度回调）
     *
     * @param url 目标URL
     * @param header 请求头
     * @param params 查询参数
     * @param progressCallback 进度回调接口
     * @return 包含文件内容的字节数组
     * @throws IOException
     */
    public static byte[] downloadToMemory(String url, Map<String, String> header, Map<String, String> params, ProgressCallback progressCallback) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            String fullUrl = buildUrlWithParams(url, params);
            connection = (HttpURLConnection) new URL(fullUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            if (header != null) {
                header.forEach(connection::setRequestProperty);
            }
            int contentLength = connection.getContentLength();
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Server returned HTTP " + statusCode);
            }
            inputStream = connection.getInputStream();
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            int totalRead = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                if (progressCallback != null) {
                    progressCallback.onProgress(totalRead, contentLength);
                }
            }
            return outputStream.toByteArray();
        } finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
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

    public interface ProgressCallback {
        void onProgress(int bytesRead, int totalBytes);
    }
}
