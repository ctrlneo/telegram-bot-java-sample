package com.demo.telegram.bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Telegram Bot响应DTO
 * 封装Telegram Bot API的响应数据
 */
public class TelegramBotResponse {

    /**
     * Telegram Bot API方法名
     * 例如: sendMessage, editMessageText, answerCallbackQuery
     */
    private String method;

    /**
     * 聊天ID (用户ID或群组ID)
     */
    @JsonProperty("chat_id")
    private Long chatId;

    /**
     * 消息文本内容
     */
    private String text;

    /**
     * 消息解析模式
     * HTML, Markdown, MarkdownV2
     */
    @JsonProperty("parse_mode")
    private String parseMode;

    /**
     * 消息ID (用于编辑消息时使用)
     */
    @JsonProperty("message_id")
    private Integer messageId;

    /**
     * 回调查询ID (用于应答内联键盘回调)
     */
    @JsonProperty("callback_query_id")
    private String callbackQueryId;

    /**
     * 是否禁用Web页面预览
     */
    @JsonProperty("disable_web_page_preview")
    private Boolean disableWebPagePreview;

    /**
     * 是否禁用通知
     */
    @JsonProperty("disable_notification")
    private Boolean disableNotification;

    public TelegramBotResponse() {
    }

    public TelegramBotResponse(String method, Long chatId, String text) {
        this.method = method;
        this.chatId = chatId;
        this.text = text;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParseMode() {
        return parseMode;
    }

    public void setParseMode(String parseMode) {
        this.parseMode = parseMode;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getCallbackQueryId() {
        return callbackQueryId;
    }

    public void setCallbackQueryId(String callbackQueryId) {
        this.callbackQueryId = callbackQueryId;
    }

    public Boolean getDisableWebPagePreview() {
        return disableWebPagePreview;
    }

    public void setDisableWebPagePreview(Boolean disableWebPagePreview) {
        this.disableWebPagePreview = disableWebPagePreview;
    }

    public Boolean getDisableNotification() {
        return disableNotification;
    }

    public void setDisableNotification(Boolean disableNotification) {
        this.disableNotification = disableNotification;
    }

    /**
     * 创建简单的发送消息响应
     */
    public static TelegramBotResponse sendMessage(Long chatId, String text) {
        TelegramBotResponse response = new TelegramBotResponse("sendMessage", chatId, text);
        response.setParseMode("HTML");
        return response;
    }

    /**
     * 创建空响应 (用于webhook处理完成但不需要回复的情况)
     */
    public static TelegramBotResponse empty() {
        return new TelegramBotResponse();
    }
}
