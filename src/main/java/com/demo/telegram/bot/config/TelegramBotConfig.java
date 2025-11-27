package com.demo.telegram.bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Telegram Bot配置类
 */
@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotConfig {

    /**
     * Bot Token
     */
    private String botToken;

    /**
     * Webhook安全密钥
     */
    private String secretToken;

    /**
     * 允许的IP列表
     */
    private List<String> allowedIps;

    /**
     * 频率限制配置
     */
    private RateLimitConfig rateLimit = new RateLimitConfig();

    /**
     * 绑定配置
     */
    private BindingConfig binding = new BindingConfig();

    // Getters and Setters
    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public List<String> getAllowedIps() {
        return allowedIps;
    }

    public void setAllowedIps(List<String> allowedIps) {
        this.allowedIps = allowedIps;
    }

    public RateLimitConfig getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(RateLimitConfig rateLimit) {
        this.rateLimit = rateLimit;
    }

    public BindingConfig getBinding() {
        return binding;
    }

    public void setBinding(BindingConfig binding) {
        this.binding = binding;
    }

    /**
     * 频率限制配置
     */
    public static class RateLimitConfig {
        /**
         * IP每分钟最大请求数
         */
        private int ipRequestsPerMinute = 100;

        /**
         * 用户每分钟最大请求数
         */
        private int userRequestsPerMinute = 20;

        // Getters and Setters
        public int getIpRequestsPerMinute() {
            return ipRequestsPerMinute;
        }

        public void setIpRequestsPerMinute(int ipRequestsPerMinute) {
            this.ipRequestsPerMinute = ipRequestsPerMinute;
        }

        public int getUserRequestsPerMinute() {
            return userRequestsPerMinute;
        }

        public void setUserRequestsPerMinute(int userRequestsPerMinute) {
            this.userRequestsPerMinute = userRequestsPerMinute;
        }
    }

    /**
     * 绑定配置
     */
    public static class BindingConfig {
        /**
         * 绑定码过期时间（分钟）
         */
        private int codeExpiryMinutes = 10;

        /**
         * 绑定码最小长度
         */
        private int codeLengthMin = 24;

        /**
         * 绑定码最大长度
         */
        private int codeLengthMax = 30;

        // Getters and Setters
        public int getCodeExpiryMinutes() {
            return codeExpiryMinutes;
        }

        public void setCodeExpiryMinutes(int codeExpiryMinutes) {
            this.codeExpiryMinutes = codeExpiryMinutes;
        }

        public int getCodeLengthMin() {
            return codeLengthMin;
        }

        public void setCodeLengthMin(int codeLengthMin) {
            this.codeLengthMin = codeLengthMin;
        }

        public int getCodeLengthMax() {
            return codeLengthMax;
        }

        public void setCodeLengthMax(int codeLengthMax) {
            this.codeLengthMax = codeLengthMax;
        }
    }
}
