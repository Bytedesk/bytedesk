# 接待流程

## 对话流程

```mermaid
sequenceDiagram
访客->>服务器: 打开会话页面，请求会话
服务器-->>访客: 返回会话消息
服务器-->>客服: 通知客服访客进入
loop 消息对话
    客服->>服务器: 客服发送消息
    服务器->>访客: 转发消息给访客
    访客->>服务器: 访客发送消息
    服务器-->>客服: 通知客服访客发送消息
end
```

## 技能组接待流程

## 一对一接待流程

## 说明

vscode中使用mermaid画/显示流程图，需要安装插件`Markdown Preview Mermaid Support`

- [mermaid markdown](https://marketplace.visualstudio.com/items?itemName=bierner.markdown-mermaid)
- [mermaid](https://github.com/mermaid-js/mermaid)
- [mermaid online editor](https://mermaid.live/edit)
