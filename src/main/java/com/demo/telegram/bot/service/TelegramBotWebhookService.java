package com.demo.telegram.bot.service;

import com.demo.telegram.bot.handler.TelegramCommandHandlerFactory;
import com.demo.telegram.bot.model.TelegramBotResponse;
import com.demo.telegram.bot.model.TelegramOperationType;
import com.demo.telegram.bot.util.TelegramWebhookValidator;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Telegram Bot Webhook处理服务 - 简化版本
 */
@Service
public class TelegramBotWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotWebhookService.class);

    @Autowired
    private TelegramWebhookValidator webhookValidator;

    @Autowired
    private TelegramCommandHandlerFactory commandHandlerFactory;

    /**
     * 处理Telegram Webhook请求
     */
    public TelegramBotResponse processWebhook(JSONObject webhook) {
        Long telegramUserId = null;
        TelegramOperationType operationType = TelegramOperationType.INVALID;
        long startTime = System.currentTimeMillis();

        try {
            // 提取用户ID、聊天ID和消息文本
            telegramUserId = webhookValidator.extractTelegramUserId(webhook);
            Long chatId = webhookValidator.extractChatId(webhook);
            String messageText = webhookValidator.extractMessageText(webhook);

            if (telegramUserId == null) {
                logger.warn("无法提取Telegram用户ID");
                return TelegramBotResponse.empty();
            }

            if (chatId == null) {
                logger.warn("无法提取聊天ID");
                return TelegramBotResponse.empty();
            }

            if (messageText == null || messageText.trim().isEmpty()) {
                logger.info("收到空消息，忽略处理");
                return TelegramBotResponse.empty();
            }

            messageText = messageText.trim();

            // 只处理命令消息（以/开头）
            if (!messageText.startsWith("/")) {
                return TelegramBotResponse.empty();
            }

            logger.info("处理Telegram消息: userId={}, chatId={}, text={}", telegramUserId, chatId, messageText);

            // 匹配命令类型
            operationType = TelegramOperationType.matchCommand(messageText);
            logger.info("匹配到命令类型: {}", operationType);

            // 获取对应的处理器并处理
            String responseText = commandHandlerFactory.handle(operationType, telegramUserId, messageText, webhook);

            // 构造Telegram Bot API响应
            TelegramBotResponse response;
            if (responseText != null && !responseText.trim().isEmpty()) {
                response = TelegramBotResponse.sendMessage(chatId, responseText);
            } else {
                response = TelegramBotResponse.empty();
            }

            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Webhook处理完成: operationType={}, userId={}, executionTime={}ms",
                    operationType, telegramUserId, executionTime);

            return response;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("处理Telegram webhook异常: operationType={}, userId={}, executionTime={}ms",
                    operationType, telegramUserId, executionTime, e);

            // 构造错误响应
            Long chatId = webhookValidator.extractChatId(webhook);
            if (chatId != null) {
                return TelegramBotResponse.sendMessage(chatId, "抱歉，系统遇到了一些问题，请稍后重试。");
            } else if (telegramUserId != null) {
                return TelegramBotResponse.sendMessage(telegramUserId, "抱歉，系统遇到了一些问题，请稍后重试。");
            } else {
                return TelegramBotResponse.empty();
            }
        }
    }
}
