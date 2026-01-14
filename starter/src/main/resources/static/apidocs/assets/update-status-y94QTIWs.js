import{u as d,j as n}from"./index-CJncBc6B.js";const o={title:"POST 更新客服状态",openapi:"POST /api/v1/agent/update/status"};function c(s){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...d(),...s.components},{ApiPlayground:i,RequestExample:a,ResponseExample:r}=e;return i||t("ApiPlayground"),a||t("RequestExample"),r||t("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"客服更新自己的在线状态，如在线、忙碌、离开等。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(i,{method:"POST",endpoint:"/api/v1/agent/update/status"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于客服修改自己的工作状态。状态变化会实时同步给访客，影响会话分配。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "status": "ONLINE"
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"请求字段",children:"请求字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"})," (必需): 客服状态",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"ONLINE"}),": 在线（可接受新会话）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"OFFLINE"}),": 离线（不可接受新会话）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"BUSY"}),": 忙碌（暂时不可接受新会话）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"AWAY"}),": 离开（暂时离开）"]}),`
`]}),`
`]}),`
`]}),`
`,n.jsx(e.h3,{id:"响应字段",children:"响应字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 客服唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"}),": 更新后的状态"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"updatedAt"}),": 更新时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"客服上线/下线"}),`
`,n.jsx(e.li,{children:"设置忙碌状态"}),`
`,n.jsx(e.li,{children:"临时离开"}),`
`,n.jsx(e.li,{children:"休息状态"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"设置为 OFFLINE 后不会接受新的会话分配"}),`
`,n.jsx(e.li,{children:"已有会话不会受状态变化影响"}),`
`,n.jsx(e.li,{children:"状态变化会通过 WebSocket 推送给在线访客"}),`
`]}),`
`,n.jsxs(r,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "agent_123456",
    "status": "ONLINE",
    "updatedAt": "2025-01-07T10:30:00Z"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Invalid status value",
  "data": null
}
`})})]}),`
`,n.jsxs(a,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`# 设置为在线
curl -X POST "https://api.weiyuai.cn/api/v1/agent/update/status" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "status": "ONLINE"
  }'

# 设置为忙碌
curl -X POST "https://api.weiyuai.cn/api/v1/agent/update/status" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "status": "BUSY"
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`// 设置客服状态
async function setAgentStatus(status) {
  const response = await fetch('https://api.weiyuai.cn/api/v1/agent/update/status', {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer your_access_token_here',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ status })
  });

  const result = await response.json();
  return result;
}

// 使用示例
setAgentStatus('ONLINE');
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

def set_agent_status(status):
    url = "https://api.weiyuai.cn/api/v1/agent/update/status"
    headers = {
      "Authorization": "Bearer your_access_token_here",
      "Content-Type": "application/json"
    }
    data = {"status": status}

    response = requests.post(url, headers=headers, json=data)
    return response.json()

# 使用示例
result = set_agent_status("ONLINE")
`})})]})]})}function h(s={}){const{wrapper:e}={...d(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(c,{...s})}):c(s)}function t(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,o as frontmatter};
