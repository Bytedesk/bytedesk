import{u as c,j as n}from"./index-CJncBc6B.js";const o={title:"POST 客服接受会话",openapi:"POST /api/v1/agent/accept"};function t(s){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...c(),...s.components},{ApiPlayground:r,RequestExample:a,ResponseExample:d}=e;return r||i("ApiPlayground"),a||i("RequestExample"),d||i("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"客服接受访客的会话请求，开始对话。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"POST",endpoint:"/api/v1/agent/accept"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于客服主动接受待分配的会话。当有新访客进入时，会话会先进入队列，客服可以调用此接口接取会话。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "threadUid": "thread_123456"
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"请求字段",children:"请求字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"threadUid"})," (必需): 会话唯一标识符"]}),`
`]}),`
`,n.jsx(e.h3,{id:"会话对象字段",children:"会话对象字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 会话唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"visitorUid"}),": 访客唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"agentUid"}),": 客服唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"workgroupUid"}),": 技能组唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"}),": 会话状态（QUEUING, ASSIGNED, CLOSED）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 创建时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"客服主动接取排队中的会话"}),`
`,n.jsx(e.li,{children:"会话分配"}),`
`,n.jsx(e.li,{children:"转接会话"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"客服必须在线才能接受会话"}),`
`,n.jsx(e.li,{children:"当前接待数未达到上限"}),`
`,n.jsx(e.li,{children:"会话已被其他客服接受时会失败"}),`
`,n.jsx(e.li,{children:"接受后会通过 WebSocket 通知访客"}),`
`]}),`
`,n.jsxs(d,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "thread_123456",
    "visitorUid": "visitor_123456",
    "agentUid": "agent_123456",
    "workgroupUid": "wg_123456",
    "status": "ASSIGNED",
    "createdAt": "2025-01-07T10:30:00Z"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Thread not found",
  "data": null
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Thread already assigned to another agent",
  "data": null
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 403,
  "message": "Agent has reached maximum threads limit",
  "data": null
}
`})})]}),`
`,n.jsxs(a,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl -X POST "https://api.weiyuai.cn/api/v1/agent/accept" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "threadUid": "thread_123456"
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/agent/accept', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    threadUid: 'thread_123456'
  })
});

const result = await response.json();
if (result.code === 200) {
  console.log('成功接受会话', result.data);
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/agent/accept"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Content-Type": "application/json"
}
data = {
  "threadUid": "thread_123456"
}

response = requests.post(url, headers=headers, json=data)
result = response.json()
if result["code"] == 200:
  print("成功接受会话", result["data"])
`})})]})]})}function h(s={}){const{wrapper:e}={...c(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(t,{...s})}):t(s)}function i(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,o as frontmatter};
