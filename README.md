# CreditApp Android WebView 应用

一个简单的 Android WebView 容器应用，用于访问 CreditApp 家庭积分管理系统。

## 功能特性

- 📱 **WebView 容器** - 完整的 Web 应用容器
- 🔧 **可配置服务器地址** - 支持用户自定义服务器地址
- 🔄 **下拉刷新** - 支持下拉刷新页面
- 📶 **网络状态检测** - 自动检测网络连接状态
- 💾 **本地缓存** - 支持 WebView 缓存
- 🔐 **记住登录** - 可选择记住登录状态
- 🎨 **Material Design** - 现代化 UI 设计

## 技术栈

- **语言**: Kotlin
- **最低 SDK**: 24 (Android 7.0)
- **目标 SDK**: 34 (Android 14)
- **架构**: 单 Activity + Fragment
- **UI**: Material Design 3

## 快速开始

### 环境要求

- Android Studio Hedgehog 或更高版本
- JDK 17+
- Gradle 8.4+

### 构建步骤

1. 克隆项目
```bash
git clone <repository-url>
cd creditapp-android
```

2. 打开 Android Studio，导入项目

3. 等待 Gradle 同步完成

4. 连接 Android 设备或启动模拟器

5. 点击 Run 按钮构建并运行

### 配置服务器地址

首次启动时，应用会弹出配置对话框，输入你的 CreditApp 服务器地址：

```
http://192.168.1.100:8080
```

或者进入 **设置** 页面修改服务器地址。

## 项目结构

```
app/
├── src/main/
│   ├── java/com/creditapp/webview/
│   │   ├── MainActivity.kt       # 主 Activity（WebView 容器）
│   │   ├── SettingsActivity.kt   # 设置页面
│   │   └── AppPreferences.kt     # SharedPreferences 封装
│   ├── res/
│   │   ├── layout/              # 布局文件
│   │   ├── drawable/            # 图标资源
│   │   ├── menu/                # 菜单资源
│   │   └── values/              # 字符串、颜色、主题
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## 使用说明

### 基本操作

| 操作 | 说明 |
|------|------|
| 下拉 | 刷新页面 |
| 返回键 | 返回上一页或退出 |
| 菜单 → 设置 | 配置服务器地址 |
| 菜单 → 刷新 | 重新加载页面 |
| 菜单 → 首页 | 返回服务器首页 |

### 服务器要求

- 支持 HTTP 或 HTTPS
- 确保服务器地址在局域网内可访问
- 建议使用内网 IP 地址（如 192.168.x.x）

## 开发指南

### 添加新功能

1. 在 `res/layout/` 添加布局文件
2. 创建对应的 Activity 或 Fragment
3. 在 `AndroidManifest.xml` 注册新组件
4. 更新菜单或导航逻辑

### 自定义主题

编辑 `res/values/colors.xml` 修改主题色：

```xml
<color name="primary">#1976D2</color>
<color name="primary_dark">#1565C0</color>
<color name="accent">#FF4081</color>
```

### 修改默认 URL

编辑 `MainActivity.kt` 中的 `DEFAULT_URL` 常量：

```kotlin
companion object {
    const val DEFAULT_URL = "http://your-server:8080"
}
```

## 发布说明

### 版本 1.0.0

- ✅ 基础 WebView 容器功能
- ✅ 可配置服务器地址
- ✅ 下拉刷新
- ✅ 网络状态检测
- ✅ 记住登录状态
- ✅ 清除缓存功能
- ✅ Material Design 3 UI

## 常见问题

**Q: 页面加载失败怎么办？**

A: 检查以下几点：
1. 确保设备已连接网络
2. 确认服务器地址正确（包含 http:// 或 https://）
3. 确认服务器正在运行
4. 检查防火墙是否允许访问

**Q: 如何清除缓存？**

A: 进入 设置 → 点击"清除缓存"按钮

**Q: 支持哪些 Android 版本？**

A: Android 7.0 (API 24) 及以上版本

## 许可证

MIT License

## 相关项目

- [CreditApp](../creditapp) - 家庭积分管理系统后端