package com.demo.telegram.bot.handler;

import com.alibaba.fastjson.JSONObject;

/**
 * Telegram命令处理器接口
 */
public interface TelegramCommandHandler {

    /**
     * 处理命令
     *
     * @param telegramUserId Telegram用户ID
     * @param messageText    消息文本
     * @param webhook        完整的webhook数据
     * @return 响应文本
     */
    String handle(Long telegramUserId, String messageText, JSONObject webhook);
}
