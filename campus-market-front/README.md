# 校园二手交易平台 — 运行指南

## 项目结构

```
campus-market/            # 后端 Spring Boot 3
├── pom.xml
├── sql/schema.sql        # 建表脚本
└── src/

campus-market-front/      # 前端 Vue 3 + Vite
├── package.json
├── vite.config.js
└── src/
    ├── api/              # 接口层
    ├── utils/            # 工具函数
    ├── stores/           # Pinia 状态
    ├── router/           # 路由
    ├── views/            # 页面
    └── components/       # 公共组件
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

## 第一步：初始化数据库

```bash
# Windows 用 MySQL 客户端执行
mysql -u root -p < C:\Users\Jason\Desktop\code\campus-market\sql\schema.sql
```

---



## 第二步：配置数据库连接

打开 `campus-market/src/main/resources/application.yml`，修改 MySQL 和 Redis 配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_market?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456          # ← 改为你的 MySQL 密码

  data:
    redis:
      host: localhost
      port: 6379
      password:               # ← 如果有 Redis 密码，填在这里
```

---

## 第三步：启动后端

```bash
cd C:\Users\Jason\Desktop\code\campus-market

# 方式 A：Maven 直接运行
mvn spring-boot:run

# 方式 B：先打包再运行
mvn clean package -DskipTests
java -jar target/campus-market-1.0.0.jar
```

启动成功的标志：

```
Started CampusMarketApplication in 4.0xx seconds
```

---

## 第四步：启动前端

新开一个终端：

```bash
cd C:\Users\Jason\Desktop\code\campus-market-front

# 安装依赖（首次运行需要）
npm install

# 启动开发服务器
npm run dev
```

> **PowerShell 执行策略报错**，使用 CMD（命令提示符）替代，或在 PowerShell 中执行：
> `powershell -ExecutionPolicy Bypass -Command "npm install"`

启动成功后浏览器访问 **http://localhost:3000**

---

## 第五步：验证功能

按顺序测试以下流程：

```bash
# 1. 注册
curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"test\",\"password\":\"123456\",\"nickname\":\"测试用户\"}"

# 2. 登录（获取 token）
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"test\",\"password\":\"123456\"}"

# 3. 商品列表
curl http://localhost:8080/api/products?page=1&size=10

# 4. AI 生成描述
curl -X POST http://localhost:8080/api/ai/generate-description ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"iPhone 13 校园自用\"}"
```

浏览器端操作：注册 → 登录 → 浏览商品 → 发布商品 → AI 生成描述

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

### Q：后端启动报端口被占用

改 `application.yml` 中的 `server.port` 为其他值（如 8081）。

### Q：前端启动报端口被占用

改 `vite.config.js` 中的 `server.port`。

### Q：前端请求后端 401

登录过期，重新登录获取新 token。

### Q：后端连不上 MySQL

检查 MySQL 服务是否运行，`application.yml` 中密码是否正确。

### Q：npm 安装依赖慢

```bash
npm config set registry https://registry.npmmirror.com
```

---

## 常用命令速查

```bash
# 后端重新编译
cd campus-market && mvn clean compile

# 后端只启动不编译
mvn spring-boot:run

# 前端安装新依赖后
cd campus-market-front && npm install

# 前端启动
npm run dev
```
