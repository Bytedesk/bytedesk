# CLI

Bytedesk OSS CLI foundation module.

Current scope:

- local command framework without extra third-party CLI dependencies
- agent-friendly text and JSON output
- local config and token persistence
- API-backed command groups for auth, org, ticket
- scaffolded command groups for thread, message, knowledge
- runtime entrypoint class is `com.bytedesk.cli.CliApplication`

Configuration storage:

- local config file: `~/.bytedesk/config.properties`
- commonly used keys:
  - `server.base-url`
  - `auth.token`
  - `auth.platform`
  - `auth.channel`
  - `auth.current-org-uid`
  - `auth.current-org-name`

Build and test:

```bash
./control/mvnw -f pom.xml -pl modules/cli -am -DskipTests compile
./control/mvnw -f pom.xml -pl modules/cli -am -Dtest=BytedeskCliTests -Dsurefire.failIfNoSpecifiedTests=false test
```

Run:

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar help
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar --version
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar --format=json version
```

Command overview:

- `help`: show command list
- `version`: show CLI version
- `config`: inspect or modify local CLI config
- `auth`: log in, inspect current user, or log out
- `org`: list current user organizations, inspect current org, switch org, inspect org by uid
- `ticket`: list, inspect, create, or close tickets
- `thread`, `message`, `knowledge`: placeholder command groups for later implementation

Typical workflow:

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

Authentication:

- username and password login:

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar auth login \
  --server http://127.0.0.1:9003 \
  --username admin@email.com \
  --password your-password \
  --platform BYTEDESK \
  --channel WEB
```

- access-token login:

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar auth login \
  --server http://127.0.0.1:9003 \
  --access-token your-access-token
```

- optional captcha parameters supported by login:
  - `--captcha-uid <uid>`
  - `--captcha-code <code>`

- inspect current session:

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar auth whoami
```

- log out and clear local token:

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar auth logout
```

Organization commands:

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar org list
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar org current
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar org switch --org your-org-uid
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar org get --uid your-org-uid
```

Ticket commands:

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar ticket list --page 0 --size 20 --status OPEN
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar ticket get --uid your-ticket-uid
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar ticket create \
  --title "Payment callback failed" \
  --description "Production callback returned 500" \
  --priority HIGH \
  --type BUG
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar ticket close --uid your-ticket-uid --reason resolved
```

Config commands:

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar config list
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar config get server.base-url
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar config set server.base-url http://127.0.0.1:9003
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar config remove auth.token
```

JSON output:
Prepend `--format=json` before the command name when the caller is an agent, script, or automation workflow.

```bash
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar --format=json auth whoami
java -jar modules/cli/target/bytedesk-module-cli-1.9.0.jar --format=json ticket list --page 0 --size 5
```

Current limitations:

- `thread`, `message`, and `knowledge` are still scaffolded only
- HTTP requests currently expect the Bytedesk standard JSON envelope with `code`, `message`, and `data`
- local startup usually requires the backend running and reachable, commonly on `http://127.0.0.1:9003`

More REST-backed command groups can be added on top of the same runtime without introducing an external CLI framework.
