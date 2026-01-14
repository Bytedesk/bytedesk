import{u as r,j as n}from"./index-CJncBc6B.js";const o={title:"POST 更新工单状态",openapi:"POST /api/v1/ticket/update/status"};function d(t){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...r(),...t.components},{ApiPlayground:i,RequestExample:c,ResponseExample:a}=e;return i||s("ApiPlayground"),c||s("RequestExample"),a||s("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"更新工单的处理状态。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(i,{method:"POST",endpoint:"/api/v1/ticket/update/status"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于更新工单的状态，如将工单从待处理改为处理中，或标记为已解决、已关闭等。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "uid": "ticket_123456",
  "status": "IN_PROGRESS",
  "comment": "正在处理中，预计今天完成"
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"请求字段",children:"请求字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"})," (必需): 工单唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"})," (必需): 目标状态",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"OPEN"}),": 待处理"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"IN_PROGRESS"}),": 处理中"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"RESOLVED"}),": 已解决"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"CLOSED"}),": 已关闭"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"comment"})," (可选): 状态变更备注"]}),`
`]}),`
`,n.jsx(e.h3,{id:"响应字段",children:"响应字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 工单唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"}),": 更新后的状态"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"comment"}),": 备注内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"updatedBy"}),": 更新人名称"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"updatedAt"}),": 更新时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"开始处理工单"}),`
`,n.jsx(e.li,{children:"标记工单为已解决"}),`
`,n.jsx(e.li,{children:"关闭已完成的工单"}),`
`,n.jsx(e.li,{children:"重新打开已关闭的工单"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"只有工单处理人或管理员可以更新状态"}),`
`,n.jsx(e.li,{children:"关闭工单通常需要先标记为已解决"}),`
`,n.jsx(e.li,{children:"状态变更会记录在工单历史中"}),`
`,n.jsx(e.li,{children:"系统会通知相关人员状态变更"}),`
`]}),`
`,n.jsxs(a,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "ticket_123456",
    "status": "IN_PROGRESS",
    "comment": "正在处理中，预计今天完成",
    "updatedBy": "客服小王",
    "updatedAt": "2025-01-07T10:30:00Z"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Ticket not found",
  "data": null
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 403,
  "message": "No permission to update this ticket",
  "data": null
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Invalid status transition",
  "data": null
}
`})})]}),`
`,n.jsxs(c,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`# 开始处理工单
curl -X POST "https://api.weiyuai.cn/api/v1/ticket/update/status" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "uid": "ticket_123456",
    "status": "IN_PROGRESS",
    "comment": "正在处理中，预计今天完成"
  }'

# 标记为已解决
curl -X POST "https://api.weiyuai.cn/api/v1/ticket/update/status" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "uid": "ticket_123456",
    "status": "RESOLVED",
    "comment": "问题已解决，请用户确认"
  }'

# 关闭工单
curl -X POST "https://api.weiyuai.cn/api/v1/ticket/update/status" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "uid": "ticket_123456",
    "status": "CLOSED",
    "comment": "工单已关闭"
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`// 更新工单状态
async function updateTicketStatus(ticketUid, status, comment = '') {
  const response = await fetch('https://api.weiyuai.cn/api/v1/ticket/update/status', {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer your_access_token_here',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      uid: ticketUid,
      status,
      comment
    })
  });

  const result = await response.json();
  return result.data;
}

// 使用示例
await updateTicketStatus('ticket_123456', 'IN_PROGRESS', '正在处理中');
await updateTicketStatus('ticket_123456', 'RESOLVED', '问题已解决');
await updateTicketStatus('ticket_123456', 'CLOSED', '工单已关闭');
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

def update_ticket_status(ticket_uid, status, comment=""):
    url = "https://api.weiyuai.cn/api/v1/ticket/update/status"
    headers = {
      "Authorization": "Bearer your_access_token_here",
      "Content-Type": "application/json"
    }

    data = {
      "uid": ticket_uid,
      "status": status,
      "comment": comment
    }

    response = requests.post(url, headers=headers, json=data)
    result = response.json()
    return result["data"]

# 使用示例
update_ticket_status("ticket_123456", "IN_PROGRESS", "正在处理中")
update_ticket_status("ticket_123456", "RESOLVED", "问题已解决")
update_ticket_status("ticket_123456", "CLOSED", "工单已关闭")
`})})]})]})}function u(t={}){const{wrapper:e}={...r(),...t.components};return e?n.jsx(e,{...t,children:n.jsx(d,{...t})}):d(t)}function s(t,e){throw new Error("Expected component `"+t+"` to be defined: you likely forgot to import, pass, or provide it.")}export{u as default,o as frontmatter};
