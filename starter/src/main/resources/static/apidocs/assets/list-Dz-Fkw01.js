import{u as t,j as n}from"./index-CJncBc6B.js";const o={title:"GET 查询客服列表",openapi:"GET /api/v1/agent"};function l(s){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...t(),...s.components},{ApiPlayground:r,RequestExample:a,ResponseExample:c}=e;return r||i("ApiPlayground"),a||i("RequestExample"),c||i("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"查询组织下的客服列表，支持分页查询和状态筛选。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"GET",endpoint:"/api/v1/agent"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于获取当前组织下的所有客服信息，包括客服状态、技能组、接待数量等。支持按组织、用户等维度查询。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,n.jsx(e.h2,{id:"查询参数",children:"查询参数"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"orgUid"})," (可选): 组织唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"userUid"})," (可选): 用户唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"})," (可选): 客服状态（ONLINE, OFFLINE, BUSY, AWAY）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"page"})," (可选): 页码，默认为 0"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"size"})," (可选): 每页数量，默认为 10"]}),`
`]}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"客服对象字段",children:"客服对象字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 客服唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"userUid"}),": 用户唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"nickname"}),": 客服昵称"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"avatar"}),": 客服头像 URL"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"}),": 客服状态",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"ONLINE"}),": 在线"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"OFFLINE"}),": 离线"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"BUSY"}),": 忙碌"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"AWAY"}),": 离开"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"workgroupUid"}),": 所属技能组 UID"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"autoReply"}),": 自动回复内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"maxThreads"}),": 最大接待数"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"currentThreads"}),": 当前接待数"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"isAvailable"}),": 是否可用（接待未满且在线）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 创建时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"查看客服列表"}),`
`,n.jsx(e.li,{children:"客服状态监控"}),`
`,n.jsx(e.li,{children:"客服分配"}),`
`,n.jsx(e.li,{children:"工作量统计"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"需要相应的权限才能查看客服列表"}),`
`,n.jsx(e.li,{children:"只能查看当前组织及子组织的客服"}),`
`,n.jsx(e.li,{children:"客服状态会实时更新"}),`
`]}),`
`,n.jsx(c,{children:n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "uid": "agent_123456",
        "userUid": "user_123456",
        "nickname": "客服小王",
        "avatar": "https://api.weiyuai.cn/avatar/agent1.png",
        "status": "ONLINE",
        "workgroupUid": "wg_123456",
        "autoReply": "您好，我是客服小王，请问有什么可以帮您？",
        "maxThreads": 10,
        "currentThreads": 3,
        "isAvailable": true,
        "createdAt": "2025-01-07T09:00:00Z"
      },
      {
        "uid": "agent_123457",
        "userUid": "user_123457",
        "nickname": "客服小李",
        "avatar": "https://api.weiyuai.cn/avatar/agent2.png",
        "status": "BUSY",
        "workgroupUid": "wg_123456",
        "autoReply": "您好，我是客服小李，请稍等片刻",
        "maxThreads": 10,
        "currentThreads": 10,
        "isAvailable": false,
        "createdAt": "2025-01-07T09:00:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalPages": 1,
    "totalElements": 2,
    "last": true,
    "first": true
  }
}
`})})}),`
`,n.jsxs(a,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`# 查询所有在线客服
curl -X GET "https://api.weiyuai.cn/api/v1/agent?status=ONLINE&page=0&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"

# 查询指定技能组的客服
curl -X GET "https://api.weiyuai.cn/api/v1/agent?workgroupUid=wg_123456&page=0&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`// 查询在线客服
const response = await fetch('https://api.weiyuai.cn/api/v1/agent?status=ONLINE&page=0&size=10', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Accept': 'application/json'
  }
});

const result = await response.json();
const agents = result.data.content;
const availableAgents = agents.filter(agent => agent.isAvailable);
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/agent"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Accept": "application/json"
}
params = {
  "status": "ONLINE",
  "page": 0,
  "size": 10
}

response = requests.get(url, headers=headers, params=params)
result = response.json()
agents = result["data"]["content"]
available_agents = [a for a in agents if a["isAvailable"]]
`})})]})]})}function h(s={}){const{wrapper:e}={...t(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(l,{...s})}):l(s)}function i(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,o as frontmatter};
