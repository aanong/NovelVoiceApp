# NovelVoice App

本项目是一个集成了小说阅读、文本转语音 (TTS) 以及实时聊天功能的移动端应用程序。采用前后端分离架构，提供了流畅的阅读体验和即时的社交互动。

---

## 📋 目录

- [技术栈](#-技术栈)
- [核心功能](#-核心功能)
- [项目结构](#-项目结构)
- [环境要求](#-环境要求)
- [快速开始](#-快速开始)
- [📚 详细使用教程](#-详细使用教程)
- [配置说明](#-配置说明)
- [API 接口](#-api-接口)
- [数据库设计](#-数据库设计)
- [功能详解](#-功能详解)
- [开发指南](#-开发指南)
- [部署说明](#-部署说明)
- [常见问题](#-常见问题)

---

## 🚀 技术栈

### 后端 (Backend)
- **核心框架**: Spring Boot 2.7.18
- **持久层**: MyBatis 2.3.1
- **数据库**: MySQL 5.7+
- **实时通信**: Netty 4.1.x (基于 WebSocket)
- **序列化**: Google Protobuf 3.21.12
- **语言版本**: Java 8 (JDK 1.8)
- **工具库**: 
  - Lombok (代码简化)
  - EasyExcel 3.3.3 (Excel处理)
  - Spring Security Core 6.2.7 (安全)
  - MinIO 8.5.7 (对象存储)
  - 阿里云 OSS 3.17.4 (对象存储)
- **构建工具**: Maven 3.6+

### 前端 (Frontend)
- **框架**: React Native 0.72.6
- **语言**: TypeScript 4.8.4
- **导航**: React Navigation 6.x
- **网络请求**: Axios 1.6.x
- **本地存储**: AsyncStorage 1.21.0
- **语音功能**: React Native TTS 4.1.0
- **状态管理**: React Hooks
- **实时通信**: WebSocket + Protobufjs 8.0.0
- **文件处理**: 
  - react-native-image-picker 7.1.0
  - react-native-document-picker 9.1.1
  - react-native-fs 2.20.0

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

### 5. Excel 导入导出
- **动态配置驱动**: 通过 YAML 配置文件定义导入导出规则
- **数据验证**: 支持 SpEL 表达式验证，内置多种验证函数
- **批量处理**: 支持大批量数据导入，带批处理回调
- **模板下载**: 自动生成导入模板
- **错误报告**: 详细的导入错误信息反馈

### 6. 文件存储
- **多存储策略**: 支持本地存储、MinIO、阿里云 OSS
- **策略模式**: 可灵活切换存储方式
- **文件类型限制**: 支持配置允许的文件类型
- **文件大小限制**: 可配置最大文件大小

---

## 📁 项目结构

### 模块概览
- `backend`: 核心业务后端 (Spring Boot)
- `frontend`: 移动端 APP (React Native)
- `admin-web`: 管理后台前端 (React)
- `excel-spring-boot-starter`: Excel 导入导出组件 (Starter)
- `file-spring-boot-starter`: 文件存储组件 (Starter, 支持 Local/MinIO/OSS)

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

### 管理后台结构 (`/admin-web`) [新增]
```text
src/
├── layout/         # 布局组件（侧边栏、头部）
├── pages/          # 页面组件
│   ├── novel/      # 小说管理
│   ├── system/     # 系统管理（角色、菜单）
│   └── Login.tsx   # 登录页
├── services/       # API 请求
└── App.tsx         # 路由配置
```


---

## 🗄️ 数据库设计

### 表结构说明

#### 用户表 (`users`)
- `id`: 主键
- `username`: 用户名（唯一）
- `password`: 密码（加密存储）
- `nickname`: 昵称
- `avatar`: 头像 URL
- `token`: 登录 Token
- `last_login_time`: 最后登录时间

#### 小说表 (`novels`)
- `id`: 主键
- `title`: 小说标题
- `author`: 作者
- `description`: 简介
- `cover_url`: 封面图片 URL

#### 章节表 (`chapters`)
- `id`: 主键
- `novel_id`: 小说 ID（外键）
- `title`: 章节标题
- `content`: 章节内容
- `chapter_no`: 章节序号
- `word_count`: 字数统计

#### 阅读进度表 (`reading_progress`)
- `id`: 主键
- `user_id`: 用户 ID
- `novel_id`: 小说 ID
- `chapter_id`: 当前章节 ID
- `chapter_no`: 章节序号
- `scroll_position`: 滚动位置（像素）
- `tts_position`: TTS 朗读位置（字符索引）
- `last_read_time`: 最后阅读时间
- 唯一索引: `(user_id, novel_id)`

#### 会话表 (`conversations`)
- `id`: 主键
- `user1_id`: 用户1 ID
- `user2_id`: 用户2 ID
- `last_message_id`: 最后消息 ID
- `last_message_time`: 最后消息时间
- 唯一索引: `(user1_id, user2_id)`

#### 消息表 (`messages`)
- `id`: 主键
- `sender_id`: 发送者 ID
- `receiver_id`: 接收者 ID（NULL 表示群聊）
- `conversation_id`: 会话 ID（私聊时使用）
- `content`: 消息内容
- `type`: 消息类型（0-文本, 1-图片, 2-表情, 3-文件）
- `file_url`: 文件 URL
- `file_name`: 文件名
- `file_size`: 文件大小（字节）
- `is_read`: 是否已读

#### 在线用户表 (`online_users`)
- `id`: 主键
- `user_id`: 用户 ID（唯一）
- `channel_id`: WebSocket 通道 ID
- `last_active_time`: 最后活跃时间

#### 权限管理表 (RBAC) [新增]

**角色表 (`sys_role`)**:
- `id`: 角色 ID
- `role_name`: 角色名称
- `role_key`: 角色权限标识

**菜单表 (`sys_menu`)**:
- `id`: 菜单 ID
- `menu_name`: 菜单名称
- `perms`: 权限标识
- `component`: 组件路径

**关联表**:
- `sys_user_role`: 用户-角色关联
- `sys_role_menu`: 角色-菜单关联

### 数据库初始化

执行 SQL 脚本创建所有表:
```bash
mysql -u root -p < backend/src/main/resources/db/schema.sql
```

或使用 MySQL 客户端:
```sql
source backend/src/main/resources/db/schema.sql;
```

---

## 🔧 环境要求

### 后端环境
- **JDK**: 1.8 或更高版本
- **Maven**: 3.6 或更高版本
- **MySQL**: 5.7 或更高版本
- **IDE**: IntelliJ IDEA (推荐) 或 VS Code

### 前端环境
- **Node.js**: 16.x 或更高版本
- **npm**: 8.x 或更高版本
- **React Native CLI**: 已全局安装
- **Android Studio**: (Android 开发)
- **Xcode**: (iOS 开发，仅 macOS)

---

## 🚀 快速开始

### 第一步：克隆项目
```bash
git clone <repository-url>
cd NovelVoiceApp
```

### 第二步：数据库初始化

1. **创建数据库**:
   ```bash
   # 登录 MySQL
   mysql -u root -p
   
   # 执行 SQL 脚本
   source backend/src/main/resources/db/schema.sql
   ```

2. **验证数据库**:
   ```sql
   USE novel_voice_db;
   SHOW TABLES;
   ```

### 第三步：后端配置与启动

1. **配置数据库连接**:
   
   编辑 `backend/src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/novel_voice_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
       username: root
       password: ${DB_PASSWORD:your_password}
   ```

2. **配置环境变量** (可选):
   ```bash
   # Windows (PowerShell)
   $env:DB_PASSWORD="your_password"
   $env:GEMINI_API_KEY="your_api_key"
   
   # Linux/macOS
   export DB_PASSWORD="your_password"
   export GEMINI_API_KEY="your_api_key"
   ```

3. **编译并运行**:
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

4. **验证服务**:
   - Spring Boot HTTP: http://localhost:8080
   - Netty WebSocket: ws://localhost:8081/ws
   - 健康检查: http://localhost:8080/api/auth/user (需要登录)

### 第四步：前端配置与启动

1. **安装依赖**:
   ```bash
   cd frontend
   npm install
   ```

2. **iOS 额外步骤** (仅 macOS):
   ```bash
   cd ios
   pod install
   cd ..
   ```

3. **配置 API 地址**:
   
   创建或编辑 `frontend/.env` 文件:
   ```env
   API_BASE_URL=http://localhost:8080
   WS_URL=ws://localhost:8081
   ```

   **注意**: 
   - 真机调试时，将 `localhost` 替换为电脑的内网 IP 地址
   - Android 模拟器使用 `10.0.2.2` 访问宿主机

4. **启动 Metro 服务**:
   ```bash
   npm start
   ```

5. **运行应用**:
   ```bash
   # Android (需要 Android Studio 和模拟器/真机)
   npm run android
   
   # iOS (需要 Xcode，仅 macOS)
   npm run ios
   ```

### 第五步：管理后台启动

1. **进入目录**:
   ```bash
   cd admin-web
   ```

2. **安装依赖**:
   ```bash
   npm install
   ```

3. **启动服务**:
   ```bash
   npm run dev
   ```
   访问地址: http://localhost:3000

---

## 📚 详细使用教程

### 1. 小说内容管理指南
小说内容可以通过以下三种方式导入：

#### 方案 A：使用管理后台 (推荐)
1. 启动 `admin-web` 并使用管理员账号登录。
2. 进入“小说管理”模块。
3. 点击“新增小说”上传封面图和填写基本信息。
4. 在小说列表中点击“章节管理”，支持批量上传章节内容。

#### 方案 B：Excel 批量导入
1. 下载模板：访问 `http://localhost:8080/api/excel/template/novel_import` 下载 Excel 模板。
2. 填写数据：按照模板格式填写小说标题、作者、简介等。
3. 执行导入：使用 Postman 或 `curl` 发送 POST 请求到 `/api/excel/import`，参数 `taskType` 设为 `novel_import`。

---

### 2. 实时聊天系统使用教程

#### 开发测试：
1. 确保后端 8081 端口未被占用。
2. 如果修改了 `ChatMessage.proto`，请务必执行：
   ```bash
   # 后端
   mvn clean compile
   # 前端
   cd frontend && npx protobufjs-cli pbjs -t static-module -w es6 -o src/proto/ChatMessage.js ../backend/src/main/resources/proto/ChatMessage.proto
   ```
3. 前端启动后，点击“聊天”模块。系统会自动建立 WebSocket 连接。
4. **私聊流程**：在用户列表中点击任意在线用户，即可发起一对一加密（Protobuf 序列化）聊天。

---

### 3. TTS 朗读功能配置

#### 移动端配置：
1. 确保手机/模拟器已安装语音引擎（如 Google 文本转语音）。
2. 在 `NovelReaderScreen` 中点击底部的“朗读”按钮。
3. 系统会自动同步朗读进度到后端，即使更换设备也能从中断处继续。

---

### 4. 文件存储切换指南

如果您希望从本地存储切换到云存储（如阿里云 OSS）：
1. 在 `application.yml` 中修改 `storage.type: aliyun-oss`。
2. 填入您的 `access-key-id` 和 `bucket` 信息。
3. 重启后端服务。系统将自动调用 `FileStorageFactory` 切换存储引擎，无需修改任何业务代码。

---

## ⚙️ 配置说明

### 后端配置 (`application.yml`)

#### 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/novel_voice_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: ${DB_PASSWORD:default_password}
```

#### 文件上传配置
```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB      # 单个文件最大大小
      max-request-size: 20MB   # 请求最大大小
```

#### 文件存储配置

**本地存储** (默认):
```yaml
storage:
  type: local
  local:
    path: ./uploads           # 存储路径
    base-url: http://localhost:8080
```

**MinIO 对象存储**:
```yaml
storage:
  type: minio
  minio:
    endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
    access-key: ${MINIO_ACCESS_KEY:minioadmin}
    secret-key: ${MINIO_SECRET_KEY:minioadmin}
    bucket: ${MINIO_BUCKET:novelvoice}
    secure: false
```

**阿里云 OSS**:
```yaml
storage:
  type: aliyun-oss
  aliyun-oss:
    endpoint: ${ALIYUN_OSS_ENDPOINT:oss-cn-hangzhou.aliyuncs.com}
    access-key-id: ${ALIYUN_OSS_ACCESS_KEY_ID:your-key}
    access-key-secret: ${ALIYUN_OSS_ACCESS_KEY_SECRET:your-secret}
    bucket: ${ALIYUN_OSS_BUCKET:novelvoice}
```

#### Netty WebSocket 配置
```yaml
netty:
  websocket:
    port: 8081
    path: /ws
```

#### Gemini AI 配置 (可选)
```yaml
gemini:
  api:
    key: ${GEMINI_API_KEY:your_api_key}
    model: gemini-3-pro-preview
    base-url: https://generativelanguage.googleapis.com/v1beta
```

#### Excel 配置
Excel 导入导出配置在 `excel-config.yml` 中，支持动态配置任务类型、列映射、验证规则等。

### 前端配置

#### API 地址配置
在 `frontend/src/services/api.ts` 中配置:
```typescript
const API_BASE_URL = 'http://localhost:8080';
const WS_URL = 'ws://localhost:8081';
```

#### 环境变量配置 (推荐)
创建 `frontend/.env`:
```env
API_BASE_URL=http://192.168.1.100:8080
WS_URL=ws://192.168.1.100:8081
```

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
| POST | /api/files/upload/{subDir} | 上传文件到指定子目录 |

### Excel 接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/excel/import | 导入 Excel 文件 |
| POST | /api/excel/export | 导出 Excel 文件 |
| GET | /api/excel/template/{taskType} | 下载导入模板 |
| GET | /api/excel/task-types | 获取所有任务类型 |
| POST | /api/excel/import-with-callback | 导入并处理数据（带业务回调） |

**Excel 导入参数**:
- `file`: Excel 文件 (multipart/form-data)
- `taskType`: 任务类型 (如: `user_import`, `novel_import`, `chapter_import`)
- `sheetIndex`: Sheet 索引，默认 0

**Excel 导出请求体**:
```json
{
  "taskType": "user_import",
  "data": [...],
  "fileName": "用户数据导出"
}
```

---

## 📖 功能详解

### Excel 导入导出功能

#### 配置说明
Excel 功能通过 `excel-config.yml` 配置文件进行管理，支持：
- 动态任务类型定义
- 列映射配置
- 数据验证规则（SpEL 表达式）
- 导入/导出列控制

#### 使用示例

**1. 导入用户数据**:
```bash
curl -X POST http://localhost:8080/api/excel/import \
  -F "file=@users.xlsx" \
  -F "taskType=user_import" \
  -F "sheetIndex=0"
```

**2. 下载导入模板**:
```bash
curl -O http://localhost:8080/api/excel/template/user_import
```

**3. 导出数据**:
```bash
curl -X POST http://localhost:8080/api/excel/export \
  -H "Content-Type: application/json" \
  -d '{
    "taskType": "user_import",
    "data": [...],
    "fileName": "用户数据导出"
  }'
```

#### 验证表达式示例
- `#notBlank(#val)`: 非空验证
- `#lengthBetween(#val, 3, 50)`: 长度范围验证
- `#isEmail(#val)`: 邮箱格式验证
- `#isPhone(#val)`: 手机号格式验证
- `#options(#val, '选项1', '选项2')`: 选项验证
- `#longGreaterThan(#val, 0)`: 数值比较验证

### 文件存储功能

#### 存储策略切换
在 `application.yml` 中修改 `storage.type` 即可切换存储方式：
- `local`: 本地文件系统
- `minio`: MinIO 对象存储
- `aliyun-oss`: 阿里云 OSS

#### 使用示例
```java
// 上传图片
POST /api/files/upload/image
Content-Type: multipart/form-data
file: <image_file>

// 上传文件到指定目录
POST /api/files/upload/avatars
Content-Type: multipart/form-data
file: <file>
```

### Protobuf 通信

#### 消息格式
定义文件: `backend/src/main/resources/proto/ChatMessage.proto`

#### 前端使用
```typescript
import { ChatMessage } from './proto/ChatMessage';

// 创建消息
const message = ChatMessage.create({
  senderId: userId,
  receiverId: targetUserId,
  content: 'Hello',
  type: 0, // 0-文本, 1-图片, 2-表情, 3-文件
  timestamp: Date.now()
});

// 编码
const buffer = ChatMessage.encode(message).finish();
```

---

## 🛡️ 安全规范

### 敏感信息管理
- **数据库密码**: 通过环境变量 `DB_PASSWORD` 传入
- **API Key**: 通过环境变量传入（如 `GEMINI_API_KEY`, `MINIO_ACCESS_KEY`）
- **前端配置**: API 地址配置在 `.env` 文件中，不提交到代码仓库

### SQL 注入防护
- MyBatis 中必须使用 `#{}` 参数占位符
- 禁止使用 `${}` 进行字符串拼接
- 所有用户输入必须进行参数化查询

### 密码安全
- 用户密码使用 `PasswordUtil` 工具类加密存储
- 禁止明文传输和存储密码
- Token 认证机制保护 API 接口

### 文件上传安全
- 文件类型验证（通过配置限制）
- 文件大小限制（默认 10MB）
- 文件名安全处理（防止路径遍历）

---

## 💻 开发指南

### 后端开发

#### 代码结构规范
```
controller/  -> RESTful API 控制器，只负责请求转发和响应
service/     -> 业务逻辑层，接口 + 实现分离
mapper/      -> MyBatis Mapper 接口
entity/      -> 数据库实体类，对应表结构
dto/         -> 请求传输对象，用于接收前端参数
vo/          -> 视图对象，用于返回给前端的数据
common/      -> 公共工具类、统一返回对象、全局异常处理
netty/       -> Netty WebSocket 服务器相关代码
util/        -> 工具类
```

#### API 返回格式
所有 RESTful 接口必须返回统一的 `Result<T>` 对象：
```json
{
  "code": 200,
  "msg": "Success",
  "data": {}
}
```

#### 添加新的 Excel 任务类型
1. 在 `excel-config.yml` 中添加任务配置
2. 定义列映射和验证规则
3. 使用 `ExcelService` 进行导入导出

#### Protobuf 代码生成
修改 `.proto` 文件后，需要重新生成代码：
```bash
# 后端 (Maven 自动生成)
mvn clean compile

# 前端 (手动生成)
cd frontend
npx protobufjs-cli pbjs -t static-module -w es6 -o src/proto/ChatMessage.js backend/src/main/resources/proto/ChatMessage.proto
```

### 前端开发

#### 代码结构规范
```
src/
├── screens/        # 页面组件
├── services/       # API 请求和 WebSocket 服务
├── components/     # 可复用组件
├── types/          # TypeScript 类型定义
├── proto/          # Protobuf 定义和生成代码
└── navigation/     # 路由配置
```

#### 组件开发规范
- 使用函数组件 + Hooks
- 禁止使用 Class 组件
- 组件文件使用 PascalCase 命名
- 样式使用 `StyleSheet.create()` 创建

#### API 请求示例
```typescript
import api from './services/api';

// GET 请求
const novels = await api.get('/novels/list');

// POST 请求
const result = await api.post('/auth/login', {
  username: 'user',
  password: 'pass'
});
```

#### WebSocket 连接示例
```typescript
import { ChatMessage } from './proto/ChatMessage';

const ws = new WebSocket('ws://localhost:8081/ws');

ws.onmessage = (event) => {
  const message = ChatMessage.decode(new Uint8Array(event.data));
  console.log(message);
};
```

### 开发注意事项

1. **后端代码修改**: 修改后需重启 Spring Boot 服务
2. **Protobuf 修改**: 修改后需重新生成前后端代码
3. **真机调试**: 使用电脑内网 IP 地址，不使用 localhost
4. **Android 模拟器**: 使用 `10.0.2.2` 访问宿主机 localhost
5. **iOS 模拟器**: 可以直接使用 localhost
6. **数据库变更**: 修改表结构后需同步更新 Entity 和 Mapper

---

## 🚀 部署说明

### 后端部署

#### 1. 打包应用
```bash
cd backend
mvn clean package -DskipTests
```

#### 2. 运行 JAR 包
```bash
java -jar target/novel-voice-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --DB_PASSWORD=your_password
```

#### 3. 使用 Docker (可选)
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/novel-voice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 前端部署

#### Android 打包
```bash
cd frontend/android
./gradlew assembleRelease
```

#### iOS 打包
使用 Xcode 进行 Archive 和发布。

---

## ❓ 常见问题

### Q1: 后端启动失败，提示数据库连接错误
**A**: 检查 `application.yml` 中的数据库配置，确保：
- MySQL 服务已启动
- 数据库已创建
- 用户名和密码正确
- 网络连接正常

### Q2: 前端无法连接后端 API
**A**: 检查以下几点：
- API 地址配置是否正确
- 后端服务是否已启动
- 网络是否可达（真机需使用内网 IP）
- 防火墙是否阻止连接

### Q3: WebSocket 连接失败
**A**: 检查：
- Netty 服务是否已启动（端口 8081）
- WebSocket URL 是否正确
- 网络连接是否正常

### Q4: Excel 导入失败
**A**: 检查：
- 文件格式是否正确（.xlsx 或 .xls）
- 任务类型是否在配置文件中定义
- 数据是否符合验证规则
- 查看后端日志获取详细错误信息

### Q5: 文件上传失败
**A**: 检查：
- 文件大小是否超过限制（默认 10MB）
- 存储配置是否正确
- 存储路径是否有写权限（本地存储）
- 对象存储服务是否可访问（MinIO/OSS）

### Q6: Protobuf 消息解析错误
**A**: 确保：
- 前后端使用相同版本的 `.proto` 文件
- 已重新生成 Protobuf 代码
- 消息格式符合定义

### Q7: Android 模拟器无法访问 localhost
**A**: Android 模拟器使用 `10.0.2.2` 访问宿主机，将 API 地址中的 `localhost` 替换为 `10.0.2.2`。

---

## 📝 更新日志

### v2.0 (当前版本)
- ✅ 登录注册优化（表单验证、Token 认证、自动登录）
- ✅ 阅读器增强（章节切换、目录弹窗、TTS 进度跟踪）
- ✅ 私聊功能（用户列表、一对一私聊、在线状态）
- ✅ 聊天增强（表情、图片/文件发送、消息类型区分）
- ✅ Excel 导入导出功能（动态配置、数据验证）
- ✅ 多存储策略支持（本地、MinIO、阿里云 OSS）

### v1.0
- ✅ 基础用户认证
- ✅ 小说阅读功能
- ✅ 群聊功能
- ✅ TTS 语音朗读

---

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码提交规范
遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建/工具相关

---

## 📄 License

MIT License

---

## 📮 联系方式

如有问题或建议，请提交 Issue 或联系项目维护者。
