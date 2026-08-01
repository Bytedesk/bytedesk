# FreeSWITCH 声音包与音乐资源下载与安装说明

本说明文档整理了 FreeSWITCH 官方声音提示包（sounds）与默认音乐（music）的下载地址、推荐采样率、安装步骤、验证方法，以及拨号计划中的使用示例。适用于当前部署在 118.25.178.96 的 FreeSWITCH（Ubuntu 22.04，默认声音目录位于 `/usr/local/freeswitch/sounds`）。

## 官方下载目录

- 声音提示（英语 en-us-callie）
  - 8000Hz: <https://files.freeswitch.org/releases/sounds/freeswitch-sounds-en-us-callie-8000-1.0.52.tar.gz>
  - 16000Hz: <https://files.freeswitch.org/releases/sounds/freeswitch-sounds-en-us-callie-16000-1.0.52.tar.gz>
  - 32000Hz: <https://files.freeswitch.org/releases/sounds/freeswitch-sounds-en-us-callie-32000-1.0.52.tar.gz>
  - 48000Hz: <https://files.freeswitch.org/releases/sounds/freeswitch-sounds-en-us-callie-48000-1.0.52.tar.gz>

- 默认音乐（MOH）
  - 48000Hz: <https://files.freeswitch.org/releases/music/freeswitch-sounds-music-48000-1.0.7.tar.gz>
  - 32000Hz: <https://files.freeswitch.org/releases/music/freeswitch-sounds-music-32000-1.0.7.tar.gz>
  - 16000Hz: <https://files.freeswitch.org/releases/music/freeswitch-sounds-music-16000-1.0.7.tar.gz>
  - 8000Hz:  <https://files.freeswitch.org/releases/music/freeswitch-sounds-music-8000-1.0.7.tar.gz>

- 目录索引（如需查看可用文件）：
  - 声音提示目录索引：<https://files.freeswitch.org/releases/sounds/>
  - 默认音乐目录索引：<https://files.freeswitch.org/releases/music/>

提示：部分旧版本（如 1.0.2/1.0.3/1.0.5/1.0.6/1.0.7）仍可用，如果遇到 404，可以访问目录索引确认具体文件名后再下载。

## 采样率如何选择

- WebRTC（浏览器）场景：推荐 48000Hz（与浏览器音频栈一致，减少重采样）。
- 传统窄带（如 G.711）场景：可选 8000Hz。
- 宽带语音：16k/32k 也可选，具体视业务链路/终端能力而定。
- 可并存多种采样率目录，FreeSWITCH 会按配置/通道能力选择或重采样；为提升质量，尽量选与终端一致的采样率。

## 安装步骤（直接下载与解压）

以下步骤默认将资源放到 `/usr/local/freeswitch/sounds`，该路径通常对应 `$${sounds_dir}`。

1) 创建目录（如不存在）

```bash
sudo mkdir -p /usr/local/freeswitch/sounds
```

1) 进入目录并下载压缩包（按需选择采样率）

- 声音提示（示例 48000Hz）：

```bash
cd /usr/local/freeswitch/sounds
curl -fL -O https://files.freeswitch.org/releases/sounds/freeswitch-sounds-en-us-callie-48000-1.0.52.tar.gz
```

- 默认音乐（示例 48000Hz）：

```bash
cd /usr/local/freeswitch/sounds
curl -fL -O https://files.freeswitch.org/releases/music/freeswitch-sounds-music-48000-1.0.7.tar.gz
```

1) 解压

```bash
cd /usr/local/freeswitch/sounds
sudo tar -xzf freeswitch-sounds-en-us-callie-48000-1.0.52.tar.gz
sudo tar -xzf freeswitch-sounds-music-48000-1.0.7.tar.gz
```

1) 目录结构（示例）

- 声音提示：`/usr/local/freeswitch/sounds/en/us/callie/ivr/48000/*.wav`
- 默认音乐：`/usr/local/freeswitch/sounds/music/48000/*`

1) 权限（如需要）

```bash
sudo chown -R freeswitch:freeswitch /usr/local/freeswitch/sounds
sudo chmod -R 755 /usr/local/freeswitch/sounds
```

## 验证安装

- 在 FreeSWITCH CLI 查看 sounds 目录值

```bash
fs_cli -x 'eval $${sounds_dir}'
```

- 检查目标文件是否存在

```bash
ls -lah /usr/local/freeswitch/sounds/en/us/callie/ivr/48000 | head
```

- 播放验证（拨号计划或命令行）
  - 拨号计划 `playback` 示例见下一节
  - 也可在拨测分机接通后 `playback` 某个 ivr 文件

## 拨号计划中的使用示例

- 使用文件播放（声音包安装完成后）

```xml
<action application="playback" data="en/us/callie/ivr/48000/ivr-echo_your_audio_back.wav"/>
```

- 使用内置音调（无需文件）

```xml
<action application="playback" data="tone_stream://%(1000,0,640)"/>
```

- hold music（本机 `autoload_configs/local_stream.conf.xml` 已定义）
  - 目录映射：`$${sounds_dir}/music/8000|16000|32000|48000`
  - 在通话中可使用：`local_stream://moh/48000`（或 `moh/8000` 等）
  - 变量 `hold_music` 可设为：`local_stream://moh`

## 常见问题

- 下载 404：
  - 访问目录索引（sounds/music）确认存在的文件名与版本再下载。
- 播放失败：
  - 路径拼写错误或权限问题；用 `fs_cli -x 'eval $${sounds_dir}'` 确认根路径，再拼接相对路径测试。
- 有信令无媒体：
  - 与声音包无关，多因 RTP 端口未放行或 NAT/ICE/TURN 配置导致；放行 UDP 16384–32768，并在浏览器/客户端确认麦克风权限与 ICE 服务器设置。

## APT 仓库方式（说明）

- 在部分发行版/仓库中存在声音包的二进制包（如 `freeswitch-sounds-en-us-callie`、`freeswitch-music-default` 等），但 Ubuntu 22.04 上的常见第三方源可能不稳定或不可用。
- 如遇 APT 包不可用，建议采用上述“直接下载与解压”的方式，简单可靠。

## 变更记录

- 2025-10-11：新增文档，收录 48k 声音与音乐下载链接与安装步骤，并补充拨号计划使用方法。

<!--  -->
2025-10-23 13:26:16.080924 0.00% [CONSOLE] mod_local_stream.c:289 Can't open directory: /usr/local/freeswitch/sounds/music/16000
2025-10-23 13:26:16.080969 0.00% [CONSOLE] mod_local_stream.c:289 Can't open directory: /usr/local/freeswitch/sounds/music/8000
2025-10-23 13:26:16.080969 0.00% [CONSOLE] mod_local_stream.c:289 Can't open directory: /usr/local/freeswitch/sounds/music/32000
2025-10-23 13:26:16.080987 0.00% [CONSOLE] mod_local_stream.c:289 Can't open directory: /usr/local/freeswitch/sounds/music/8000
