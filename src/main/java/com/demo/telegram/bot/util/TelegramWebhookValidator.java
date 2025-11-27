package com.demo.telegram.bot.util;

import com.demo.telegram.bot.config.TelegramBotConfig;
import com.demo.telegram.bot.exception.TelegramWebhookException;
import com.demo.telegram.bot.model.TelegramRequestParam;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Telegram Webhook数据验证工具类
 */
@Component
public class TelegramWebhookValidator {

    private static final Logger logger = LoggerFactory.getLogger(TelegramWebhookValidator.class);

    @Autowired
    private TelegramBotConfig  telegramBotConfig;

    /**
     * 验证Telegram Webhook请求
     */
    public void validateWebhook(TelegramRequestParam param) {
        // 1. IP白名单验证（必须）
        validateIpWhitelist(param);

        // 2. 请求格式验证（必须）
        validateRequestFormat(param);

        // 3. Secret Token验证（必须）
        validateSecretToken(param);

        // 4. 防重放攻击验证
        validateAntiReplay(param);

        // 5. 频率限制验证
        validateRateLimit(param);

        // 6. 消息内容安全检查
        validateMessageContent(param);

        // 7. 时间戳验证
        validateTimestamp(param);

        logger.info("Telegram webhook验证通过");
    }

    /**
     * 从webhook数据中提取Telegram用户ID
     */
    public Long extractTelegramUserId(JSONObject webhook) {
        try {
            // 尝试从消息中获取用户ID
            JSONObject message = webhook.getJSONObject("message");
            if (message != null) {
                JSONObject from = message.getJSONObject("from");
                if (from != null) {
                    return from.getLong("id");
                }
            }

            // 尝试从回调查询中获取用户ID
            JSONObject callbackQuery = webhook.getJSONObject("callback_query");
            if (callbackQuery != null) {
                JSONObject from = callbackQuery.getJSONObject("from");
                if (from != null) {
                    return from.getLong("id");
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从webhook数据中提取聊天ID
     */
    public Long extractChatId(JSONObject webhook) {
        try {
            // 尝试从消息中获取聊天ID
            JSONObject message = webhook.getJSONObject("message");
            if (message != null) {
                JSONObject chat = message.getJSONObject("chat");
                if (chat != null) {
                    return chat.getLong("id");
                }
            }

            // 尝试从回调查询中获取聊天ID
            JSONObject callbackQuery = webhook.getJSONObject("callback_query");
            if (callbackQuery != null) {
                JSONObject callbackMessage = callbackQuery.getJSONObject("message");
                if (callbackMessage != null) {
                    JSONObject chat = callbackMessage.getJSONObject("chat");
                    if (chat != null) {
                        return chat.getLong("id");
                    }
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从webhook数据中提取消息文本
     */
    public String extractMessageText(JSONObject webhook) {
        try {
            // 尝试从消息中获取文本
            JSONObject message = webhook.getJSONObject("message");
            if (message != null) {
                return message.getString("text");
            }

            // 尝试从回调查询中获取数据
            JSONObject callbackQuery = webhook.getJSONObject("callback_query");
            if (callbackQuery != null) {
                return callbackQuery.getString("data");
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证IP白名单
     */
    private void validateIpWhitelist(TelegramRequestParam param) {
        // 如果没有配置IP白名单，则跳过验证
        if (telegramBotConfig.getAllowedIps().isEmpty()) {
            logger.warn("未配置IP白名单，跳过IP验证");
            return;
        }

        if (!IpUtil.isIpAllowed(param.getClientIp(), telegramBotConfig.getAllowedIps())) {
            logger.warn("TelegramBotCheck error.  IP白名单验证失败: " + param.getClientIp() );
            throw new TelegramWebhookException(TelegramWebhookException.IP_WHITELIST_VALIDATION_FAILED);
        }
    }

    /**
     * 验证请求格式
     */
    private void validateRequestFormat(TelegramRequestParam param) {
        JSONObject body = param.getBody();
        if (body == null) {
            logger.warn("TelegramBotCheck error. 请求体为空");
            throw new TelegramWebhookException(TelegramWebhookException.REQUEST_FORMAT_VALIDATION_FAILED);
        }

        // 验证Telegram webhook必需的字段
        if (!body.containsKey("update_id")) {
            logger.warn("TelegramBotCheck error. 缺少update_id字段");
            throw new TelegramWebhookException(TelegramWebhookException.UPDATE_ID_NOT_FOUND);
        }

        // 至少要有message或者callback_query等字段之一
        if (!body.containsKey("message") &&
                !body.containsKey("callback_query") &&
                !body.containsKey("edited_message") &&
                !body.containsKey("inline_query")) {
            logger.warn("TelegramBotCheck error. 缺少有效的消息类型字段");
            throw new TelegramWebhookException(TelegramWebhookException.MESSAGE_TYPE_NOT_FOUND);
        }
    }

    /**
     * 验证Secret Token
     */
    private void validateSecretToken(TelegramRequestParam param) {
        if (StringUtils.isBlank(telegramBotConfig.getSecretToken())) {
            logger.warn("TelegramBotCheck error. 未配置Telegram Bot Secret Token");
            throw new TelegramWebhookException(TelegramWebhookException.SECRET_TOKEN_VALIDATION_FAILED);
        }

        String requestToken = param.getRequest().getHeader("X-Telegram-Bot-Api-Secret-Token");

        // 添加调试信息：尝试不同的header名称变体
        if (requestToken == null) {
            requestToken = param.getRequest().getHeader("x-telegram-bot-api-secret-token");
            logger.info("尝试小写header名称获取到的token: {}", requestToken);
        }

        if (!telegramBotConfig.getSecretToken().equals(requestToken)) {
            logger.warn("TelegramBotCheck error. Secret Token验证失败, requestToken:{}, configToken:{}",
                    requestToken, telegramBotConfig.getSecretToken());
            throw new TelegramWebhookException(TelegramWebhookException.SECRET_TOKEN_VALIDATION_FAILED);
        }
    }

    /**
     * 验证webhook数据基本格式
     */
    public boolean validateWebhookFormat(JSONObject webhook) {
        if (webhook == null) {
            return false;
        }

        // 检查是否包含update_id
        if (!webhook.containsKey("update_id")) {
            return false;
        }

        // 检查是否包含消息或回调查询
        return webhook.containsKey("message") || webhook.containsKey("callback_query");
    }


    /**
     * 防重放攻击验证
     */
    private void validateAntiReplay(TelegramRequestParam param) {
        JSONObject body = param.getBody();
        Long updateId = body.getLong("update_id");

        if (updateId == null) {
            logger.warn("TelegramBotCheck error. 缺少update_id字段");
            throw new TelegramWebhookException(TelegramWebhookException.UPDATE_ID_NOT_FOUND);
        }

        // todo 判断 update_id 在指定时间内是否重复
    }

    /**
     * 频率限制验证
     */
    private void validateRateLimit(TelegramRequestParam param) {
        if (StringUtils.isBlank(param.getClientIp())) {
            logger.warn("TelegramBotCheck error. 无法获取客户端IP");
            throw new TelegramWebhookException(TelegramWebhookException.RATE_LIMIT_VALIDATION_FAILED);
        }

        // todo 可以使用Redis进行频率限制检查
    }

    /**
     * 消息内容安全检查
     */
    private void validateMessageContent(TelegramRequestParam param) {
        JSONObject body = param.getBody();
        JSONObject message = body.getJSONObject("message");

        if (message == null) {
            return; // 非消息类型的update
        }

        // 检查消息长度
        String text = message.getString("text");
        if (text != null && text.length() > 4096) {
            logger.warn("TelegramBotCheck error. 消息长度超限: {}", text.length());
            throw new TelegramWebhookException(TelegramWebhookException.MESSAGE_LENGTH_EXCEEDED);
        }

        // 检查用户信息完整性
        JSONObject from = message.getJSONObject("from");
        if (from == null || from.getLong("id") == null) {
            logger.warn("TelegramBotCheck error. 用户信息不完整");
            throw new TelegramWebhookException(TelegramWebhookException.USER_INFO_INCOMPLETE);
        }

        // 检查是否是机器人发送的消息
        Boolean isBot = from.getBoolean("is_bot");
        if (Boolean.TRUE.equals(isBot)) {
            logger.warn("TelegramBotCheck error. 检测到机器人发送的消息");
            throw new TelegramWebhookException(TelegramWebhookException.BOT_MESSAGE_DETECTED);
        }
    }

    /**
     * 时间戳验证（消息不能太旧）
     */
    private void validateTimestamp(TelegramRequestParam param) {
        JSONObject body = param.getBody();
        JSONObject message = body.getJSONObject("message");

        if (message == null) {
            return; // 非消息类型的update
        }

        Long messageDate = message.getLong("date");
        if (messageDate == null) {
            return; // 没有时间戳的消息
        }

        // 消息时间戳（秒）转换为毫秒
        long messageTime = messageDate * 1000;
        long currentTime = System.currentTimeMillis();

        // 消息不能超过5分钟
        long maxAge = TimeUnit.MINUTES.toMillis(5);
        if (currentTime - messageTime > maxAge) {
            logger.warn("TelegramBotCheck error. 消息过期: messageTime={}, currentTime={}, age={}ms",
                    messageTime, currentTime, currentTime - messageTime);
            throw new TelegramWebhookException(TelegramWebhookException.TIMESTAMP_VALIDATION_FAILED);
        }

        // 消息不能来自未来（允许30秒的时间误差）
        if (messageTime > currentTime + 30000) {
            logger.warn("TelegramBotCheck error. 消息来自未来: messageTime={}, currentTime={}", messageTime, currentTime);
            throw new TelegramWebhookException(TelegramWebhookException.TIMESTAMP_VALIDATION_FAILED);
        }
    }
}
