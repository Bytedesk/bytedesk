import{u as c,j as n}from"./index-CJncBc6B.js";const a={title:"POST 发送消息",openapi:"POST /messages"};function h(s){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",ul:"ul",...c(),...s.components},{ApiPlayground:r,RequestExample:t,ResponseExample:i}=e;return r||d("ApiPlayground"),t||d("RequestExample"),i||d("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"在指定会话中发送消息，支持文本、图片、文件等多种消息类型。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"POST",endpoint:"/api/v1/messages"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于在已存在的会话中发送消息，用户可以发送不同类型的内容与其他参与者进行沟通。"}),`
`,n.jsx(e.h2,{id:"请求参数",children:"请求参数"}),`
`,n.jsx(e.h3,{id:"headers",children:"Headers"}),`
`,n.jsxs(e.table,{children:[n.jsx(e.thead,{children:n.jsxs(e.tr,{children:[n.jsx(e.th,{children:"参数名"}),n.jsx(e.th,{children:"类型"}),n.jsx(e.th,{children:"必需"}),n.jsx(e.th,{children:"说明"})]})}),n.jsxs(e.tbody,{children:[n.jsxs(e.tr,{children:[n.jsx(e.td,{children:"Authorization"}),n.jsx(e.td,{children:"string"}),n.jsx(e.td,{children:"✓"}),n.jsx(e.td,{children:"Bearer Token 认证"})]}),n.jsxs(e.tr,{children:[n.jsx(e.td,{children:"Content-Type"}),n.jsx(e.td,{children:"string"}),n.jsx(e.td,{children:"✓"}),n.jsx(e.td,{children:"application/json"})]})]})]}),`
`,n.jsx(e.h3,{id:"body-parameters",children:"Body Parameters"}),`
`,n.jsxs(e.table,{children:[n.jsx(e.thead,{children:n.jsxs(e.tr,{children:[n.jsx(e.th,{children:"参数名"}),n.jsx(e.th,{children:"类型"}),n.jsx(e.th,{children:"必需"}),n.jsx(e.th,{children:"说明"})]})}),n.jsxs(e.tbody,{children:[n.jsxs(e.tr,{children:[n.jsx(e.td,{children:"threadId"}),n.jsx(e.td,{children:"string"}),n.jsx(e.td,{children:"✓"}),n.jsx(e.td,{children:"目标会话的 ID"})]}),n.jsxs(e.tr,{children:[n.jsx(e.td,{children:"type"}),n.jsx(e.td,{children:"string"}),n.jsx(e.td,{children:"✓"}),n.jsx(e.td,{children:"消息类型：TEXT/IMAGE/FILE"})]}),n.jsxs(e.tr,{children:[n.jsx(e.td,{children:"content"}),n.jsx(e.td,{children:"string"}),n.jsx(e.td,{children:"✓"}),n.jsx(e.td,{children:"消息内容"})]})]})]}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"id"}),": 消息唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"threadId"}),": 所属会话 ID"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"senderId"}),": 发送者用户 ID"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"type"}),": 消息类型"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"content"}),": 消息内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 消息发送时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"消息类型详解",children:"消息类型详解"}),`
`,n.jsx(e.h3,{id:"text文本消息",children:"TEXT（文本消息）"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "threadId": "thread_123456",
  "type": "TEXT",
  "content": "Hello, world!"
}
`})}),`
`,n.jsx(e.h3,{id:"image图片消息",children:"IMAGE（图片消息）"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "threadId": "thread_123456",
  "type": "IMAGE", 
  "content": "https://api.weiyuai.cn/images/photo.jpg"
}
`})}),`
`,n.jsx(e.h3,{id:"file文件消息",children:"FILE（文件消息）"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "threadId": "thread_123456",
  "type": "FILE",
  "content": "{\\"url\\":\\"https://api.weiyuai.cn/files/document.pdf\\",\\"name\\":\\"report.pdf\\",\\"size\\":1024000}"
}
`})}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"即时聊天通讯"}),`
`,n.jsx(e.li,{children:"客服对话交流"}),`
`,n.jsx(e.li,{children:"群组讨论发言"}),`
`,n.jsx(e.li,{children:"文件共享传输"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"需要有会话的发送权限"}),`
`,n.jsx(e.li,{children:"图片和文件需要先上传获得 URL"}),`
`,n.jsx(e.li,{children:"消息发送后不可修改"}),`
`,n.jsx(e.li,{children:"系统可能对消息内容进行审核"}),`
`]}),`
`,n.jsxs(i,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "id": "msg_789012",
  "threadId": "thread_123456",
  "senderId": "user_123456",
  "type": "TEXT",
  "content": "你好，这是一条测试消息",
  "createdAt": "2023-01-04T12:30:00Z"
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "error": 400,
  "message": "Thread not found or access denied"
}
`})})]}),`
`,n.jsxs(t,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl --request POST \\
  --url https://api.weiyuai.cn/api/v1/messages \\
  --header 'Authorization: Bearer <token>' \\
  --header 'Content-Type: application/json' \\
  --data '{
    "threadId": "thread_123456",
    "type": "TEXT",
    "content": "你好，这是一条测试消息"
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/messages', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer <token>',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    threadId: 'thread_123456',
    type: 'TEXT',
    content: '你好，这是一条测试消息'
  })
});

const data = await response.json();
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

response = requests.post(
    'https://api.weiyuai.cn/api/v1/messages',
    headers={
        'Authorization': 'Bearer <token>',
        'Content-Type': 'application/json'
    },
    json={
        'threadId': 'thread_123456',
        'type': 'TEXT',
        'content': '你好，这是一条测试消息'
    }
)
`})})]})]})}function o(s={}){const{wrapper:e}={...c(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(h,{...s})}):h(s)}function d(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{o as default,a as frontmatter};
