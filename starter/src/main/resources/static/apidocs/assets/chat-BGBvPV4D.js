import{u as c,j as n}from"./index-CJncBc6B.js";const l={title:"POST AI机器人对话",openapi:"POST /api/v1/robot/chat"};function d(t){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...c(),...t.components},{ApiPlayground:o,RequestExample:r,ResponseExample:i}=e;return o||s("ApiPlayground"),r||s("RequestExample"),i||s("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"与 AI 机器人进行对话交互。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(o,{method:"POST",endpoint:"/api/v1/robot/chat"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于与 AI 机器人进行对话。支持多轮对话，会自动维护对话上下文。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "robotUid": "robot_123456",
  "threadUid": "thread_123456",
  "content": "你好，请问如何使用这个产品？",
  "stream": false
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"请求字段",children:"请求字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"robotUid"})," (必需): 机器人唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"threadUid"})," (可选): 会话唯一标识符，首次对话可不传，后续对话需要传入"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"content"})," (必需): 用户消息内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"stream"})," (可选): 是否使用流式响应，默认 false"]}),`
`]}),`
`,n.jsx(e.h3,{id:"响应字段非流式",children:"响应字段（非流式）"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"threadUid"}),": 会话唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"message"}),": AI 回复消息对象",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 消息唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"content"}),": 消息内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 创建时间"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"usage"}),": Token 使用情况",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"promptTokens"}),": 提示 token 数"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"completionTokens"}),": 完成 token 数"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"totalTokens"}),": 总 token 数"]}),`
`]}),`
`]}),`
`]}),`
`,n.jsx(e.h3,{id:"响应字段流式",children:"响应字段（流式）"}),`
`,n.jsx(e.p,{children:"返回 SSE (Server-Sent Events) 流，每个事件包含消息片段。"}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"智能客服对话"}),`
`,n.jsx(e.li,{children:"AI 问答"}),`
`,n.jsx(e.li,{children:"多轮对话"}),`
`,n.jsx(e.li,{children:"流式对话体验"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"首次对话可不传 threadUid，系统会自动创建"}),`
`,n.jsx(e.li,{children:"后续对话需要传入 threadUid 以保持上下文"}),`
`,n.jsx(e.li,{children:"流式响应适合长文本回复场景"}),`
`,n.jsx(e.li,{children:"会话有超时机制，超时后需要重新开始"}),`
`]}),`
`,n.jsxs(i,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "threadUid": "thread_123456",
    "message": {
      "uid": "msg_123456",
      "content": "您好！欢迎使用我们的产品。以下是使用指南：...",
      "createdAt": "2025-01-07T10:30:00Z"
    },
    "usage": {
      "promptTokens": 150,
      "completionTokens": 200,
      "totalTokens": 350
    }
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Robot not found",
  "data": null
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Robot is not enabled",
  "data": null
}
`})})]}),`
`,n.jsxs(r,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`# 非流式对话
curl -X POST "https://api.weiyuai.cn/api/v1/robot/chat" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "robotUid": "robot_123456",
    "content": "你好，请问如何使用这个产品？",
    "stream": false
  }'

# 流式对话
curl -X POST "https://api.weiyuai.cn/api/v1/robot/chat" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "robotUid": "robot_123456",
    "threadUid": "thread_123456",
    "content": "还有其他问题",
    "stream": true
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`// 非流式对话
async function chatWithRobot(robotUid, content, threadUid = null) {
  const response = await fetch('https://api.weiyuai.cn/api/v1/robot/chat', {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer your_access_token_here',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      robotUid,
      content,
      threadUid,
      stream: false
    })
  });

  const result = await response.json();
  return result.data;
}

// 使用示例
const result = await chatWithRobot('robot_123456', '你好');
console.log(result.message.content);
console.log('会话ID:', result.threadUid);

// 继续对话
const followUp = await chatWithRobot('robot_123456', '还有其他问题吗？', result.threadUid);
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

def chat_with_robot(robot_uid, content, thread_uid=None):
    url = "https://api.weiyuai.cn/api/v1/robot/chat"
    headers = {
      "Authorization": "Bearer your_access_token_here",
      "Content-Type": "application/json"
    }

    data = {
      "robotUid": robot_uid,
      "content": content,
      "stream": False
    }

    if thread_uid:
        data["threadUid"] = thread_uid

    response = requests.post(url, headers=headers, json=data)
    result = response.json()
    return result["data"]

# 使用示例
result = chat_with_robot("robot_123456", "你好")
print(result["message"]["content"])
print("会话ID:", result["threadUid"])

# 继续对话
follow_up = chat_with_robot("robot_123456", "还有其他问题吗？", result["threadUid"])
`})})]})]})}function h(t={}){const{wrapper:e}={...c(),...t.components};return e?n.jsx(e,{...t,children:n.jsx(d,{...t})}):d(t)}function s(t,e){throw new Error("Expected component `"+t+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,l as frontmatter};
