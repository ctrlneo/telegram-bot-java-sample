# Telegram Bot Java 使用指南

本指南将详细介绍如何从零开始使用这个Telegram Bot项目，包括创建Bot、配置项目到最终运行的完整流程。

## 📋 目录

1. [前置条件](#1-前置条件)
2. [创建Bot并获取Token](#2-创建bot并获取token)
3. [配置项目](#3-配置项目)
4. [设置Webhook（必须步骤）](#4-设置webhook必须步骤)
5. [设置Bot命令菜单（可选步骤）](#5-设置bot命令菜单可选步骤)
6. [测试和使用](#6-测试和使用)
7. [常见问题](#7-常见问题)

## 1. 前置条件

### 环境要求
- **JDK 21+** - 项目使用了JDK 21的文本块等新特性
- **Maven 3.6+** - 用于构建和运行项目
- **暴露公网服务** - 用于接收Telegram的Webhook请求（公网域名、ngrok、frp等方式实现）

## 2. 创建Bot并获取Token

### 步骤1：找到BotFather
1. 打开Telegram应用
2. 在搜索框中输入 `@BotFather`
3. 点击官方账号（带有蓝色认证标识）
4. 点击"开始"按钮

### 步骤2：创建新Bot
向BotFather发送以下命令：
```
/newbot
```

BotFather会要求你提供：
- **Bot名称**：例如 `My Demo Bot`
- **Bot用户名**：必须唯一且以`bot`结尾，例如 `MyDemoBot_2024_bot`

### 步骤3：获取Bot Token
创建成功后，BotFather会返回如下格式的消息：

```
Done! Congratulations on your new bot.
You can find it at t.me/MyDemoBot_2024_bot.
You can now add a description, about section and profile picture for your bot, see /help for a list of commands.

Use this token to access the HTTP API:
1234567890:ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890

Keep your token secure and store it safely, it can be used by anyone to control your bot.
```

**重要提示：**
- Token格式为：`数字:字母数字混合字符串`
- 务必保管好Token，不要泄露给他人
- 如果Token泄露，可以使用 `/revoke` 命令重新生成

## 3. 配置项目

### 步骤1：配置Bot Token

编辑 `src/main/resources/application.yml`，将Bot Token替换为从BotFather获取的token：

```yaml
telegram:
  bot:
    bot-token: 1234567890:ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890  # 替换为你的Bot Token
```

### 步骤2：配置Secret Token

在同一个配置文件中，添加Secret Token：

```yaml
telegram:
  bot:
    bot-token: 1234567890:ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890  # 你的Bot Token
    secret-token: your-secret-token-32-characters-minimum     # 替换为你的Secret Token
```

**什么是Secret Token？**
Secret Token是一个自定义的安全密钥，用于验证Webhook请求的真实性：

🔐 **安全机制：**
- Telegram会在每个Webhook请求的HTTP头 `X-Telegram-Bot-Api-Secret-Token` 中包含这个token
- 项目通过验证请求头中的token是否与配置的匹配来确认请求确实来自Telegram
- 防止恶意第三方伪造Webhook请求

📋 **Token要求：**
- **长度**：至少32个字符
- **内容**：建议使用随机生成的字符串，包含大小写字母、数字和特殊字符
- **唯一性**：每个Bot应使用不同的Secret Token

**如何生成Secret Token？**

方法1：使用OpenSSL（推荐）
```bash
# 生成64位随机十六进制字符串（32字节）
openssl rand -hex 32

# 生成Base64编码的随机字符串
openssl rand -base64 32
```

方法2：使用Java代码
```java
import java.security.SecureRandom;
import java.util.Base64;

// 生成32字节的随机数据
byte[] randomBytes = new byte[32];
new SecureRandom().nextBytes(randomBytes);
String secretToken = Base64.getEncoder().encodeToString(randomBytes);
System.out.println("Generated Secret Token: " + secretToken);
```

方法3：在线工具
- 可以使用在线随机字符串生成器
- 确保长度至少32个字符
- 选择包含大小写字母、数字和特殊字符

⚠️ **安全提醒：**
- Secret Token和Bot Token一样重要，都需要妥善保管
- 不要将Secret Token提交到代码仓库
- 生产环境和开发环境应使用不同的Secret Token
- 如果Secret Token泄露，需要立即重新生成并更新Webhook配置

### 步骤3：运行项目

```bash
cd telegram-bot-java-sample
mvn spring-boot:run
```

项目将在 `http://localhost:8080` 启动。

## 4. 设置Webhook（必须步骤）

⚠️ **重要：** Webhook是Bot正常工作的**必须步骤**，如果不设置Webhook，Bot将无法接收和处理用户消息。

### 步骤1：设置Webhook

**Webhook URL要求：**
- **必须使用HTTPS**：Telegram只接受HTTPS协议，不支持HTTP
- **有效SSL证书**：需要配置有效的SSL证书
- **端口限制**：仅支持标准端口443、80、88、8443
- **公网访问**：URL必须能够被Telegram服务器访问

**本地开发环境（使用ngrok）：**
如果需要在本地测试，可以使用ngrok暴露本地端口：

**ngrok是什么？**
ngrok是一个内网穿透工具，可以将本地端口映射到公网，生成临时的HTTPS URL，非常适合本地开发和测试。

```bash
# 安装ngrok
brew install ngrok

# 暴露本地8080端口
ngrok http 8080
# 会得到类似 https://abc123.ngrok.io 的HTTPS地址
```

**准备好域名后，通过下面的方法设置对应的webhook（请替换 secret_token、域名、secret_token）：**
```bash
curl -X POST "https://api.telegram.org/bot{YOUR_BOT_TOKEN}/setWebhook" \
     -H "Content-Type: application/json" \
     -d '{
       "url": "https://your-domain.com/bot/rest/webhook",
       "secret_token": "your-secret-token-32-characters-minimum",
       "max_connections": 40,
       "allowed_updates": ["message", "callback_query"],
       "drop_pending_updates": true
     }'
```

**Webhook参数说明：**
- `url`: 接收消息的服务器地址（必须是公网可访问的HTTPS地址），内如：https://你的域名/bot/rest/webhook；请注意后面的 `/bot/rest/webhook` 为项目中的 webhook 的地址。 
- `secret_token`: 与application.yml中配置的相同（至少32字符）
- `max_connections`: 最大连接数（1-100）
- `allowed_updates`: 允许接收的更新类型
- `drop_pending_updates`: 是否丢弃待处理的消息

### 步骤2：验证Webhook设置

```bash
# 查看Webhook状态
curl "https://api.telegram.org/bot{YOUR_BOT_TOKEN}/getWebhookInfo"

# 获取Bot基本信息
curl "https://api.telegram.org/bot{YOUR_BOT_TOKEN}/getMe"
```

**成功设置的标志：**
- `getWebhookInfo` 返回的 `url` 字段显示你设置的地址
- `last_error_message` 为空或无错误信息
- `pending_update_count` 为较小值（表示没有积压的消息）

## 5. 设置Bot命令菜单（可选步骤）

💡 **提示：** 此步骤为**可选**，设置后可以让用户在输入 `/` 时看到可用命令列表，提升用户体验。不设置也不会影响Bot的基本功能。

### 设置命令菜单

```bash
curl -X POST "https://api.telegram.org/bot{YOUR_BOT_TOKEN}/setMyCommands" \
     -H "Content-Type: application/json" \
     -d '{
       "commands": [
         {
           "command": "start",
           "description": "开始使用机器人"
         },
         {
           "command": "help",
           "description": "显示帮助信息"
         },
         {
           "command": "balance",
           "description": "查询余额"
         }
       ]
     }'
```

**菜单效果：**
- 用户在聊天中输入 `/` 时会显示可用命令列表
- 新用户更容易发现和使用Bot功能
- 提升整体用户体验

**如果不设置：**
- Bot仍然可以正常响应所有命令
- 用户需要手动输入完整命令
- 不会影响Bot的核心功能

## 6. 测试和使用

### 步骤1：在Telegram中测试

在Telegram中搜索你的Bot用户名，然后发送以下命令进行测试：

```
/start      # 查看欢迎消息
/help       # 查看帮助信息
/balance    # 查询余额（返回模拟数据）
```

### 步骤2：查看运行状态

```bash
# 健康检查
curl http://localhost:8080/bot/rest/tg/health

# 状态信息
curl http://localhost:8080/bot/rest/tg/status
```

### 步骤3：查看日志

项目日志位于 `logs/telegram-bot.log`：

```bash
# 查看最新日志
tail -f logs/telegram-bot.log

# 搜索错误
grep "ERROR" logs/telegram-bot.log
```

## 7. 常见问题

### Q1: 项目启动失败？
**A:** 检查以下几点：
- 确保使用JDK 21+
- 执行 `mvn clean install` 重新构建
- 检查application.yml配置是否正确

### Q2: Bot无响应？
**A:** 按以下步骤排查：
1. 检查Bot Token是否正确
2. 检查Webhook是否设置成功
3. 查看服务器日志是否有错误
4. 确认防火墙允许8080端口
5. 验证Secret Token是否匹配

### Q3: Webhook设置失败？
**A:** 检查以下几点：
- URL是否可以公网访问
- SSL证书是否有效（生产环境必须HTTPS）
- 服务器是否返回200状态码
- Secret Token长度是否至少32个字符

### Q4: 命令菜单不显示？
**A:** 尝试以下解决方案：
1. 重新发送setMyCommands请求
2. 检查命令格式是否正确
3. 重启Telegram应用
4. 确认命令拼写无误

### Q5: 如何获取服务器公网IP？
```bash
curl ifconfig.me
```

### Q6: 如何重置Bot Token？
向BotFather发送 `/revoke` 命令，然后重新生成Token。

## 📚 项目架构

本项目采用**分层架构设计**，结合**工厂模式**和**策略模式**，实现了高内聚、低耦合的Telegram Bot系统。

### 🏗️ 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    Telegram Bot Platform                     │
└─────────────────────┬───────────────────────────────────────┘
                      │ Webhook Request
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                Controller Layer (控制器层)                    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │        TelegramBotController                         │    │
│  │  • 接收Webhook请求 (/bot/rest/webhook)              │    │
│  │  • IP白名单验证                                      │    │
│  │  • 数据格式验证                                      │    │
│  │  • 调用Service层处理                                 │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────┬───────────────────────────────────────┘
                      │ 验证后的请求数据
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                 Service Layer (服务层)                       │
│  ┌─────────────────────────────────────────────────────┐    │
│  │      TelegramBotWebhookService                      │    │
│  │  • 解析Telegram Webhook数据                         │    │
│  │  • 提取用户ID、聊天ID、消息文本                     │    │
│  │  • 匹配命令类型 (START/HELP/BALANCE/INVALID)       │    │
│  │  • 调用对应的命令处理器                             │    │
│  │  • 构造响应返回                                     │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────┬───────────────────────────────────────┘
                      │ 命令处理请求
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                Handler Layer (处理器层)                       │
│  ┌─────────────────────┬─────────────────────────────────┐   │
│  │  CommandHandler     │  CommandHandlerFactory         │   │
│  │  ┌─────────────┐    │  • 工厂模式管理处理器           │   │
│  │  │ Start       │    │  • 根据操作类型获取处理器       │   │
│  │  │ Help        │    │  • 支持处理器注册               │   │
│  │  │ Balance     │    └─────────────────────────────────┘   │
│  │  │ Invalid     │                                        │
│  │  └─────────────┘                                        │
│  └─────────────────────┬─────────────────────────────────┘   │
│                      │ 统一处理逻辑                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │    AbstractTelegramCommandHandler                    │    │
│  │  • 模板方法模式                                      │    │
│  │  • 统一响应格式化                                    │    │
│  │  • 错误/成功/信息消息构造                            │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────┬───────────────────────────────────────┘
                      │ 处理结果
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                Response Layer (响应层)                       │
│  ┌─────────────────────────────────────────────────────┐    │
│  │       TelegramBotResponse                           │    │
│  │  • 支持多种响应方法 (sendMessage/editMessage)      │    │
│  │  • HTML格式消息支持                                  │    │
│  │  • 静态工厂方法创建                                  │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP 200 Response
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                   Telegram Platform                         │
└─────────────────────────────────────────────────────────────┘
```

### 📦 核心包结构

```
com.demo.telegram.bot/
├── 📁 config/                    # 配置层
│   └── TelegramBotConfig         # Bot配置管理
├── 📁 controller/               # 控制器层
│   └── TelegramBotController     # Webhook请求处理
├── 📁 service/                  # 服务层
│   └── TelegramBotWebhookService # 核心业务逻辑
├── 📁 handler/                  # 处理器层
│   ├── TelegramCommandHandler     # 命令处理接口
│   ├── TelegramCommandHandlerFactory  # 处理器工厂
│   ├── AbstractTelegramCommandHandler  # 抽象基类
│   └── impl/                     # 具体实现
│       ├── StartCommandHandler     # /start命令处理
│       ├── HelpCommandHandler      # /help命令处理
│       ├── BalanceCommandHandler   # /balance命令处理
│       └── InvalidCommandHandler   # 无效命令处理
├── 📁 model/                    # 模型层
│   ├── TelegramBotResponse       # 响应数据模型
│   ├── TelegramOperationType     # 操作类型枚举
│   └── TelegramRequestParam      # 请求参数模型
├── 📁 util/                     # 工具层
│   ├── TelegramWebhookValidator  # Webhook验证工具
│   └── IpUtil                    # IP处理工具
└── 📁 exception/                # 异常层
    └── TelegramWebhookException  # 自定义异常
```

### 🔧 设计模式应用

1. **工厂模式 (Factory Pattern)**
   - `TelegramCommandHandlerFactory` 根据操作类型创建对应处理器
   - 解耦命令处理逻辑，易于扩展新命令

2. **策略模式 (Strategy Pattern)**
   - `TelegramCommandHandler` 不同命令采用不同处理策略
   - 每个命令独立处理，互不影响

3. **模板方法模式 (Template Method Pattern)**
   - `AbstractTelegramCommandHandler` 定义通用消息格式化方法
   - 统一响应格式，减少重复代码

4. **枚举模式 (Enum Pattern)**
   - `TelegramOperationType` 集中管理命令类型和匹配逻辑

### 🛡️ 安全架构

```
请求 → IP白名单验证 → 格式验证 → Secret Token验证 → 频率限制 → 业务处理
   │              │           │              │             │
   ▼              ▼           ▼              ▼             ▼
 拒绝访问       400错误     401错误        429错误       正常处理
```

### 📊 数据流程

1. **Webhook接收**: Telegram → `TelegramBotController`
2. **安全验证**: IP白名单 + Secret Token + 格式检查
3. **数据解析**: 提取用户ID、聊天ID、消息文本
4. **命令匹配**: `TelegramOperationType.matchCommand()`
5. **处理器选择**: `TelegramCommandHandlerFactory.getHandler()`
6. **命令处理**: 具体的CommandHandler实现
7. **响应构造**: `TelegramBotResponse`创建响应
8. **结果返回**: HTTP 200响应给Telegram

### 🎯 架构优势

- **🔒 高安全性**: 多层验证机制，防重放、防攻击
- **🔧 易扩展**: 工厂模式支持快速添加新命令
- **📱 可维护**: 分层清晰，职责明确
- **⚡ 高性能**: 统一验证，避免重复处理
- **🌐 可配置**: 支持多环境配置部署

## 🔧 技术栈

- **框架**: Spring Boot 3.2.0
- **Java版本**: JDK 21+ (使用文本块特性)
- **JSON处理**: FastJSON
- **构建工具**: Maven
- **日志框架**: SLF4J + Logback

---

🎉 **恭喜！** 您已经成功配置并运行了Telegram Bot项目！

如果遇到问题，请参考上述常见问题部分或查看项目日志进行排查。
