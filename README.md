[README.md](https://github.com/user-attachments/files/27312170/README.md)
# 校园二手交易平台

> 基于 Spring Boot 3 + Vue 3 的校园二手交易平台，集成 AI 智能助手 Agent 系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-4FC08D)](https://vuejs.org/)
[![JDK](https://img.shields.io/badge/JDK-17-orange)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

**GitHub：** https://github.com/JasonWang-dev1/campus-market-pro

---

## 项目结构

```
campus-market/                  # 后端 Spring Boot 3
├── pom.xml
├── sql/schema.sql              # 建表脚本
└── src/main/java/com/campusmarket/
    ├── CampusMarketApplication.java
    ├── controller/             # REST 控制器
    ├── service/                # 业务层（接口 + 实现）
    ├── mapper/                 # MyBatis Mapper
    ├── entity/                 # 数据实体
    ├── dto/                    # 请求体
    ├── vo/                     # 响应体
    ├── agent/                  # 🤖 AI 智能体系统（新增）
    │   ├── AgentService.java   #   工作流编排
    │   ├── Planner.java        #   任务规划（调用 DeepSeek）
    │   ├── Executor.java       #   任务执行
    │   ├── DeepSeekClient.java #   DeepSeek API 客户端
    │   ├── Tool.java           #   工具接口
    │   ├── ToolRegistry.java   #   工具注册中心
    │   ├── model/              #   数据模型
    │   └── tools/              #   内置工具
    │       ├── ProductSearchTool.java
    │       ├── ProductPublishTool.java
    │       └── AIDescriptionTool.java
    ├── config/                 # 配置类（Security/Redis/MyBatis）
    ├── security/               # JWT + Spring Security
    ├── utils/                  # 工具类
    └── common/                 # 公共类（Result/异常/枚举）

campus-market-front/            # 前端 Vue 3 + Vite
├── package.json
├── vite.config.js
└── src/
    ├── api/                    # 接口层
    │   ├── request.js          #   Axios 实例 + 拦截器
    │   ├── index.js            #   统一出口
    │   ├── auth.js             #   登录/注册
    │   ├── product.js          #   商品 CRUD
    │   ├── ai.js               #   AI 描述生成
    │   └── agent.js            #   Agent 智能助手
    ├── utils/                  # 工具函数
    │   ├── token.js            #   Token 管理
    │   └── index.js            #   通用函数
    ├── stores/user.js          # Pinia 用户状态
    ├── router/index.js         # 路由配置
    ├── components/Header.vue   # 公共导航栏
    ├── views/                  # 页面
    │   ├── Login.vue           #   登录/注册
    │   ├── Home.vue            #   首页
    │   ├── ProductList.vue     #   商品列表
    │   ├── ProductAdd.vue      #   发布商品
    │   ├── AIPage.vue          #   AI 生成描述
    │   └── AgentPage.vue       #   🤖 智能助手
    └── styles/global.css
```

---

## 环境要求

| 组件 | 版本 | 验证命令 |
|------|------|---------|
| JDK | ≥ 17 | `java -version` |
| Maven | ≥ 3.9 | `mvn -version` |
| Node.js | ≥ 18 | `node -v` |
| npm | ≥ 9 | `npm -v` |
| MySQL | ≥ 8.0 | `mysql -u root -p -e "SELECT 1"` |
| Redis | ≥ 6.x | `redis-cli ping` → PONG |

---

## 快速启动

### 1. 初始化数据库

```bash
mysql -u root -p < campus-market/sql/schema.sql
```

### 2. 配置连接

打开 `campus-market/src/main/resources/application.yml`，修改：

```yaml
spring:
  datasource:
    username: root          # MySQL 用户名
    password: 123456        # MySQL 密码

  data:
    redis:
      password:             # Redis 密码（如有）

deepseek:
  api-key: sk-your-key     # DeepSeek API Key（Agent 系统需要）
```

### 3. 启动后端

```bash
cd campus-market
mvn spring-boot:run
```

启动标志：`Started CampusMarketApplication in 4.0xx seconds`

### 4. 启动前端

```bash
cd campus-market-front
npm install
npm run dev
```

访问 **http://localhost:3000**

---

## 功能总览

### 📱 用户端

| 页面 | 路径 | 说明 |
|------|------|------|
| 登录/注册 | `/login` | JWT 登录 + 账号注册 |
| 首页 | `/home` | Banner + 快捷入口 + 最新商品 |
| 商品列表 | `/products` | 分类筛选 + 搜索 + 分页 |
| 发布商品 | `/products/add` | 表单校验 + AI 快捷入口 |

### 🤖 AI 能力

| 页面 | 路径 | 说明 |
|------|------|------|
| AI 生成描述 | `/ai` | 输入标题 → AI 生成商品描述 |
| AI 智能助手 | `/agent` | 自然语言指令 → 自动规划并执行 |

### 🧠 Agent 智能体系统

Agent 系统是平台的 AI 大脑，支持用自然语言指挥平台自动完成任务。

**支持的指令示例：**

| 指令 | Agent 执行流程 |
|------|---------------|
| "帮我搜一下iPhone" | → 规划 → search_products(keyword=iPhone) |
| "我想卖一个iPad，价格3000" | → 规划 → publish_product(title=ipad, price=3000) |
| "搜索耳机并生成描述" | → 规划 → search_products → generate_description（链式执行） |
| "有哪些数码商品" | → 规划 → search_products(category=数码) |

**架构流程：**

```
用户输入 → Planner(DeepSeek拆解任务) → Executor(按步骤执行) → ToolRegistry(调用具体工具) → 汇总结果
```

---

## API 接口

### 基础接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/login` | 登录 | × |
| POST | `/api/auth/register` | 注册 | × |
| GET | `/api/products` | 商品列表 | × |
| GET | `/api/products/{id}` | 商品详情 | × |
| POST | `/api/products` | 发布商品 | √ |
| PUT | `/api/products/{id}` | 更新商品 | √ |
| DELETE | `/api/products/{id}` | 删除商品 | √ |

### AI 接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/ai/generate-description` | AI 生成商品描述 | × |
| POST | `/api/agent/execute` | Agent 执行指令 | √ |

---

## 端口说明

| 服务 | 端口 |
|------|------|
| 后端 API | 8080 |
| 前端页面 | 3000 |
| MySQL | 3306 |
| Redis | 6379 |

---

## 常见问题

### 后端启动报端口被占用

改 `application.yml` 中的 `server.port`。

### 前端启动报端口被占用

改 `vite.config.js` 中的 `server.port`。

### 前端请求后端 401

登录过期，重新登录获取新 token。

### 后端连不上 MySQL

检查 MySQL 服务是否运行，`application.yml` 中密码是否正确。

### npm 安装依赖慢

```bash
npm config set registry https://registry.npmmirror.com
```

### PowerShell 执行策略报错

使用 CMD（命令提示符）替代，或在 PowerShell 中执行：
```
powershell -ExecutionPolicy Bypass -Command "npm install"
```

---

## 常用命令

```bash
# 后端重新编译
cd campus-market && mvn clean compile

# 后端启动
mvn spring-boot:run

# 前端安装依赖
cd campus-market-front && npm install

# 前端启动
npm run dev

# Git 提交推送
git add .
git commit -m "描述"
git push
```
