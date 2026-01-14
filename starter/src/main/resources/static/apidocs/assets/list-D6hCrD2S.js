import{u as d,j as e}from"./index-CJncBc6B.js";const o={title:"GET 查询工单列表",openapi:"GET /api/v1/ticket"};function l(s){const n={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...d(),...s.components},{ApiPlayground:r,RequestExample:c,ResponseExample:t}=n;return r||i("ApiPlayground"),c||i("RequestExample"),t||i("ResponseExample"),e.jsxs(e.Fragment,{children:[e.jsx(n.h2,{id:"概述",children:"概述"}),`
`,e.jsx(n.p,{children:"分页查询工单列表，支持按状态、优先级、类型等筛选。"}),`
`,e.jsx("div",{style:{marginTop:24,marginBottom:24},children:e.jsx(r,{method:"GET",endpoint:"/api/v1/ticket"})}),`
`,e.jsx(n.h2,{id:"接口说明",children:"接口说明"}),`
`,e.jsx(n.p,{children:"此接口用于获取工单列表。支持按组织、用户、状态、优先级等多维度查询。"}),`
`,e.jsx(n.h2,{id:"请求头",children:"请求头"}),`
`,e.jsx(n.pre,{children:e.jsx(n.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,e.jsx(n.h2,{id:"查询参数",children:"查询参数"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"orgUid"})," (可选): 组织唯一标识符"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"userUid"})," (可选): 用户唯一标识符"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"status"})," (可选): 工单状态（OPEN, IN_PROGRESS, RESOLVED, CLOSED）"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"priority"})," (可选): 优先级（LOW, MEDIUM, HIGH, URGENT）"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"type"})," (可选): 工单类型"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"assigneeUid"})," (可选): 处理人 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"page"})," (可选): 页码，默认为 0"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"size"})," (可选): 每页数量，默认为 10"]}),`
`]}),`
`,e.jsx(n.h2,{id:"字段说明",children:"字段说明"}),`
`,e.jsx(n.h3,{id:"工单对象字段",children:"工单对象字段"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"uid"}),": 工单唯一标识符"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"title"}),": 工单标题"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"description"}),": 工单描述"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"status"}),": 工单状态",`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"OPEN"}),": 待处理"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"IN_PROGRESS"}),": 处理中"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"RESOLVED"}),": 已解决"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"CLOSED"}),": 已关闭"]}),`
`]}),`
`]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"priority"}),": 优先级",`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"LOW"}),": 低"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"MEDIUM"}),": 中"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"HIGH"}),": 高"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"URGENT"}),": 紧急"]}),`
`]}),`
`]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"type"}),": 工单类型"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"categoryUid"}),": 分类 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"categoryName"}),": 分类名称"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"creatorUid"}),": 创建人 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"creatorName"}),": 创建人名称"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"assigneeUid"}),": 处理人 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"assigneeName"}),": 处理人名称"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"threadUid"}),": 关联会话 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"resolvedAt"}),": 解决时间"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"closedAt"}),": 关闭时间"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"createdAt"}),": 创建时间"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"updatedAt"}),": 更新时间"]}),`
`]}),`
`,e.jsx(n.h2,{id:"使用场景",children:"使用场景"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"工单列表展示"}),`
`,e.jsx(n.li,{children:"我的工单查询"}),`
`,e.jsx(n.li,{children:"工单分配管理"}),`
`,e.jsx(n.li,{children:"工单统计分析"}),`
`]}),`
`,e.jsx(n.h2,{id:"注意事项",children:"注意事项"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"只能查询有权限的工单"}),`
`,e.jsx(n.li,{children:"管理员可以查看所有工单"}),`
`,e.jsx(n.li,{children:"普通用户只能查看自己创建或被分配的工单"}),`
`]}),`
`,e.jsx(t,{children:e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "uid": "ticket_123456",
        "title": "系统登录失败",
        "description": "用户反馈无法登录系统，提示密码错误",
        "status": "OPEN",
        "priority": "HIGH",
        "type": "技术支持",
        "categoryUid": "category_123456",
        "categoryName": "系统问题",
        "creatorUid": "user_123456",
        "creatorName": "张三",
        "assigneeUid": "agent_123456",
        "assigneeName": "客服小王",
        "threadUid": "thread_123456",
        "resolvedAt": null,
        "closedAt": null,
        "createdAt": "2025-01-07T09:00:00Z",
        "updatedAt": "2025-01-07T10:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalPages": 3,
    "totalElements": 25,
    "last": false,
    "first": true
  }
}
`})})}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`# 查询待处理的工单
curl -X GET "https://api.weiyuai.cn/api/v1/ticket?status=OPEN&page=0&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"

# 查询分配给我的工单
curl -X GET "https://api.weiyuai.cn/api/v1/ticket?assigneeUid=user_123456&page=0&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"

# 查询高优先级工单
curl -X GET "https://api.weiyuai.cn/api/v1/ticket?priority=HIGH,URGENT&page=0&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`// 查询工单列表
async function getTickets(filters = {}) {
  const params = new URLSearchParams({
    page: filters.page || 0,
    size: filters.size || 10
  });

  if (filters.status) params.append('status', filters.status);
  if (filters.priority) params.append('priority', filters.priority);
  if (filters.assigneeUid) params.append('assigneeUid', filters.assigneeUid);

  const response = await fetch(
    \`https://api.weiyuai.cn/api/v1/ticket?\${params}\`,
    {
      method: 'GET',
      headers: {
        'Authorization': 'Bearer your_access_token_here',
        'Accept': 'application/json'
      }
    }
  );

  const result = await response.json();
  return result.data;
}

// 使用示例
const tickets = await getTickets({ status: 'OPEN', priority: 'HIGH' });
console.log(\`找到 \${tickets.totalElements} 个工单\`);
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

def get_tickets(status=None, priority=None, assignee_uid=None, page=0):
    url = "https://api.weiyuai.cn/api/v1/ticket"
    headers = {
      "Authorization": "Bearer your_access_token_here",
      "Accept": "application/json"
    }

    params = {"page": page, "size": 10}
    if status:
        params["status"] = status
    if priority:
        params["priority"] = priority
    if assignee_uid:
        params["assigneeUid"] = assignee_uid

    response = requests.get(url, headers=headers, params=params)
    result = response.json()
    return result["data"]

# 使用示例
tickets = get_tickets(status="OPEN", priority="HIGH")
print(f"找到 {tickets['totalElements']} 个工单")
`})})]})]})}function h(s={}){const{wrapper:n}={...d(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(l,{...s})}):l(s)}function i(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,o as frontmatter};
