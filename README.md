# CopyElements - Burp Suite Extension

[![Java Version](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Burp Suite](https://img.shields.io/badge/Burp%20Suite-Montoya%20API-red.svg)](https://portswigger.net/)

一个功能强大的 Burp Suite 扩展，用于快速复制 HTTP 请求/响应中的各种元素到剪贴板。支持提取主机、请求头、路径、参数值等信息，并提供格式化的请求/响应报文复制功能。

## ✨ 功能特性

### 🎯 核心功能
- **复制唯一主机列表** - 从选中的请求中提取所有唯一的主机名
- **复制请求头键名** - 提取所有唯一的HTTP请求头名称
- **复制路径（不含查询参数）** - 获取干净的URL路径列表
- **复制请求参数值** - 通过参数名搜索并复制所有匹配的请求参数值
- **复制响应参数值** - 从JSON响应体中搜索并复制指定键的所有值

### 🚀 新增功能
- **复制格式化请求报文** - 复制完整的HTTP请求消息，支持JSON/XML自动格式化
- **复制格式化响应报文** - 复制完整的HTTP响应消息，支持JSON/XML自动格式化
- **智能二进制检测** - 自动识别并跳过二进制内容，避免复制无效数据
- **UTF-8编码支持** - 完全支持中文等Unicode字符的正确显示
- **多工具支持** - 在Proxy、Logger、Repeater、Intruder等多个Burp工具中可用

### 🛠️ 技术特点
- **基于Montoya API** - 使用最新的Burp Suite扩展API
- **智能内容检测** - 优先检查Content-Type头，然后进行二进制内容检测
- **多JSON解析器支持** - 集成Jackson和Fastjson2双重解析引擎
- **自定义剪贴板处理** - 确保UTF-8字符的正确复制
- **用户友好界面** - 提供中文用户界面和详细操作反馈

## 🔧 安装使用

### 系统要求
- Burp Suite Professional/Community Edition
- Java 17 或更高版本
- Maven 3.6+ (用于编译)

### 安装步骤

1. **下载扩展**
   - 从 [Releases](https://github.com/GitHubNull/copyElements/releases) 页面下载最新的 `copyElements-x.x.jar` 文件

2. **加载扩展**
   - 打开 Burp Suite
   - 进入 `Extensions` → `Installed`
   - 点击 `Add` 按钮
   - 选择 `Java` 类型
   - 选择下载的 JAR 文件
   - 点击 `Next` 完成安装

### 使用方法

1. **在HTTP历史记录中使用**
   - 在 `Proxy` → `HTTP history` 中选择一个或多个请求
   - 右键点击选中的请求
   - 选择相应的复制功能菜单项

2. **在Logger中使用**
   - 在 `Logger` 工具中选择请求记录
   - 右键菜单选择复制功能

3. **在Repeater/Intruder中使用**（新功能）
   - 支持复制格式化的请求/响应报文
   - 右键菜单选择 `copyRequest` 或 `copyResponse`

4. **参数值提取**
   - 选择 `copyAllRequestParamValuesSet` 或 `copyAllResponseParamValuesSet`
   - 在弹出对话框中输入参数名
   - 系统将搜索并复制所有匹配的值

## 🏗️ 编译构建

### 从源码编译

```bash
# 克隆项目
git clone https://github.com/GitHubNull/copyElements.git
cd copyElements

# 编译项目
mvn clean package

# 生成的JAR文件位于 target/copyElements-x.x.jar
```

### 开发命令

```bash
# 仅编译（不打包）
mvn compile

# 清理构建产物
mvn clean

# 运行测试
mvn test
```

## 📦 项目结构

```
copyElements/
├── src/main/java/top/oxff/
│   ├── CopyElements.java                    # 主扩展类
│   ├── model/
│   │   └── ValuesType.java                  # 参数类型枚举
│   ├── ui/
│   │   ├── CopyElementsContextMenuItemsProvider.java  # 上下文菜单提供者
│   │   └── ParamNameConfigDialog.java       # 参数配置对话框
│   └── utils/
│       ├── ClipboardUtils.java              # 剪贴板工具类
│       ├── JsonUtils.java                   # JSON解析工具
│       └── MessageFormatter.java            # 消息格式化工具
├── pom.xml                                  # Maven配置文件
├── CLAUDE.md                                # Claude Code指导文档
└── README.md                                # 项目说明文档
```

## 🎨 功能演示

### 上下文菜单
- `copyHosts` - 复制唯一主机列表
- `copyHeaderKeys` - 复制请求头键名
- `copyPathWithoutQuery` - 复制路径（不含参数）
- `copyAllRequestParamValuesSet` - 复制请求参数值
- `copyAllResponseParamValuesSet` - 复制响应参数值
- `copyRequest` - 复制格式化请求报文 ⭐新功能
- `copyResponse` - 复制格式化响应报文 ⭐新功能

### 支持的工具
- ✅ **Proxy** - 完整功能支持
- ✅ **Logger** - 完整功能支持  
- ✅ **Repeater** - 支持请求/响应复制 ⭐新功能
- ✅ **Intruder** - 支持请求/响应复制 ⭐新功能

## 🔍 技术细节

### 依赖库
- **Burp Suite Montoya API** 2024.7 - Burp Suite扩展开发API
- **Jackson Databind** 2.15.4 - JSON处理和格式化
- **Fastjson2** 2.0.51 - 高性能JSON解析

### 编码处理
- 完全支持UTF-8编码
- 自动检测和跳过二进制内容
- 优先检查HTTP Content-Type头
- 自定义剪贴板数据格式支持

### 安全特性
- 自动过滤二进制文件（图片、视频、音频等）
- 智能内容检测避免处理恶意数据
- 安全的字符编码转换

## 🚀 版本历史

### v1.3 (Latest)
- ✨ 新增复制格式化请求/响应报文功能
- 🔧 支持Repeater和Intruder工具
- 🐛 修复中文字符编码问题
- ⚡ 改进二进制内容检测机制
- 🎨 优化用户界面交互

### v1.0
- 🎉 初始版本发布
- ✨ 基础复制功能实现
- 📦 支持Proxy和Logger工具

[查看完整变更记录](CHANGELOG.md)

## 🤝 贡献

欢迎提交Issue和Pull Request！

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 📄 许可证

该项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详细信息。

## 👨‍💻 作者

- **oxff** - 项目维护者
- GitHub: [@GitHubNull](https://github.com/GitHubNull)

## 🙏 致谢

- PortSwigger 提供的优秀 Burp Suite 平台
- Jackson 和 Fastjson2 JSON处理库
- 所有提出反馈和建议的用户

---

⭐ 如果这个项目对您有帮助，请给一个Star支持！