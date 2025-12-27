import{u as t,j as n}from"./index-Cfp_hilu.js";const h={title:"POST 创建工单",openapi:"POST /ticket/create"};function d(i){const e={code:"code",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...t(),...i.components},{ApiPlayground:r,RequestExample:c,ResponseExample:l}=e;return r||s("ApiPlayground"),c||s("RequestExample"),l||s("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"创建新的客服工单，用于用户提交问题、反馈或服务请求。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"POST",endpoint:"/tickets/v1/create"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于创建客服工单，用户可以通过此接口提交各类问题和服务请求，工单将被分配给相应的客服人员处理。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,n.jsx(e.h2,{id:"参数说明",children:"参数说明"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"title"})," (必需): 工单标题，简要描述问题"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"description"})," (必需): 工单详细描述"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"priority"})," (可选): 优先级",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"LOW"}),": 低优先级"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"MEDIUM"}),": 中优先级（默认）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"HIGH"}),": 高优先级"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"URGENT"}),": 紧急"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"category"}),' (可选): 工单分类，如"技术支持"、"账务问题"等']}),`
`]}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"id"}),": 工单唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"title"}),": 工单标题"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"description"}),": 工单详细描述"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"}),": 工单状态（新建时默认为 OPEN）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"priority"}),": 优先级"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 工单创建时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"工单状态说明",children:"工单状态说明"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"OPEN"}),": 已开启，等待处理"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"IN_PROGRESS"}),": 处理中"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"RESOLVED"}),": 已解决"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"CLOSED"}),": 已关闭"]}),`
`]}),`
`,n.jsx(e.h2,{id:"优先级说明",children:"优先级说明"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"LOW"}),": 一般问题，72小时内响应"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"MEDIUM"}),": 常规问题，24小时内响应"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"HIGH"}),": 重要问题，8小时内响应"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"URGENT"}),": 紧急问题，2小时内响应"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"用户反馈产品问题"}),`
`,n.jsx(e.li,{children:"技术支持请求"}),`
`,n.jsx(e.li,{children:"账务相关咨询"}),`
`,n.jsx(e.li,{children:"功能建议提交"}),`
`,n.jsx(e.li,{children:"投诉和建议"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"工单创建后会自动分配工单号"}),`
`,n.jsx(e.li,{children:"系统会根据优先级安排处理顺序"}),`
`,n.jsx(e.li,{children:"用户可以通过其他接口查询工单状态"}),`
`,n.jsx(e.li,{children:"建议提供详细的问题描述以便快速解决"}),`
`]}),`
`,n.jsxs(l,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "id": "ticket_567890",
  "title": "登录问题咨询",
  "description": "用户无法正常登录系统，提示密码错误",
  "status": "OPEN",
  "priority": "MEDIUM",
  "createdAt": "2023-01-04T14:00:00Z"
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "error": 400,
  "message": "Title and description are required"
}
`})})]}),`
`,n.jsxs(c,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl -X POST "https://api.weiyuai.cn/api/v1/ticket/create" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "title": "登录问题咨询",
    "description": "用户无法正常登录系统，提示密码错误",
    "priority": "MEDIUM",
    "category": "技术支持"
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/ticket/create', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    title: '登录问题咨询',
    description: '用户无法正常登录系统，提示密码错误',
    priority: 'MEDIUM',
    category: '技术支持'
  })
});

const ticket = await response.json();
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/ticket/create"
headers = {
    "Authorization": "Bearer your_access_token_here",
    "Content-Type": "application/json"
}
data = {
    "title": "登录问题咨询",
    "description": "用户无法正常登录系统，提示密码错误",
    "priority": "MEDIUM",
    "category": "技术支持"
}

response = requests.post(url, headers=headers, json=data)
ticket = response.json()
`})})]})]})}function j(i={}){const{wrapper:e}={...t(),...i.components};return e?n.jsx(e,{...i,children:n.jsx(d,{...i})}):d(i)}function s(i,e){throw new Error("Expected component `"+i+"` to be defined: you likely forgot to import, pass, or provide it.")}export{j as default,h as frontmatter};
