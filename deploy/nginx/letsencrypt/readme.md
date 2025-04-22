<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-24 08:00:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 16:22:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# letsencrypt.org https 通配证书

- [letsencrypt](https://letsencrypt.org)
- [手动申请 Let's Encrypt 通配符证书](https://sspai.com/post/66008)
- [参考网站](https://certbot.eff.org/instructions?ws=nginx&os=ubuntufocal)

```bash
# 更新源
sudo apt update
# 安装snapd
sudo apt install snapd
# 查看snapd版本
snap version
# 更新snap到最新版
sudo snap install core; sudo snap refresh core
# 删除之前安装的certbot，如果之前没有安装过certbot，则忽略
# sudo apt-get remove certbot 或 sudo dnf remove certbot, 或 sudo yum remove certbot
# 重新安装certbot
sudo snap install --classic certbot
# 检查certbot是否正常运行
sudo ln -s /snap/bin/certbot /usr/bin/certbot
# 安装证书并更新nginx
# sudo certbot --nginx
# 仅用于安装证书，不更新nginx
# sudo certbot certonly --nginx
# 生成证书，支持通配符
sudo certbot certonly --manual --preferred-challenges=dns-01
# 修正：续约的时候使用这个才成功：sudo certbot --manual --preferred-challenges dns certonly
# 自动更新证书
sudo certbot renew --dry-run
# The command to renew certbot is installed in one of the following locations:
# /etc/crontab/
# /etc/cron.*/*
# systemctl list-timers
# 修改nginx配置文件 site-available 
# 重新加载nginx配置
sudo nginx -s reload
# 重启
service nginx restart
# 打开浏览器确认是否正常运行
# 暂时不支持3级域名 *.*.bytedesk.com
# The server will not issue certificates for the identifier :: Error creating new order :: Cannot issue for "*.*.bytedesk.com": Domain name has more than one wildcard
```

## 运行 sudo certbot certonly --manual --preferred-challenges=dns-01 详情，提示如下

root@VM-4-17-ubuntu:~# sudo certbot certonly --manual --preferred-challenges=dns-01
Saving debug log to /var/log/letsencrypt/letsencrypt.log
Please enter the domain name(s) you would like on your certificate (comma and/or
<!-- 注意：添加多个域名，支持2级、3级通配符域名 -->
space separated) (Enter 'c' to cancel): weiyuai.cn,*.weiyuai.cn

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
<!-- 添加域名解析TXT记录 -->
Please deploy a DNS TXT record under the name:

_acme-challenge.bytedesk.com.

with the following value:

KRxVNaEepknOJaIZt4e6cR0aTv_AydVsULksMiI7ySA

Before continuing, verify the TXT record has been deployed. Depending on the DNS
provider, this may take some time, from a few seconds to multiple minutes. You can
check if it has finished deploying with aid of online tools, such as the Google
Admin Toolbox: https://toolbox.googleapps.com/apps/dig/#TXT/_acme-challenge.bytedesk.com.
Look for one or more bolded line(s) below the line ';ANSWER'. It should show the
value(s) you've just added.

Press Enter to Continue

Successfully received certificate.
Certificate is saved at: /etc/letsencrypt/live/bytedesk.com/fullchain.pem
Key is saved at:         /etc/letsencrypt/live/bytedesk.com/privkey.pem
This certificate expires on 2022-06-09.
These files will be updated when the certificate renews.

NEXT STEPS:

- This certificate will not be renewed automatically. Autorenewal of --manual certificates requires the use of an authentication hook script (--manual-auth-hook) but one was not provided. To renew this certificate, repeat this same certbot command before the certificate's expiry date.
We were unable to subscribe you the EFF mailing list because your e-mail address appears to be invalid. You can try again later by visiting <https://act.eff.org>.

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
If you like Certbot, please consider supporting our work by:

- Donating to ISRG / Let's Encrypt:   <https://letsencrypt.org/donate>
- Donating to EFF:                    <https://eff.org/donate-le>

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
