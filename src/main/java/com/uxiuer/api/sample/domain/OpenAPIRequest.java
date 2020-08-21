package com.uxiuer.api.sample.domain;

import com.uxiuer.api.sample.common.enums.HttpMethod;
import okhttp3.MediaType;

import java.io.File;

/**
 * @Description:
 * @author: imart·deng
 * @date: 2020/7/1 16:59
 */
public class OpenAPIRequest {
    /**
     * 默认post json头
     */
    private static final MediaType APPLICATION_JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    /**
     * 多表单头
     */
    private static final MediaType MULTIPART_FORM_TYPE = MediaType.parse("multipart/form-data; charset=utf-8");

    /**
     * 主机，例：https://api.xxx.com
     */
    private String host;

    /**
     * 请求uri，例：/common/v1/documents
     */
    private String uri;

    /**
     * url请求参数，例：pageNum=1&pageSize=10
     */
    private String queryString;

    /**
     * 请求json包体，例：{"type":"WITHDRAW_COMBINE"}
     */
    private String requestBody;

    /**
     * 文件请求
     */
    private File file;

    /**
     * 请求方法
     */
    private HttpMethod method;

    /**
     * 请求类型
     */
    private MediaType mediaType = APPLICATION_JSON_TYPE;

    public OpenAPIRequest(String host, String uri, String queryString, String requestBody, HttpMethod method) {
        this.host = host;
        this.uri = uri;
        this.queryString = queryString;
        this.requestBody = requestBody;
        this.method = method;
    }

    public OpenAPIRequest(String host, String uri, File file, HttpMethod method) {
        this.host = host;
        this.uri = uri;
        this.file = file;
        this.method = method;
        this.mediaType = MULTIPART_FORM_TYPE;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getRequestUrl() {
        return isEmpty(queryString) ? host + uri : host + uri + "?" + queryString;
    }


    protected boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }
}
