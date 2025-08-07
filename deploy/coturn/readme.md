---
sidebar_label: Coturn
sidebar_position: 15
---

# Coturn

## STUN/TURN 服务安装

```bash
# https://github.com/coturn/coturn
apt install coturn
# 查看日志
turnserver --log-file stdout
# 启动运行
sudo systemctl start coturn
# 或
turnserver
# turnserver -c /etc/turnserver.conf
# 查看运行状态
sudo systemctl status coturn
# 停止运行
sudo systemctl stop coturn
# 重启运行
sudo systemctl restart coturn
# 
netstat -anp|grep turnserver
```

## 防火墙开放端口号

```bash
ufw allow 3478
# ufw status
# ufw disable
# 3478 TCP
```

## 修改 /etc/turnserver.conf 配置文件

```bash
# 备份配置文件
cp /etc/turnserver.conf /etc/turnserver.conf.original
# 下面的四行原本都是注释掉的，去掉前面的 #， 添加参数：
# 这个端口号是默认的
listening-port=3478    
# 此ip为服务器公网ip，替换为自己的  
external-ip=47.**.**.81
# 这个是用户名:密码，替换为自己的用户名和密码
user=username1:password1
user=username2:password2
# 这里填写域名，替换为自己的
realm=coturn.weiyuai.cn
```

## 修改 /etc/default/coturn 配置文件

```bash
# 去掉前面的注释 #
TURNSERVER_ENABLED=1
```

## 重启 turnserver

```bash
sudo systemctl restart coturn
```

## 测试

- [WebRTC 测试](https://webrtc.github.io/samples/src/content/peerconnection/trickle-ice/)

![WebRTC 测试](/img/deploy/webrtc/coturn_turn_stun_test.png)

## 参考资料

- [Samples](https://webrtc.github.io/samples/)
- [Github Samples](https://github.com/webrtc/samples)
- [WebRTC API](https://developer.mozilla.org/en-US/docs/Web/API/WebRTC_API)
- [Janus Gateway](https://github.com/meetecho/janus-gateway)
