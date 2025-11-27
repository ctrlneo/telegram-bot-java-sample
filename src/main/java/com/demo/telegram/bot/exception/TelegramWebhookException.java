package com.demo.telegram.bot.exception;

import java.text.MessageFormat;

public class TelegramWebhookException extends RuntimeException {

    /**
     * 异常默认错误码
     * 拖底用的，日常编码抛出出确切的错误码
     */
    public static final String ERROR_CODE = "P9999";

    /**
     * Secret Token验证失败
     */
    public static final String SECRET_TOKEN_VALIDATION_FAILED = "TW0001";

    /**
     * IP白名单验证失败
     */
    public static final String IP_WHITELIST_VALIDATION_FAILED = "TW0002";

    /**
     * 请求格式验证失败
     */
    public static final String REQUEST_FORMAT_VALIDATION_FAILED = "TW0003";

    /**
     * 防重放攻击验证失败
     */
    public static final String ANTI_REPLAY_VALIDATION_FAILED = "TW0004";

    /**
     * 频率限制验证失败
     */
    public static final String RATE_LIMIT_VALIDATION_FAILED = "TW0005";

    /**
     * 消息内容安全检查失败
     */
    public static final String MESSAGE_CONTENT_VALIDATION_FAILED = "TW0006";

    /**
     * 时间戳验证失败
     */
    public static final String TIMESTAMP_VALIDATION_FAILED = "TW0007";

    /**
     * 缺少update_id字段
     */
    public static final String UPDATE_ID_NOT_FOUND = "TW0008";

    /**
     * 缺少有效的消息类型字段
     */
    public static final String MESSAGE_TYPE_NOT_FOUND = "TW0009";

    /**
     * 消息长度超限
     */
    public static final String MESSAGE_LENGTH_EXCEEDED = "TW0010";

    /**
     * 用户信息不完整
     */
    public static final String USER_INFO_INCOMPLETE = "TW0011";

    /**
     * 检测到机器人发送的消息
     */
    public static final String BOT_MESSAGE_DETECTED = "TW0012";

    /**
     * 无法提取Telegram用户ID
     */
    public static final String TELEGRAM_USER_ID_INVALID = "TW0013";

    /**
     * Bot路由验证异常
     */
    public static final String BOT_ROUTE_ERROR = "TW0014";
    /**
     * 生成绑定码过于频繁，请1小时后重试
     */
    public static final String BIND_CODE_RATE_LIMIT = "TW0015";

    public static final String ERROR_CODE_MSG = "System Error";

    /**
     * 序列化
     */
    private static final long serialVersionUID = 4916901757376942836L;
    /**
     * 异常的业务标识码
     */
    protected String code;
    /**
     * 异常的业务具体信息值
     */
    protected String msg;

    protected TelegramWebhookException() {
        super("errorCode:" + ERROR_CODE);
        this.code = ERROR_CODE;
        this.msg = ERROR_CODE_MSG;
    }

    public TelegramWebhookException(String _code) {
        super("errorCode:" + _code);
        this.code = _code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String message, Object... args) {
        this.msg = MessageFormat.format(message, args);
    }
}
