package com.demo.telegram.bot.handler.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 余额查询命令处理器 - Demo版本
 */
@Component
public class BalanceCommandHandler extends AbstractTelegramCommandHandler {

    @Override
    public String handle(Long telegramUserId, String messageText, JSONObject webhook) {
        // Demo: 直接返回模拟数据，无需用户绑定验证

        // 生成模拟余额数据
        BigDecimal availableBalance = new BigDecimal(Math.random() * 100000).setScale(2, RoundingMode.HALF_UP);
        BigDecimal frozenBalance = new BigDecimal(Math.random() * 10000).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalBalance = availableBalance.add(frozenBalance);

        return String.format("""
                💰 <b>余额查询结果</b>

                🆔 用户ID: <code>%d</code>
                🏷️ 账户类型: <code>演示账户</code>

                💵 可用余额: <code>¥%,.2f</code>
                ❄️ 冻结余额: <code>¥%,.2f</code>
                💎 总余额: <code>¥%,.2f</code>

                ⏰ 查询时间: %s
                📊 这是模拟数据，仅用于演示

                💡 发送 /help 查看更多命令
                """,
                telegramUserId,
                availableBalance,
                frozenBalance,
                totalBalance,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}
