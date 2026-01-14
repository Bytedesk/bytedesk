import{u as o,j as n}from"./index-CJncBc6B.js";const t={title:"POST 创建群组",openapi:"POST /api/v1/group"};function l(s){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...o(),...s.components},{ApiPlayground:r,RequestExample:c,ResponseExample:d}=e;return r||i("ApiPlayground"),c||i("RequestExample"),d||i("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"创建一个新的群组，支持设置群组名称、描述、头像等信息。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"POST",endpoint:"/api/v1/group"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于创建新的群组。创建者自动成为群组管理员。创建后可以邀请其他成员加入。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "name": "群组名称",
  "description": "群组描述",
  "avatar": "https://example.com/avatar.png",
  "type": "PUBLIC",
  "maxMembers": 500,
  "needApproval": false
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"请求字段",children:"请求字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"name"})," (必需): 群组名称，长度 1-50 字符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"description"})," (可选): 群组描述，长度 0-200 字符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"avatar"})," (可选): 群组头像 URL"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"type"})," (可选): 群组类型",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"PUBLIC"}),": 公开群组"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"PRIVATE"}),": 私有群组"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"ORG"}),": 组织群组"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"maxMembers"})," (可选): 最大成员数，默认 500"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"needApproval"})," (可选): 是否需要审批加入，默认 false"]}),`
`]}),`
`,n.jsx(e.h3,{id:"群组对象字段",children:"群组对象字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 群组唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"name"}),": 群组名称"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"description"}),": 群组描述"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"avatar"}),": 群组头像 URL"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"type"}),": 群组类型"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"maxMembers"}),": 最大成员数"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"needApproval"}),": 是否需要审批"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"memberCount"}),": 当前成员数"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"ownerUid"}),": 群主用户 UID"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 创建时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"创建项目讨论组"}),`
`,n.jsx(e.li,{children:"创建部门群组"}),`
`,n.jsx(e.li,{children:"创建兴趣群组"}),`
`,n.jsx(e.li,{children:"创建临时讨论组"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"群组名称不能为空"}),`
`,n.jsx(e.li,{children:"群组类型创建后不能修改"}),`
`,n.jsx(e.li,{children:"创建者自动成为群主"}),`
`,n.jsx(e.li,{children:"群组创建后自动生成对应的会话"}),`
`]}),`
`,n.jsxs(d,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "group_123456",
    "name": "项目讨论组",
    "description": "讨论项目相关事宜",
    "avatar": "https://api.weiyuai.cn/avatar/default.png",
    "type": "PRIVATE",
    "maxMembers": 500,
    "needApproval": true,
    "memberCount": 1,
    "ownerUid": "user_123456",
    "createdAt": "2025-01-07T10:30:00Z"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Group name cannot be empty",
  "data": null
}
`})})]}),`
`,n.jsxs(c,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl -X POST "https://api.weiyuai.cn/api/v1/group" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "name": "项目讨论组",
    "description": "讨论项目相关事宜",
    "type": "PRIVATE",
    "needApproval": true
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/group', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: '项目讨论组',
    description: '讨论项目相关事宜',
    type: 'PRIVATE',
    needApproval: true
  })
});

const result = await response.json();
const group = result.data;
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/group"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Content-Type": "application/json"
}
data = {
  "name": "项目讨论组",
  "description": "讨论项目相关事宜",
  "type": "PRIVATE",
  "needApproval": True
}

response = requests.post(url, headers=headers, json=data)
result = response.json()
group = result["data"]
`})})]})]})}function h(s={}){const{wrapper:e}={...o(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(l,{...s})}):l(s)}function i(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,t as frontmatter};
