# 发布流程

## 发布到 Cocoapod 流程

- 1. 替换更新文件
- 2. 更新 Podfile 版本号
- 3. 提交GitHub
- 4. 打标签，并提交

```bash
# 设置代理
# export http_proxy=http://127.0.0.1:10818
# export https_proxy=http://127.0.0.1:10818
# --skip-import-validation
5. arch -x86_64 pod spec lint bytedesk-oc.podspec --allow-warnings --verbose --skip-import-validation
6. arch -x86_64 pod trunk push bytedesk-oc.podspec --allow-warnings --verbose
```

### 其他

- 给项目添加pod支持，项目根目录执行：pod init，然后 pod install
- 注册：arch -x86_64 pod trunk register <email@qq.com> jack
- 查看个人信息：pod trunk me
- 创建pod初始化文件：pod spec create bytedesk-oc

## 支持 Carthage 流程

```bash
brew update
# 安装 carthage
brew install carthage
# 添加依赖，如：github "AFNetworking/AFNetworking" ~> 4.0
# 更新
carthage update
# 编译
carthage build --no-skip-current
# 注意：
# 如果你使用了类别,那么你需要在Build Settings的Linking的Other Linker Flags里加上-all_load
# 如果你想你的工程支持bitcode,需要在Other C Flags 里加上-fembed-bitcode
```

## 支持 Swift Package Manager (SPM) 流程

```bash
# 在现有framework项目中添加支持SPM，会生成相应的文件和目录
swift package init --type library
```
