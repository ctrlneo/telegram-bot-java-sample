package com.demo.telegram.bot.handler.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

/**
 * 开始命令处理器 - Demo版本
 */
@Component
public class StartCommandHandler extends AbstractTelegramCommandHandler {

    @Override
    public String handle(Long telegramUserId, String messageText, JSONObject webhook) {
        return formatInfoMessage("""
                🤖 欢迎使用Telegram机器人Demo！

                这是一个基于Spring Boot的Telegram机器人示例项目。

                📋 <b>功能特色:</b>
                • 命令处理系统
                • 模块化架构
                • JDK 21文本块支持
                • 无状态设计

                🚀 <b>快速开始:</b>
                • 发送 /help 查看所有命令
                • 发送 /balance 查询余额（模拟数据）
                • 所有功能都可直接使用！

                💡 发送 /help 查看所有可用命令
                """);
    }
}
