package com.demo.telegram.bot.model;

import java.util.Arrays;

/**
 * Telegram Bot操作类型枚举
 */
public enum TelegramOperationType {
    /**
     * 开始命令
     */
    START("开始命令", "/start"),

    /**
     * 帮助请求
     */
    HELP("显示帮助信息", "/help"),

    /**
     * 查余额
     */
    BALANCE("查余额", "/balance"),

    /**
     * 无效命令
     */
    INVALID("无效命令", null);

    private final String description;
    private final String commandPrefix;

    TelegramOperationType(String description, String commandPrefix) {
        this.description = description;
        this.commandPrefix = commandPrefix;
    }

    public String getDescription() {
        return description;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    /**
     * 根据消息文本匹配对应的操作类型
     */
    public static TelegramOperationType matchCommand(String messageText) {
        if (messageText == null || messageText.trim().isEmpty()) {
            return INVALID;
        }

        String trimmedText = messageText.trim();

        // 按命令长度降序排序，优先匹配更长的命令，避免 /abcde 被 /ab 误匹配
        return Arrays.stream(values())
                .filter(type -> type.commandPrefix != null)
                .sorted((a, b) -> Integer.compare(b.commandPrefix.length(), a.commandPrefix.length()))
                .filter(type -> trimmedText.startsWith(type.commandPrefix))
                .findFirst()
                .orElse(INVALID);
    }
}
