package com.demo.telegram.bot.handler;

import com.demo.telegram.bot.handler.impl.BalanceCommandHandler;
import com.demo.telegram.bot.handler.impl.HelpCommandHandler;
import com.demo.telegram.bot.handler.impl.InvalidCommandHandler;
import com.demo.telegram.bot.handler.impl.StartCommandHandler;
import com.demo.telegram.bot.model.TelegramOperationType;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Telegram命令处理器工厂
 */
@Component
public class TelegramCommandHandlerFactory {

    private static final Logger logger = LoggerFactory.getLogger(TelegramCommandHandlerFactory.class);

    @Autowired
    private StartCommandHandler startCommandHandler;

    @Autowired
    private HelpCommandHandler helpCommandHandler;

    @Autowired
    private BalanceCommandHandler balanceCommandHandler;

    @Autowired
    private InvalidCommandHandler invalidCommandHandler;


    private final Map<TelegramOperationType, TelegramCommandHandler> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 注册命令处理器
        registerHandler(TelegramOperationType.START, startCommandHandler);
        registerHandler(TelegramOperationType.HELP, helpCommandHandler);
        registerHandler(TelegramOperationType.BALANCE, balanceCommandHandler);
        registerHandler(TelegramOperationType.INVALID, invalidCommandHandler);

        logger.info("Telegram命令处理器工厂初始化完成，注册了 {} 个处理器", handlerMap.size());
    }

    /**
     * 注册命令处理器
     */
    private void registerHandler(TelegramOperationType operationType, TelegramCommandHandler handler) {
        handlerMap.put(operationType, handler);
        logger.debug("注册命令处理器: {} -> {}", operationType, handler.getClass().getSimpleName());
    }

    /**
     * 处理命令
     */
    public String handle(TelegramOperationType operationType, Long telegramUserId, String messageText, JSONObject webhook) {
        TelegramCommandHandler handler = handlerMap.get(operationType);
        if (handler == null) {
            logger.warn("未找到命令处理器: {}", operationType);
            handler = invalidCommandHandler;
        }

        try {
            String response = handler.handle(telegramUserId, messageText, webhook);
            logger.info("命令处理完成: operationType={}, telegramUserId={}, responseLength={}",
                    operationType, telegramUserId, response != null ? response.length() : 0);

            return response;

        } catch (Exception e) {
            logger.error("命令处理异常: operationType={}, telegramUserId={}", operationType, telegramUserId, e);
            return "❌ 处理命令时发生错误，请稍后重试";
        }
    }

    /**
     * 获取所有已注册的命令处理器
     */
    public Map<TelegramOperationType, TelegramCommandHandler> getAllHandlers() {
        return new HashMap<>(handlerMap);
    }
}
