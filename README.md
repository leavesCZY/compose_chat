# compose_chat

一个用 Jetpack Compose 实现的 IM APP

![9c962ec62fbd43df95201a3de8a77e19_tplv-k3u1fbpfcp-zoom-1](https://user-images.githubusercontent.com/30774063/164979443-c533353f-7e47-4024-a1bc-e63005ff8938.gif)

由于腾讯云 IM SDK 免费版最多只能注册一百个账号，因此如果发现注册不了的话，可以使用以下几个我预先注册好的账号，但多设备同时登陆的话会互相挤掉线 ~~

- Google
- Android
- Jetpack
- Compose
- Flutter
- Kotlin
- Java
- Dart
- ViewModel
- LiveData

更多介绍请看这里：

- [学不动也要学，用 Jetpack Compose 写一个 IM APP（一）](https://juejin.cn/post/6991429231821684773)
- [学不动也要学，用 Jetpack Compose 写一个 IM APP（二）](https://juejin.cn/post/7028397244894330917)

## 下载体验

下载 APK：[compose_chat](https://github.com/leavesCZY/compose_chat/releases)

## 历史版本

### v1.5.3

- 从单 Activity 模式改为多 Activity 模式

### v1.5.2

- 升级依赖库
- 修复 bug

### v1.5.1

- 引入用 Jetpack Compose 实现的图片选择框架 [Matisse](https://github.com/leavesCZY/Matisse)
- 进一步适配 Material Design 3
- 优化图片的压缩策略
- 通过 buildSrc 管理依赖，统一通过 Plugin 管理配置项

### v1.5.0

- 支持 Gif 类型的图片消息和用户头像
- 支持保存 Gif 类型的图片到本地相册

### v1.4.9

- 修复 bug
- 规整代码

### v1.4.8

- 优化交互体验
- 规整代码

### v1.4.7

- 升级 targetSdkVersion 到 31
- 发送图片消息前先检测是否需要对图片进行压缩
- 优化图片消息的显示比例
- 统一消息的发送逻辑
- 修复 bug

### v1.4.6

- 修复 bug

### v1.4.5

- 升级依赖库
- 优化交互体验

### v1.4.4

- 支持保存图片到本地相册
- 支持修改个人资料时进行效果预览
- 为侧滑栏添加拖拽动画
- 升级依赖库

### v1.4.3

- 引入 Material Design 3

### v1.4.2

- 支持点击查看大图
- 支持选择本地图片作为头像
- 优化交互体验

### v1.4.0

- 支持发送图片消息
- 新增应用全局黑白化的主题

### v1.2.1

- 群主能够修改群头像

### v1.2.0

- 支持群聊
- 支持发送 emoji 表情

### v1.0.0

- 支持私聊
- 支持发送文本消息
