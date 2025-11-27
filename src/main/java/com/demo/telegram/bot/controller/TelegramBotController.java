package com.demo.telegram.bot.controller;

import com.demo.telegram.bot.model.TelegramBotResponse;
import com.demo.telegram.bot.model.TelegramRequestParam;
import com.demo.telegram.bot.util.IpUtil;
import com.demo.telegram.bot.util.TelegramWebhookValidator;
import com.alibaba.fastjson.JSONObject;
import com.demo.telegram.bot.service.TelegramBotWebhookService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Telegram Bot Webhook控制器
 */
@RestController
@RequestMapping("/bot/rest/")
public class TelegramBotController {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotController.class);

    @Autowired
    private TelegramBotWebhookService webhookService;
    @Autowired
    private TelegramWebhookValidator telegramWebhookValidator;

    /**
     * 处理Telegram Bot Webhook请求
     *
     * @param webhook Telegram发送的Webhook数据
     * @return 响应结果
     */
    @PostMapping("/webhook")
    @ResponseBody
    public TelegramBotResponse handleWebhook(HttpServletRequest request, @RequestBody JSONObject webhook) {
        try {
            logger.info("收到Telegram webhook请求: updateId={}", webhook.getLong("update_id"));
            String clientIp = IpUtil.getClientIp(request);
            TelegramRequestParam param = new TelegramRequestParam();
            param.setBody(webhook);
            param.setRequest(request);
            param.setClientIp(clientIp);
            // telegram 请求信息验证
            telegramWebhookValidator.validateWebhook(param);

            // 处理webhook请求
            TelegramBotResponse response = webhookService.processWebhook(webhook);

            logger.info("Webhook处理完成: responseMethod={}, hasText={}",
                    response.getMethod(), response.getText() != null);
            // 返回响应
            return response;

        } catch (Exception e) {
            logger.error("处理Telegram webhook异常", e);
            // 返回空响应，HTTP 200状态码告知Telegram处理完成
            return TelegramBotResponse.empty();
        }
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Telegram Bot is running");
    }

    /**
     * 状态检查端点
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(java.util.Map.of(
                "status", "running",
                "timestamp", System.currentTimeMillis(),
                "version", "1.0.0"
        ));
    }
}
