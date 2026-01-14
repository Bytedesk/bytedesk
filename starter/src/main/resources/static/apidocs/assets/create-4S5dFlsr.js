import{u as t,j as n}from"./index-CJncBc6B.js";const o={title:"POST 创建线程",openapi:"POST /threads/v1/create"};function c(i){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...t(),...i.components},{ApiPlayground:r,RequestExample:l,ResponseExample:d}=e;return r||s("ApiPlayground"),l||s("RequestExample"),d||s("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"创建一个新的会话线程，用于组织和管理用户与客服之间的对话。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"POST",endpoint:"/threads/v1/create"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"在开始与客服交流之前，需要先创建一个线程。每个线程都有唯一的标识符，用于后续的消息交互。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,n.jsx(e.h2,{id:"参数说明",children:"参数说明"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"type"})," (必需): 会话类型",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"PERSONAL"}),": 个人会话"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"GROUP"}),": 群组会话"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"SUPPORT"}),": 客服会话"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"title"})," (可选): 会话标题"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"description"})," (可选): 会话描述"]}),`
`]}),`
`,n.jsx(e.h2,{id:"创建规则",children:"创建规则"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"会话类型必须是有效的枚举值"}),`
`,n.jsx(e.li,{children:"创建者自动成为会话成员"}),`
`,n.jsx(e.li,{children:"新会话默认状态为 ACTIVE"}),`
`,n.jsx(e.li,{children:"系统会自动生成唯一的会话 ID"}),`
`]}),`
`,n.jsx(e.h2,{id:"会话类型说明",children:"会话类型说明"}),`
`,n.jsx(e.h3,{id:"personal个人会话",children:"PERSONAL（个人会话）"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"一对一的私人对话"}),`
`,n.jsx(e.li,{children:"只有两个参与者"}),`
`,n.jsx(e.li,{children:"用于私密沟通"}),`
`]}),`
`,n.jsx(e.h3,{id:"group群组会话",children:"GROUP（群组会话）"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"多人参与的群组对话"}),`
`,n.jsx(e.li,{children:"支持成员管理"}),`
`,n.jsx(e.li,{children:"适用于团队协作"}),`
`]}),`
`,n.jsx(e.h3,{id:"support客服会话",children:"SUPPORT（客服会话）"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"用户与客服的对话"}),`
`,n.jsx(e.li,{children:"具有工单性质"}),`
`,n.jsx(e.li,{children:"支持会话状态管理"}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"发起新的聊天对话"}),`
`,n.jsx(e.li,{children:"创建项目讨论组"}),`
`,n.jsx(e.li,{children:"开启客服咨询"}),`
`,n.jsx(e.li,{children:"建立临时沟通渠道"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"不同类型会话可能有不同的权限要求"}),`
`,n.jsx(e.li,{children:"群组会话可能需要邀请其他成员"}),`
`,n.jsx(e.li,{children:"客服会话可能会自动分配客服人员"}),`
`]}),`
`,n.jsxs(d,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "id": "thread_123459",
  "type": "PERSONAL",
  "status": "ACTIVE",
  "title": "新建个人会话",
  "description": "用户创建的个人会话",
  "createdAt": "2023-01-04T12:00:00Z"
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "error": 400,
  "message": "Invalid thread type"
}
`})})]}),`
`,n.jsxs(l,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl -X POST "https://api.weiyuai.cn/api/v1/threads" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "type": "PERSONAL",
    "title": "新建个人会话",
    "description": "用户创建的个人会话"
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/threads', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    type: 'PERSONAL',
    title: '新建个人会话',
    description: '用户创建的个人会话'
  })
});

const newThread = await response.json();
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/threads"
headers = {
    "Authorization": "Bearer your_access_token_here",
    "Content-Type": "application/json"
}
data = {
    "type": "PERSONAL",
    "title": "新建个人会话",
    "description": "用户创建的个人会话"
}

response = requests.post(url, headers=headers, json=data)
new_thread = response.json()
`})})]})]})}function a(i={}){const{wrapper:e}={...t(),...i.components};return e?n.jsx(e,{...i,children:n.jsx(c,{...i})}):c(i)}function s(i,e){throw new Error("Expected component `"+i+"` to be defined: you likely forgot to import, pass, or provide it.")}export{a as default,o as frontmatter};
