import{u as l,j as n}from"./index-CJncBc6B.js";const o={title:"POST 发送消息",openapi:"POST /api/v1/message/send"};function t(s){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...l(),...s.components},{ApiPlayground:d,RequestExample:r,ResponseExample:c}=e;return d||i("ApiPlayground"),r||i("RequestExample"),c||i("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"向指定的会话发送消息，支持文本、图片、文件等多种消息类型。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(d,{method:"POST",endpoint:"/api/v1/message/send"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于向指定的会话发送消息。消息类型包括文本、图片、语音、视频、文件、位置、卡片等。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "threadUid": "thread_123456",
  "type": "text",
  "content": "消息内容",
  "extra": {}
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"请求字段",children:"请求字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"threadUid"})," (必需): 会话唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"type"})," (必需): 消息类型",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"text"}),": 文本消息"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"image"}),": 图片消息"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"audio"}),": 语音消息"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"video"}),": 视频消息"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"file"}),": 文件消息"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"location"}),": 位置消息"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"card"}),": 卡片消息"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"event"}),": 事件消息"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"content"})," (必需): 消息内容，根据类型不同格式不同"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"extra"})," (可选): 扩展字段，用于存储额外的自定义数据"]}),`
`]}),`
`,n.jsx(e.h3,{id:"消息对象字段",children:"消息对象字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 消息唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"threadUid"}),": 会话唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"senderUid"}),": 发送者用户唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"receiverUid"}),": 接收者用户唯一标识符（私聊时使用）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"type"}),": 消息类型"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"content"}),": 消息内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"extra"}),": 扩展字段"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"}),": 消息状态（SENDING, SENT, READ, FAILED）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 消息创建时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"即时通讯"}),`
`,n.jsx(e.li,{children:"客服对话"}),`
`,n.jsx(e.li,{children:"群组聊天"}),`
`,n.jsx(e.li,{children:"系统通知"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"需要先加入会话才能发送消息"}),`
`,n.jsx(e.li,{children:"消息内容会经过敏感词过滤"}),`
`,n.jsx(e.li,{children:"文件、图片等需要先上传获取 URL"}),`
`,n.jsx(e.li,{children:"消息发送后会通过 WebSocket 推送给会话成员"}),`
`]}),`
`,n.jsxs(c,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "msg_123456",
    "threadUid": "thread_123456",
    "senderUid": "user_123456",
    "type": "text",
    "content": "你好",
    "extra": {},
    "status": "SENT",
    "createdAt": "2025-01-07T10:30:00Z"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Thread not found",
  "data": null
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 403,
  "message": "Not a member of this thread",
  "data": null
}
`})})]}),`
`,n.jsxs(r,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`# 发送文本消息
curl -X POST "https://api.weiyuai.cn/api/v1/message/send" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "threadUid": "thread_123456",
    "type": "text",
    "content": "你好，这是一条测试消息"
  }'

# 发送图片消息
curl -X POST "https://api.weiyuai.cn/api/v1/message/send" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "threadUid": "thread_123456",
    "type": "image",
    "content": "https://example.com/image.jpg",
    "extra": {
      "width": 800,
      "height": 600
    }
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`// 发送文本消息
const response = await fetch('https://api.weiyuai.cn/api/v1/message/send', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    threadUid: 'thread_123456',
    type: 'text',
    content: '你好，这是一条测试消息'
  })
});

const result = await response.json();
const message = result.data;
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/message/send"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Content-Type": "application/json"
}

# 发送文本消息
data = {
  "threadUid": "thread_123456",
  "type": "text",
  "content": "你好，这是一条测试消息"
}

response = requests.post(url, headers=headers, json=data)
result = response.json()
message = result["data"]
`})})]})]})}function h(s={}){const{wrapper:e}={...l(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(t,{...s})}):t(s)}function i(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,o as frontmatter};
