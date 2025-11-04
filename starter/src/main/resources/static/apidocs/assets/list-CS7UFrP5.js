import{u as l,j as n}from"./index-Cfp_hilu.js";const h={title:"GET 查询会话列表",openapi:"GET /threads"};function t(s){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...l(),...s.components},{ApiPlayground:r,RequestExample:c,ResponseExample:d}=e;return r||i("ApiPlayground"),c||i("RequestExample"),d||i("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"分页查询用户的会话列表，包括个人会话、群组会话和客服会话。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"GET",endpoint:"/api/v1/threads"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于获取当前用户相关的所有会话列表，支持分页查询和会话状态筛选。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,n.jsx(e.h2,{id:"查询参数",children:"查询参数"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"page"})," (可选): 页码，默认为 1"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"size"})," (可选): 每页数量，默认为 10"]}),`
`]}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"threads"}),": 会话列表数组"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"total"}),": 会话总数"]}),`
`]}),`
`,n.jsx(e.h3,{id:"会话对象字段",children:"会话对象字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"id"}),": 会话唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"type"}),": 会话类型",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"PERSONAL"}),": 个人会话"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"GROUP"}),": 群组会话"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"SUPPORT"}),": 客服会话"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"}),": 会话状态",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"ACTIVE"}),": 活跃状态"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"CLOSED"}),": 已关闭"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"ARCHIVED"}),": 已归档"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"title"}),": 会话标题"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"description"}),": 会话描述"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 会话创建时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"聊天应用会话列表"}),`
`,n.jsx(e.li,{children:"客服系统会话管理"}),`
`,n.jsx(e.li,{children:"群组管理界面"}),`
`,n.jsx(e.li,{children:"消息中心展示"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"只返回当前用户相关的会话"}),`
`,n.jsx(e.li,{children:"支持分页以提高性能"}),`
`,n.jsx(e.li,{children:"不同类型会话可能有不同的权限规则"}),`
`]}),`
`,n.jsx(d,{children:n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "threads": [
    {
      "id": "thread_123456",
      "type": "PERSONAL",
      "status": "ACTIVE",
      "title": "与用户A的对话",
      "description": "个人聊天会话",
      "createdAt": "2023-01-01T12:00:00Z"
    },
    {
      "id": "thread_123457",
      "type": "GROUP",
      "status": "ACTIVE",
      "title": "项目讨论组",
      "description": "项目相关讨论",
      "createdAt": "2023-01-02T12:00:00Z"
    },
    {
      "id": "thread_123458",
      "type": "SUPPORT",
      "status": "CLOSED",
      "title": "技术支持",
      "description": "用户咨询技术问题",
      "createdAt": "2023-01-03T12:00:00Z"
    }
  ],
  "total": 25
}
`})})}),`
`,n.jsxs(c,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl -X GET "https://api.weiyuai.cn/api/v1/threads?page=1&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/threads?page=1&size=10', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Accept': 'application/json'
  }
});

const data = await response.json();
const threads = data.threads;
const total = data.total;
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/threads"
headers = {
    "Authorization": "Bearer your_access_token_here",
    "Accept": "application/json"
}
params = {
    "page": 1,
    "size": 10
}

response = requests.get(url, headers=headers, params=params)
data = response.json()
`})})]})]})}function o(s={}){const{wrapper:e}={...l(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(t,{...s})}):t(s)}function i(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{o as default,h as frontmatter};
