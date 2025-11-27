package com.demo.telegram.bot.handler.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

/**
 * 无效命令处理器
 */
@Component
public class InvalidCommandHandler extends AbstractTelegramCommandHandler {

    @Override
    public String handle(Long telegramUserId, String messageText, JSONObject webhook) {
        return formatErrorMessage("""
                ❓ 未知的命令

                💡 发送 /help 查看完整命令列表
                """);
    }

}
