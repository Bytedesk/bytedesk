# Jitsi

- [参考文档](https://jitsi.github.io/handbook/docs/devops-guide/devops-guide-docker)

```bash
# 切换目录
cd jitsi-docker
# 复制
cp env.example .env
# 生成密码
./gen-passwords.sh
# 创建目录
mkdir -p ~/.jitsi-meet-cfg/{web,transcripts,prosody/config,prosody/prosody-plugins-custom,jicofo,jvb,jigasi,jibri}
# 打开目录
open ~/.jitsi-meet-cfg
# 运行
docker compose up -d
```
