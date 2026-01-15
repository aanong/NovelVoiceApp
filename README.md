# NovelVoice App

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
- **本地存储**: AsyncStorage
- **语音功能**: React Native TTS
- **状态管理**: React Hooks
- **实时通信**: WebSocket + Protobufjs

---

## ✨ 核心功能

### 1. 用户认证
- 用户注册与登录（表单验证）
- Token 认证机制
- 自动登录（本地存储）
- 安全退出

### 2. 小说书架
- 小说列表展示
- 阅读历史记录
- 下拉刷新

### 3. 增强阅读器
- 文字阅读功能
- **上一章/下一章快捷切换**
- **章节目录弹窗选择**
- **TTS 语音朗读**
- **阅读进度保存与恢复**
- **TTS 朗读位置跟踪**
- 护眼阅读背景

### 4. 实时聊天室
- 高性能 Netty 框架
- Protobuf 二进制序列化
- **群聊功能**
- **私聊功能（一对一）**
- **表情发送**
- **图片/文件发送支持**
- 消息历史记录
- 用户在线状态

---

## 📁 项目结构

### 后端结构 (`/backend`)
```text
src/main/java/com/app/novelvoice
├── common/         # 公共工具类、统一返回对象、异常处理
├── config/         # 配置类（跨域、静态资源等）
├── controller/     # RESTful API 控制器
│   ├── AuthController.java        # 用户认证
│   ├── NovelController.java       # 小说相关
│   ├── ChatController.java        # 聊天相关
│   ├── ReadingProgressController.java  # 阅读进度
│   └── FileController.java        # 文件上传
├── dto/            # 请求传输对象
├── entity/         # 数据库映射实体类
├── mapper/         # MyBatis Mapper 接口
├── netty/          # Netty WebSocket 服务器实现
├── service/        # 业务逻辑层接口与实现
├── util/           # 工具类
└── vo/             # 视图返回对象
```

### 前端结构 (`/frontend`)
```text
src/
├── screens/        # 界面组件
│   ├── LoginScreen.tsx         # 登录页面
│   ├── RegisterScreen.tsx      # 注册页面
│   ├── NovelListScreen.tsx     # 小说列表
│   ├── NovelReaderScreen.tsx   # 阅读器
│   └── ChatScreen.tsx          # 聊天室
├── services/       # API 请求相关服务
│   └── api.ts      # Axios 配置和本地存储
├── types/          # TypeScript 类型定义
│   └── index.ts    # 类型和常量
└── App.tsx         # 主入口和路由配置
```

---

## 🗄️ 数据库表结构

- `users` - 用户表（含 Token 认证）
- `novels` - 小说表
- `chapters` - 章节表
- `reading_progress` - 阅读进度表（新增）
- `conversations` - 私聊会话表（新增）
- `messages` - 消息表（支持私聊和多类型消息）
- `online_users` - 在线用户表（新增）

---

## 🛠️ 项目启动方式

### 第一步：后端服务启动 (Java/Spring Boot)

1. **数据库初始化**:
   ```bash
   # 确保 MySQL 已运行
   mysql -u root -p
   source backend/src/main/resources/db/schema.sql
   ```

2. **修改配置**:
   - 编辑 `backend/src/main/resources/application.yml`
   - 设置正确的数据库 `url`, `username` 和 `password`

3. **编译并运行**:
   ```bash
   cd backend
   mvn clean spring-boot:run
   ```

4. **验证**:
   - Spring Boot: http://localhost:8080
   - Netty WebSocket: ws://localhost:8081/ws

### 第二步：前端移动端启动 (React Native)

1. **安装依赖**:
   ```bash
   cd frontend
   npm install
   # iOS 需要额外执行: cd ios && pod install && cd ..
   ```

2. **启动 Metro 服务**:
   ```bash
   npm start
   ```

3. **运行应用**:
   ```bash
   # Android
   npm run android
   
   # iOS
   npm run ios
   ```

**注意**: 真机或 Android 模拟器测试时，请使用电脑内网 IP 地址。

---

## 📡 API 接口

### 认证接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/register | 用户注册 |
| GET | /api/auth/user | 获取当前用户 |
| POST | /api/auth/logout/{userId} | 用户登出 |

### 小说接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/novels/list | 获取小说列表 |
| GET | /api/novels/{id} | 获取小说详情 |
| GET | /api/novels/{id}/chapters | 获取章节列表 |
| GET | /api/novels/chapters/{id} | 获取章节内容 |

### 阅读进度接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/reading-progress/save | 保存阅读进度 |
| GET | /api/reading-progress/{userId}/{novelId} | 获取阅读进度 |
| GET | /api/reading-progress/history/{userId} | 获取阅读历史 |

### 聊天接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/chat/history | 获取群聊历史 |
| POST | /api/chat/private/send | 发送私聊消息 |
| GET | /api/chat/private/{userId}/{targetUserId} | 获取私聊历史 |
| GET | /api/chat/conversations/{userId} | 获取会话列表 |
| GET | /api/chat/users/{excludeUserId} | 获取用户列表 |

### 文件接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/files/upload/image | 上传图片 |
| POST | /api/files/upload/file | 上传文件 |

---

## 🛡️ 安全规范

- **敏感信息**: 通过环境变量传入（`DB_PASSWORD`, `GEMINI_API_KEY`）
- **SQL 注入防护**: MyBatis 使用 `#{}` 占位符
- **密码安全**: 加密存储（`PasswordUtil`）
- **Token 认证**: 登录后自动生成和验证

---

## 📱 新功能特性

### v2.0 更新内容

1. **登录注册优化**
   - 添加表单验证
   - Token 认证机制
   - 自动登录功能

2. **阅读器增强**
   - 上一章/下一章按钮
   - 章节目录弹窗
   - TTS 进度跟踪
   - 阅读进度自动保存

3. **私聊功能**
   - 用户列表查看
   - 一对一私聊
   - 在线状态显示

4. **聊天增强**
   - 表情选择面板
   - 图片/文件发送支持
   - 消息类型区分显示

---

## 🔧 开发注意事项

1. 修改后端代码后需重启服务
2. 修改 Protobuf 定义后需重新生成代码
3. 真机调试使用电脑内网 IP
4. Android 模拟器使用 `10.0.2.2` 访问宿主机

---

## 📄 License

MIT License
