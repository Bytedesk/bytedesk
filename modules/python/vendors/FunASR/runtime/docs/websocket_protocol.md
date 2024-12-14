([简体中文](./websocket_protocol_zh.md)|English)

# WebSocket/gRPC Communication Protocol
This protocol is the communication protocol for the FunASR software package, which includes offline file transcription ([deployment document](./SDK_tutorial.md)) and real-time speech recognition ([deployment document](./SDK_tutorial_online.md)).

## Offline File Transcription
### Sending Data from Client to Server
#### Message Format
Configuration parameters and meta information are in JSON format, while audio data is in bytes.
#### Initial Communication
The message (which needs to be serialized in JSON) is:
```text
{"mode": "offline", "wav_name": "wav_name", "wav_format":"pcm", "is_speaking": True, "wav_format":"pcm", "hotwords":"{"阿里巴巴":20,"通义实验室":30}", "itn":True}
```
Parameter explanation:
```text
`mode`: `offline`, indicating the inference mode for offline file transcription
`wav_name`: the name of the audio file to be transcribed
`wav_format`: the audio and video file extension, such as pcm, mp3, mp4, etc.
`is_speaking`: False indicates the end of a sentence, such as a VAD segmentation point or the end of a WAV file
`audio_fs`: when the input audio is in PCM format, the audio sampling rate parameter needs to be added
`hotwords`：If using the hotword, you need to send the hotword data (string) to the server. For example："{"阿里巴巴":20,"通义实验室":30}"
`itn`: whether to use itn, the default value is true for enabling and false for disabling.
```

#### Sending Audio Data
For PCM format, directly send the audio data. For other audio formats, send the header information and audio and video bytes data together. Multiple sampling rates and audio and video formats are supported.

#### Sending End of Audio Flag
After sending the audio data, an end-of-audio flag needs to be sent (which needs to be serialized in JSON):
```text
{"is_speaking": False}
```

### Sending Data from Server to Client
#### Sending Recognition Results
The message (serialized in JSON) is:
```text
{"mode": "offline", "wav_name": "wav_name", "text": "asr ouputs", "is_final": True, "timestamp":"[[100,200], [200,500]]", "stamp_sents":[]}
```
Parameter explanation:
```text
`mode`: `offline`, indicating the inference mode for offline file transcription
`wav_name`: the name of the audio file to be transcribed
`text`: the text output of speech recognition
`is_final`: indicating the end of recognition
`timestamp`：If AM is a timestamp model, it will return this field, indicating the timestamp, in the format of "[[100,200], [200,500]]"
`stamp_sents`：If AM is a timestamp model, it will return this field, indicating the stamp_sents, in the format of [{"text_seg":"正 是 因 为","punc":",","start":430,"end":1130,"ts_list":[[430,670],[670,810],[810,1030],[1030,1130]]}]
```

## Real-time Speech Recognition
### System Architecture Diagram

<div align="left"><img src="images/2pass.jpg" width="600"/></div>

### Sending Data from Client to Server
#### Message Format
Configuration parameters and meta information are in JSON format, while audio data is in bytes.

#### Initial Communication
The message (which needs to be serialized in JSON) is:
```text
{"mode": "2pass", "wav_name": "wav_name", "is_speaking": True, "wav_format":"pcm", "chunk_size":[5,10,5],"hotwords":"{"阿里巴巴":20,"通义实验室":30}","itn":true}
```
Parameter explanation:
```text
`mode`: `offline` indicates the inference mode for single-sentence recognition; `online` indicates the inference mode for real-time speech recognition; `2pass` indicates real-time speech recognition and offline model correction for sentence endings.
`wav_name`: the name of the audio file to be transcribed
`wav_format`: the audio and video file extension, such as pcm, mp3, mp4, etc. (Note: only PCM audio streams are supported in version 1.0)
`is_speaking`: False indicates the end of a sentence, such as a VAD segmentation point or the end of a WAV file
`chunk_size`: indicates the latency configuration of the streaming model, `[5,10,5]` indicates that the current audio is 600ms long, with a 300ms look-ahead and look-back time.
`audio_fs`: when the input audio is in PCM format, the audio sampling rate parameter needs to be added
`hotwords`：If using the hotword, you need to send the hotword data (string) to the server. For example："{"阿里巴巴":20,"通义实验室":30}"
`itn`: whether to use itn, the default value is true for enabling and false for disabling.
```
#### Sending Audio Data
Directly send the audio data, removing the header information and sending only the bytes data. Supported audio sampling rates are 8000 (which needs to be specified as audio_fs in message), and 16000.
#### Sending End of Audio Flag
After sending the audio data, an end-of-audio flag needs to be sent (which needs to be serialized in JSON):
```text
{"is_speaking": False}
```
### Sending Data from Server to Client
#### Sending Recognition Results
The message (serialized in JSON) is:

```text
{"mode": "2pass-online", "wav_name": "wav_name", "text": "asr ouputs", "is_final": True, "timestamp":"[[100,200], [200,500]]", "stamp_sents":[]}
```
Parameter explanation:
```text
`mode`: indicates the inference mode, divided into `2pass-online` for real-time recognition results and `2pass-offline` for 2-pass corrected recognition results.
`wav_name`: the name of the audio file to be transcribed
`text`: the text output of speech recognition
`is_final`: indicating the end of recognition
`timestamp`：If AM is a timestamp model, it will return this field, indicating the timestamp, in the format of "[[100,200], [200,500]]"
`stamp_sents`：If AM is a timestamp model, it will return this field, indicating the stamp_sents, in the format of [{"text_seg":"正 是 因 为","punc":",","start":430,"end":1130,"ts_list":[[430,670],[670,810],[810,1030],[1030,1130]]}]
```
