# 音频资源说明

## 目录用途
此目录用于存放应用的音频文件,包括等待音、来电铃声等。

## 所需音频文件

### 1. waiting_tone.mp3 (等待音)
- **用途**: 拨打电话时,接通前的等待提示音
- **建议时长**: 3-5秒(循环播放)
- **音频类型**: 轻柔的提示音,如"嘟...嘟..."
- **文件位置**: `assets/sounds/waiting_tone.mp3`

### 2. ringtone.mp3 (来电铃声) [预留]
- **用途**: 接收来电时的铃声
- **建议时长**: 5-10秒(循环播放)
- **音频类型**: 清晰的铃声
- **文件位置**: `assets/sounds/ringtone.mp3`

### 3. dtmf_tone.mp3 (拨号按键音)
- **用途**: 在拨号界面按数字键时的按键反馈音
- **建议时长**: 0.1-0.2秒(单次播放)
- **音频类型**: 短促的 DTMF 音(双音多频)
- **文件位置**: `assets/sounds/dtmf_tone.mp3`
- **说明**: DTMF 音是电话拨号时的标准音频,每个数字对应不同的双频音组合

## 当前状态

⚠️ **注意**: 由于版权原因,本项目不包含实际的音频文件。

### 临时方案
当前代码已实现音频播放逻辑,但由于没有实际音频文件,功能处于准备就绪状态。

### 如何添加音频文件

#### 方案1: 使用自定义音频
1. 准备 MP3 格式的音频文件
2. 将文件放入此目录:
   - `assets/sounds/waiting_tone.mp3` (等待音)
   - `assets/sounds/ringtone.mp3` (来电铃声)
   - `assets/sounds/dtmf_tone.mp3` (拨号按键音)
3. 确保 `pubspec.yaml` 已配置(已完成):
   ```yaml
   flutter:
     assets:
       - assets/sounds/
   ```
4. 代码已经实现音频播放逻辑:
   - `dialer_page.dart` 中的 `_playDialTone()` 会播放拨号音
   - `ringtone_service.dart` 中包含等待音和铃声播放逻辑
   - 如果音频文件不存在,会静默失败并保留触觉反馈

#### 方案2: 使用系统音效
可以使用 Flutter 的系统音效作为临时替代:
```dart
import 'package:flutter/services.dart';
SystemSound.play(SystemSoundType.click);
```

#### 方案3: 使用在线音频
```dart
await _player.play(UrlSource('https://example.com/waiting_tone.mp3'));
```

## 音频资源推荐来源

### 免费音频库
1. **Freesound**: https://freesound.org/
2. **Zapsplat**: https://www.zapsplat.com/
3. **FreeSound Effects**: https://www.freesoundeffects.com/

### 搜索关键词
- "phone dial tone"
- "DTMF tone" (拨号按键音)
- "call waiting tone"
- "ringtone"
- "telephone busy signal"
- "dial pad beep"

## 技术实现

### 代码位置
- **服务**: `lib/services/ringtone_service.dart` (等待音、来电铃声)
- **拨号音**: `lib/ui/dialer_page.dart` (直接使用 AudioPlayer)
- **使用**: `lib/ui/call_page.dart` (等待音)

### 播放时机
- **拨号按键音**: 在拨号界面按数字键时播放(触觉反馈 + 音频)
- **等待音**: 拨打电话后立即播放,接通后停止
- **来电铃声**: 收到来电时播放,接听或拒接后停止

### 音频控制
```dart
// 拨号按键音 (在 dialer_page.dart 中)
_dtmfPlayer.play(AssetSource('sounds/dtmf_tone.mp3'));

// 播放等待音 (在 ringtone_service.dart 中)
await ringtoneService.playWaitingTone();

// 停止播放
await ringtoneService.stop();

// 播放来电铃声
await ringtoneService.playRingtone();
```

## 注意事项

1. **音频格式**: 推荐使用 MP3 或 AAC 格式,兼容性好
2. **文件大小**: 建议单个文件不超过 500KB
3. **音量控制**: 代码中已设置默认音量为 0.5(50%)
4. **循环播放**: 等待音和铃声都设置为循环模式
5. **版权**: 确保使用的音频文件有合法使用权

## 测试建议

### 测试拨号按键音
1. 进入拨号界面
2. 点击数字键(0-9)
3. 验证按键时有触觉反馈 + 音频反馈
4. 如果没有音频文件,应该只有触觉反馈,不会报错

### 测试等待音
1. 拨打一个号码
2. 验证等待音开始播放
3. 对方接听后,验证等待音停止
4. 挂断电话,验证等待音停止

### 测试音量
- 在设备设置中调整音量
- 验证音频音量是否适中
- 可以在代码中调整 `setVolume()` 参数(0.0-1.0)

## 当前实现状态

✅ **已完成**:
- ringtone_service.dart 服务创建
- call_page.dart 集成等待音逻辑
- dialer_page.dart 集成拨号按键音(触觉 + 音频)
- pubspec.yaml 配置音频资源路径
- audioplayers 依赖添加并安装
- 错误处理:音频文件不存在时静默失败

⚠️ **待完成**:
- 添加实际的音频文件到 assets/sounds/ 目录:
  - `dtmf_tone.mp3` (拨号按键音,0.1-0.2秒)
  - `waiting_tone.mp3` (等待音,3-5秒)
  - `ringtone.mp3` (来电铃声,5-10秒)
- 测试真机音频播放效果

---

**最后更新**: 2025年10月15日
