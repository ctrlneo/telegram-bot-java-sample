package com.demo.telegram.bot.model;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;

public class TelegramRequestParam {

    /**
     * 请求内容
     */
    private JSONObject body;

    /**
     * 请求头信息
     */
    private HttpServletRequest request;
    /**
     * 客户端 IP
     */
    private String clientIp;

    public JSONObject getBody() {
        return body;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}
