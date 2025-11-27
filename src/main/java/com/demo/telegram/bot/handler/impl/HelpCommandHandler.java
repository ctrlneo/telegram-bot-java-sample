package com.demo.telegram.bot.handler.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

/**
 * 帮助命令处理器
 */
@Component
public class HelpCommandHandler extends AbstractTelegramCommandHandler {

    @Override
    public String handle(Long telegramUserId, String messageText, JSONObject webhook) {
        return generateHelpInfo();
    }

}
