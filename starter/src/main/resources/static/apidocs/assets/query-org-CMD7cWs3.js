import{u as d,j as n}from"./index-CJncBc6B.js";const h={title:"GET 查询组织工单",openapi:"GET /ticket/query/org"};function l(i){const e={code:"code",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...d(),...i.components},{ApiPlayground:r,RequestExample:c,ResponseExample:t}=e;return r||s("ApiPlayground"),c||s("RequestExample"),t||s("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"查询指定组织下的所有工单，用于组织管理员或客服人员查看和管理工单。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"GET",endpoint:"/tickets/v1/query/org"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于查询指定组织下的所有工单列表，通常用于管理后台或客服系统中的工单管理功能。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,n.jsx(e.h2,{id:"查询参数",children:"查询参数"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"orgUid"})," (必需): 组织的唯一标识符"]}),`
`]}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"id"}),": 工单唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"title"}),": 工单标题"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"description"}),": 工单详细描述"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"}),": 工单当前状态"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"priority"}),": 工单优先级"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 工单创建时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"工单状态",children:"工单状态"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"OPEN"}),": 已开启，等待处理"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"IN_PROGRESS"}),": 正在处理中"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"RESOLVED"}),": 已解决"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"CLOSED"}),": 已关闭"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"组织管理员查看所有工单"}),`
`,n.jsx(e.li,{children:"客服人员工单分配和管理"}),`
`,n.jsx(e.li,{children:"工单统计和分析"}),`
`,n.jsx(e.li,{children:"组织级别的服务监控"}),`
`,n.jsx(e.li,{children:"客户服务质量评估"}),`
`]}),`
`,n.jsx(e.h2,{id:"权限要求",children:"权限要求"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"需要组织管理员权限或客服权限"}),`
`,n.jsx(e.li,{children:"只能查询有权限访问的组织工单"}),`
`,n.jsx(e.li,{children:"可能需要特定的角色授权"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"返回的是工单数组，不包含分页"}),`
`,n.jsx(e.li,{children:"如果组织下工单较多，建议前端实现分页显示"}),`
`,n.jsx(e.li,{children:"工单信息可能包含敏感数据，需要权限控制"}),`
`,n.jsx(e.li,{children:"建议配合筛选和排序功能使用"}),`
`]}),`
`,n.jsxs(t,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`[
  {
    "id": "ticket_567890",
    "title": "登录问题咨询",
    "description": "用户无法正常登录系统，提示密码错误",
    "status": "OPEN",
    "priority": "MEDIUM",
    "createdAt": "2023-01-04T14:00:00Z"
  },
  {
    "id": "ticket_567891",
    "title": "功能建议",
    "description": "希望能增加数据导出功能",
    "status": "IN_PROGRESS",
    "priority": "LOW",
    "createdAt": "2023-01-03T10:30:00Z"
  },
  {
    "id": "ticket_567892",
    "title": "系统故障报告",
    "description": "系统在高峰期响应缓慢",
    "status": "RESOLVED",
    "priority": "HIGH",
    "createdAt": "2023-01-02T16:45:00Z"
  }
]
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "error": 400,
  "message": "Organization not found"
}
`})})]}),`
`,n.jsxs(c,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl -X GET "https://api.weiyuai.cn/api/v1/ticket/query/org?orgUid=org_123456" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/ticket/query/org?orgUid=org_123456', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Accept': 'application/json'
  }
});

const tickets = await response.json();
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/ticket/query/org"
headers = {
    "Authorization": "Bearer your_access_token_here",
    "Accept": "application/json"
}
params = {
    "orgUid": "org_123456"
}

response = requests.get(url, headers=headers, params=params)
tickets = response.json()
`})})]})]})}function a(i={}){const{wrapper:e}={...d(),...i.components};return e?n.jsx(e,{...i,children:n.jsx(l,{...i})}):l(i)}function s(i,e){throw new Error("Expected component `"+i+"` to be defined: you likely forgot to import, pass, or provide it.")}export{a as default,h as frontmatter};
