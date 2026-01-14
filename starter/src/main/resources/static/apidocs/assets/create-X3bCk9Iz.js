import{u as c,j as e}from"./index-CJncBc6B.js";const a={title:"POST 创建AI机器人",openapi:"POST /api/v1/robot"};function o(s){const n={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...c(),...s.components},{ApiPlayground:r,RequestExample:d,ResponseExample:l}=n;return r||i("ApiPlayground"),d||i("RequestExample"),l||i("ResponseExample"),e.jsxs(e.Fragment,{children:[e.jsx(n.h2,{id:"概述",children:"概述"}),`
`,e.jsx(n.p,{children:"创建一个新的 AI 智能客服机器人。"}),`
`,e.jsx("div",{style:{marginTop:24,marginBottom:24},children:e.jsx(r,{method:"POST",endpoint:"/api/v1/robot"})}),`
`,e.jsx(n.h2,{id:"接口说明",children:"接口说明"}),`
`,e.jsx(n.p,{children:"此接口用于创建 AI 智能客服机器人。可以配置机器人的名称、头像、使用的 LLM 模型、提示词等参数。"}),`
`,e.jsx(n.h2,{id:"请求头",children:"请求头"}),`
`,e.jsx(n.pre,{children:e.jsx(n.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,e.jsx(n.h2,{id:"请求体",children:"请求体"}),`
`,e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "name": "智能客服小助手",
  "avatar": "https://example.com/avatar.png",
  "description": "基于大语言模型的智能客服助手",
  "welcomeMessage": "您好，我是智能客服小助手，请问有什么可以帮您？",
  "llmProviderUid": "provider_123456",
  "modelUid": "model_123456",
  "systemPrompt": "你是一个专业的客服助手，请礼貌、准确地回答用户问题",
  "temperature": 0.7,
  "maxTokens": 2000,
  "isEnabled": true
}
`})}),`
`,e.jsx(n.h2,{id:"字段说明",children:"字段说明"}),`
`,e.jsx(n.h3,{id:"请求字段",children:"请求字段"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"name"})," (必需): 机器人名称"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"avatar"})," (可选): 机器人头像 URL"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"description"})," (可选): 机器人描述"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"welcomeMessage"})," (可选): 欢迎语"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"llmProviderUid"})," (必需): LLM 提供商 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"modelUid"})," (必需): 模型 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"systemPrompt"})," (可选): 系统提示词"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"temperature"})," (可选): 温度参数，0-2，默认 0.7"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"maxTokens"})," (可选): 最大 token 数，默认 2000"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"isEnabled"})," (可选): 是否启用，默认 true"]}),`
`]}),`
`,e.jsx(n.h3,{id:"机器人对象字段",children:"机器人对象字段"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"uid"}),": 机器人唯一标识符"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"name"}),": 机器人名称"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"avatar"}),": 机器人头像 URL"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"description"}),": 机器人描述"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"welcomeMessage"}),": 欢迎语"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"llmProviderUid"}),": LLM 提供商 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"modelUid"}),": 模型 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"systemPrompt"}),": 系统提示词"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"temperature"}),": 温度参数"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"maxTokens"}),": 最大 token 数"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"isEnabled"}),": 是否启用"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"threadCount"}),": 会话数量"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"messageCount"}),": 消息数量"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"createdAt"}),": 创建时间"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"updatedAt"}),": 更新时间"]}),`
`]}),`
`,e.jsx(n.h2,{id:"使用场景",children:"使用场景"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"创建智能客服机器人"}),`
`,e.jsx(n.li,{children:"配置 AI 助手"}),`
`,e.jsx(n.li,{children:"多机器人管理"}),`
`,e.jsx(n.li,{children:"A/B 测试不同配置"}),`
`]}),`
`,e.jsx(n.h2,{id:"注意事项",children:"注意事项"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"需要先配置 LLM 提供商和模型"}),`
`,e.jsx(n.li,{children:"系统提示词会影响机器人的回复风格"}),`
`,e.jsx(n.li,{children:"温度参数控制回复的随机性"}),`
`,e.jsx(n.li,{children:"可以为不同场景配置多个机器人"}),`
`]}),`
`,e.jsxs(l,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "robot_123456",
    "name": "智能客服小助手",
    "avatar": "https://api.weiyuai.cn/avatar/robot.png",
    "description": "基于大语言模型的智能客服助手",
    "welcomeMessage": "您好，我是智能客服小助手，请问有什么可以帮您？",
    "llmProviderUid": "provider_123456",
    "modelUid": "model_123456",
    "systemPrompt": "你是一个专业的客服助手，请礼貌、准确地回答用户问题",
    "temperature": 0.7,
    "maxTokens": 2000,
    "isEnabled": true,
    "threadCount": 0,
    "messageCount": 0,
    "createdAt": "2025-01-07T10:30:00Z",
    "updatedAt": "2025-01-07T10:30:00Z"
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 400,
  "message": "LLM provider or model not found",
  "data": null
}
`})})]}),`
`,e.jsxs(d,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl -X POST "https://api.weiyuai.cn/api/v1/robot" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "name": "智能客服小助手",
    "description": "基于大语言模型的智能客服助手",
    "welcomeMessage": "您好，我是智能客服小助手",
    "llmProviderUid": "provider_123456",
    "modelUid": "model_123456",
    "systemPrompt": "你是一个专业的客服助手",
    "temperature": 0.7,
    "maxTokens": 2000,
    "isEnabled": true
  }'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/robot', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: '智能客服小助手',
    description: '基于大语言模型的智能客服助手',
    welcomeMessage: '您好，我是智能客服小助手',
    llmProviderUid: 'provider_123456',
    modelUid: 'model_123456',
    systemPrompt: '你是一个专业的客服助手',
    temperature: 0.7,
    maxTokens: 2000,
    isEnabled: true
  })
});

const result = await response.json();
const robot = result.data;
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/robot"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Content-Type": "application/json"
}
data = {
  "name": "智能客服小助手",
  "description": "基于大语言模型的智能客服助手",
  "welcomeMessage": "您好，我是智能客服小助手",
  "llmProviderUid": "provider_123456",
  "modelUid": "model_123456",
  "systemPrompt": "你是一个专业的客服助手",
  "temperature": 0.7,
  "maxTokens": 2000,
  "isEnabled": True
}

response = requests.post(url, headers=headers, json=data)
result = response.json()
robot = result["data"]
`})})]})]})}function h(s={}){const{wrapper:n}={...c(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(o,{...s})}):o(s)}function i(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,a as frontmatter};
