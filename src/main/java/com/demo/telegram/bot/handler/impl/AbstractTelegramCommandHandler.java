package com.demo.telegram.bot.handler.impl;

import com.demo.telegram.bot.handler.TelegramCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Telegram命令处理器抽象基类
 */
public abstract class AbstractTelegramCommandHandler implements TelegramCommandHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 格式化错误消息
     */
    protected String formatErrorMessage(String error) {
        return String.format("❌ 错误: %s", error);
    }

    /**
     * 格式化成功消息
     */
    protected String formatSuccessMessage(String message) {
        return String.format("✅ %s", message);
    }

    /**
     * 格式化信息消息
     */
    protected String formatInfoMessage(String message) {
        return String.format("ℹ️ %s", message);
    }

    /**
     * 格式化警告消息
     */
    protected String formatWarningMessage(String message) {
        return String.format("⚠️ %s", message);
    }

    /**
     * 生成帮助信息
     */
    protected String generateHelpInfo() {
        return """
                <b>显示帮助信息</b>

                🤖 <b>Demo版本功能说明:</b>
                • 所有功能都可直接使用
                • 显示的数据均为模拟演示数据
                • 用于展示Telegram机器人架构

                📋 <b>可用命令:</b>
                /start - 欢迎消息和功能介绍
                /help - 显示此帮助信息
                /balance - 余额查询（模拟数据）

                💡 <b>提示:</b>
                • 发送任何命令都会正常响应
                • 这是完全无状态的Demo版本
                """;
    }
}
