# 社交应用

本项目是一个集成了小说阅读、文本转语音 (TTS) 以及实时聊天功能的移动端应用程序。采用前后端分离架构，提供了流畅的阅读体验和即时的社交互动。

---

## 🚀 技术栈

### 后端 (Backend)
- **核心框架**: Spring Boot 2.7.18
- **持久层**: MyBatis
- **数据库**: MySQL 5.7+
- **实时通信**: Netty 4.1.x (基于 WebSocket)
- **序列化**: Google Protobuf 3.21.12
- **语言版本**: Java 8 (JDK 1.8)
- **工具**: Lombok, Maven

### 前端 (Frontend)
- **框架**: React Native 0.72.6
- **语言**: TypeScript
- **导航**: React Navigation 6.x
- **网络请求**: Axios
- **语音功能**: React Native TTS
- **状态管理**: 缓存与 React Hooks
- **实时通信**: WebSocket + Protobufjs

---

## ✨ 核心功能

1.  **用户认证**: 支持用户注册与登录。
2.  **小说书架**: 展示小说列表，支持点击阅读。
3.  **增强阅读器**:
    *   支持文字阅读功能并集成 TTS (Text-to-Speech)，实现自动朗读。
4.  **实时聊天室**:
    *   采用高性能 Netty 框架构建。
    *   使用 Protobuf 进行高效的消息二进制序列化。
    *   支持多用户在线即时通讯。

---

## 📁 项目结构

### 后端结构 (`/backend`)
```text
src/main/java/com/app/novelvoice
├── common/         # 公共工具类与统一返回对象 (Result)
├── controller/     # RESTful API 控制器 (Auth, Chat, Novel)
├── dto/            # 请求传输对象
├── entity/         # 数据库映射实体类
├── mapper/         # MyBatis Mapper 接口
├── netty/          # Netty WebSocket 服务器实现
├── service/        # 业务逻辑层接口与实现
└── vo/             # 视图返回对象
```

### 前端结构 (`/frontend`)
```text
src/
├── navigation/     # 路由配置 (Stack Navigator)
├── proto/          # Protobuf 定义及生成的 JS 代码
├── screens/        # 界面组件 (Login, List, Reader, Chat 等)
├── services/       # API 请求相关服务 (Axios, WebSocket)
└── types/          # TypeScript 类型定义
```

---

## 🛠️ 项目启动方式

### 第一步：后端服务启动 (Java/Spring Boot)

1.  **数据库初始化**:
    *   确保 MySQL 已运行，创建数据库 `novel_voice_db`。
    *   运行 `backend/src/main/resources/db/schema.sql` 脚本初始化表结构。
2.  **修改配置**:
    *   编辑 `backend/src/main/resources/application.yml`，设置正确的数据库 `url`, `username` 和 `password`。
3.  **编译并运行**:
    *   **IDE**: 直接运行 `NovelVoiceApplication.java`。
    *   **Maven 命令行**:
      ```bash
      cd backend
      mvn clean spring-boot:run
      ```
    *   **验证**: 观察控制台输出 `Netty WebSocket server started on port 8081` 和 Spring Boot 启动成功的日志 (默认端口 8080)。

### 第二步：前端移动端启动 (React Native)

1.  **安装依赖**:
    ```bash
    cd frontend
    npm install
    # 如果是 iOS 开发，还需执行: cd ios && pod install && cd ..
    ```
2.  **配置环境变量**:
    *   复制 `.env.example` 为 `.env`。
    *   设置 `API_URL` (后端 HTTP 地址) 和 `WS_URL` (Netty WebSocket 地址)。
    *   **注意**: 如果在真机或 Android 模拟器测试，请使用电脑的内网 IP 地址（例如 `http://192.168.x.x:8080`），不要使用 `localhost`。
3.  **启动 Metro 服务**:
    ```bash
    npm start
    ```
4.  **编译并运行**:
    *   **Android**: `npx react-native run-android`
    *   **iOS**: `npx react-native run-ios`

---

## 🛡️ 核心规范与安全
- **安全红线**: 严禁在代码中硬编码任何明文密码（如数据库密码、密钥等）。敏感信息必须通过配置文件或环境变量 `DB_PASSWORD` / `GEMINI_API_KEY` 传入。
- **持久层**: 必须使用 MyBatis `#{}` 占位符以防止 SQL 注入。
- **API 规范**: 所有接口必须返回统一的 `Result<T>` 对象。