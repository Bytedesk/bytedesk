# CLI

微语通用 CLI 基础模块。

当前阶段能力：

- 不引入额外 CLI 框架，使用仓库现有 Java 体系实现命令行入口
- 同时支持文本输出和 JSON 输出，便于 agent 调用
- 提供本地配置、token 存储能力
- 已接入 auth、org、ticket 三组真实 API 命令
- 预留 thread、message、knowledge 命令组
- 运行入口类使用 com.bytedesk.cli.CliApplication

配置文件：

- 本地配置路径：`~/.bytedesk/config.properties`
- 当前常用键：
  - `server.base-url`
  - `auth.token`
  - `auth.platform`
  - `auth.channel`
  - `auth.current-org-uid`
  - `auth.current-org-name`

构建与测试：

```bash
./starter/mvnw -f pom.xml -pl modules/cli -am -DskipTests compile
./starter/mvnw -f pom.xml -pl modules/cli -am -Dtest=BytedeskCliTests -Dsurefire.failIfNoSpecifiedTests=false test
```

运行方式：

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar help
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar --version
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar --format=json version
```

当前命令说明：

- `help`：查看命令列表
- `version`：查看 CLI 版本
- `config`：查看或修改本地配置
- `auth`：登录、查看当前登录信息、退出登录
- `org`：列出组织、查看当前组织、切换组织、按 uid 查看组织
- `ticket`：查询、查看、创建、关闭工单
- `thread`、`message`、`knowledge`：当前仍为占位命令组

推荐使用流程：

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar auth login \
  --server http://127.0.0.1:9003 \
  --username admin@email.com \
  --password your-password

java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar auth whoami
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar org list
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar org switch --org your-org-uid
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar ticket list --page 0 --size 10
```

认证命令：

- 用户名密码登录：

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar auth login \
  --server http://127.0.0.1:9003 \
  --username admin@email.com \
  --password your-password \
  --platform BYTEDESK \
  --channel WEB
```

- access token 登录：

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar auth login \
  --server http://127.0.0.1:9003 \
  --access-token your-access-token
```

- 登录时支持验证码参数：
  - `--captcha-uid <uid>`
  - `--captcha-code <code>`

- 查看当前登录用户：

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar auth whoami
```

- 退出登录并清理本地 token：

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar auth logout
```

组织命令：

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar org list
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar org current
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar org switch --org your-org-uid
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar org get --uid your-org-uid
```

工单命令：

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar ticket list --page 0 --size 20 --status OPEN
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar ticket get --uid your-ticket-uid
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar ticket create \
  --title "支付回调失败" \
  --description "生产环境回调接口返回 500" \
  --priority HIGH \
  --type BUG
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar ticket close --uid your-ticket-uid --reason resolved
```

配置命令：

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar config list
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar config get server.base-url
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar config set server.base-url http://127.0.0.1:9003
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar config remove auth.token
```

JSON 输出：

- 如果给 agent、脚本或自动化流程使用，请将 `--format=json` 放在命令名前面

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar --format=json auth whoami
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar --format=json ticket list --page 0 --size 5
```

当前限制：

- `thread`、`message`、`knowledge` 还只是占位实现
- 当前 HTTP 调用默认依赖微语服务端标准返回结构：`code`、`message`、`data`
- 本地联调通常需要服务端先启动，常见地址是 `http://127.0.0.1:9003`

后续可在此基础上继续接入更多 REST API、MCP 适配层和自动化工作流。
