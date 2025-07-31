# 微语登录性能测试

本目录包含用于测试微语系统登录功能的JMeter测试脚本。

## 文件说明

- `01_login.jmx` - JMeter测试计划文件，包含多种登录方式的测试
- `users.csv` - 测试用户数据文件
- `run_login_test.sh` - 测试执行脚本
- `README.md` - 本说明文件

## 测试覆盖范围

### 1. 获取验证码测试

- **接口**: `GET /kaptcha/api/v1/get`
- **并发用户**: 100
- **加压时间**: 10秒
- **测试目的**: 验证验证码生成服务的性能

### 2. 用户名密码登录测试

- **接口**: `POST /auth/v1/login`
- **并发用户**: 500
- **加压时间**: 30秒
- **持续时间**: 10分钟
- **测试目的**: 测试传统用户名密码登录的性能

### 3. 手机验证码登录测试

- **发送验证码接口**: `POST /auth/v1/send/mobile`
- **登录接口**: `POST /auth/v1/login/mobile`
- **并发用户**: 200
- **加压时间**: 20秒
- **持续时间**: 5分钟
- **测试目的**: 测试手机验证码登录流程的性能

### 4. 邮箱验证码登录测试

- **发送验证码接口**: `POST /auth/v1/send/email`
- **登录接口**: `POST /auth/v1/login/email`
- **并发用户**: 100
- **加压时间**: 10秒
- **持续时间**: 5分钟
- **测试目的**: 测试邮箱验证码登录流程的性能

### 5. AccessToken登录测试

- **接口**: `POST /auth/v1/login/accessToken`
- **并发用户**: 300
- **加压时间**: 15秒
- **持续时间**: 5分钟
- **测试目的**: 测试Token验证登录的性能

## 环境要求

### 软件要求

- Apache JMeter 5.5+
- Java 8+
- Bash shell (用于执行脚本)

### 系统要求

- 内存: 至少4GB可用内存
- 磁盘: 至少1GB可用空间用于存储测试结果
- 网络: 稳定的网络连接到测试目标服务器

## 安装和配置

### 1. 安装JMeter

#### Linux/macOS

```bash
# 下载JMeter
wget https://downloads.apache.org/jmeter/binaries/apache-jmeter-5.5.tgz

# 解压
tar -xzf apache-jmeter-5.5.tgz

# 移动到合适位置
sudo mv apache-jmeter-5.5 /usr/local/
```

#### Windows

1. 下载JMeter: <https://jmeter.apache.org/download_jmeter.cgi>
2. 解压到合适目录
3. 将bin目录添加到PATH环境变量

### 2. 配置测试环境

#### 修改测试目标地址

编辑 `01_login.jmx` 文件中的用户定义变量：

```xml
<elementProp name="host" elementType="Argument">
  <stringProp name="Argument.name">host</stringProp>
  <stringProp name="Argument.value">your-server-host</stringProp>
</elementProp>
<elementProp name="port" elementType="Argument">
  <stringProp name="Argument.name">port</stringProp>
  <stringProp name="Argument.value">9003</stringProp>
</elementProp>
```

#### 准备测试数据

确保 `users.csv` 文件包含有效的测试用户数据：

```csv
username,password,email,mobile
testuser1,password123,testuser1@example.com,13800138001
...
```

## 执行测试

### 方法1: 使用脚本执行（推荐）

```bash
# 使用默认JMeter路径
./run_login_test.sh

# 指定JMeter路径
./run_login_test.sh /path/to/jmeter/bin
```

### 方法2: 直接使用JMeter命令

```bash
# 进入jmeter目录
cd jmeter

# 执行测试
jmeter -n -t 01_login.jmx -l results/test_result.jtl -e -o reports/test_report
```

### 方法3: 使用JMeter GUI

1. 启动JMeter GUI

```bash
jmeter
```

2. 打开测试计划文件 `01_login.jmx`
3. 点击运行按钮开始测试

## 测试结果分析

### 结果文件

- `results/` - 原始测试结果文件(.jtl)
- `reports/` - HTML格式的测试报告

### 关键指标

- **响应时间**: 平均响应时间、90/95/99百分位响应时间
- **吞吐量**: 每秒处理的请求数(TPS)
- **错误率**: 请求失败的比例
- **并发用户数**: 系统能够支持的最大并发用户数

### 性能基准

根据测试计划文档，建议的性能基准：

- 登录响应时间 < 2秒
- 错误率 < 1%
- 支持500并发用户登录

## 故障排除

### 常见问题

#### 1. JMeter找不到

```
错误: 找不到JMeter可执行文件
```

**解决方案**: 确保JMeter已正确安装，或通过参数指定正确的路径

#### 2. 测试数据文件不存在

```
错误: 找不到测试数据文件: users.csv
```

**解决方案**: 确保 `users.csv` 文件存在于当前目录

#### 3. 网络连接失败

```
错误: 连接被拒绝
```

**解决方案**:

- 检查目标服务器是否运行
- 验证端口配置是否正确
- 检查防火墙设置

#### 4. 验证码错误

```
错误: 验证码验证失败
```

**解决方案**:

- 检查验证码接口是否正常工作
- 确认验证码缓存服务配置正确

### 调试技巧

1. **启用详细日志**

```bash
jmeter -n -t 01_login.jmx -l results/test_result.jtl -e -o reports/test_report -L DEBUG
```

2. **查看JMeter日志**

```bash
tail -f jmeter.log
```

3. **使用JMeter GUI调试**

- 在GUI模式下运行单个请求
- 查看请求和响应详情

## 扩展和定制

### 添加新的登录方式

1. 在测试计划中添加新的线程组
2. 配置相应的HTTP请求
3. 添加必要的断言和提取器

### 修改测试参数

- 调整并发用户数
- 修改加压时间
- 更改测试持续时间

### 集成到CI/CD

```bash
# 在CI/CD流水线中执行测试
./run_login_test.sh
# 检查测试结果
if [ $? -eq 0 ]; then
    echo "性能测试通过"
else
    echo "性能测试失败"
    exit 1
fi
```

## 联系和支持

如有问题或建议，请联系开发团队或提交Issue。

## 更新日志

- v1.0 - 初始版本，支持基本登录功能测试
- 支持用户名密码、手机验证码、邮箱验证码、AccessToken四种登录方式
- 包含完整的测试数据准备和结果分析
