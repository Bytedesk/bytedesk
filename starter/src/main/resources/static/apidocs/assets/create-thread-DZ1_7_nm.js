import{u as c,j as n}from"./index-CJncBc6B.js";const l={title:"POST 访客发起会话",openapi:"POST /api/v1/visitor/thread"};function d(i){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...c(),...i.components},{ApiPlayground:r,RequestExample:o,ResponseExample:t}=e;return r||s("ApiPlayground"),o||s("RequestExample"),t||s("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"访客发起客服会话，系统将根据路由规则分配给合适的客服。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"POST",endpoint:"/api/v1/visitor/thread"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于访客发起客服咨询。系统会根据技能组、客服状态、路由规则等自动分配给最合适的客服。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "workgroupUid": "wg_123456",
  "agentUid": "agent_123456",
  "topic": "产品咨询",
  "extra": {
    "source": "web",
    "page": "/contact"
  }
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"请求字段",children:"请求字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"workgroupUid"})," (可选): 技能组 UID，指定将会话分配到某个技能组"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"agentUid"})," (可选): 客服 UID，指定将会话分配给某个客服"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"topic"})," (可选): 会话主题或咨询类型"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"extra"})," (可选): 扩展字段",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"source"}),": 来源渠道"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"page"}),": 来源页面"]}),`
`,n.jsx(e.li,{children:"其他自定义信息"}),`
`]}),`
`]}),`
`]}),`
`,n.jsx(e.h3,{id:"会话对象字段",children:"会话对象字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 会话唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"visitorUid"}),": 访客唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"agentUid"}),": 分配的客服 UID（可能为空，表示等待分配）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"workgroupUid"}),": 技能组 UID"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"}),": 会话状态",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"QUEUING"}),": 排队中"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"ASSIGNED"}),": 已分配"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"CLOSED"}),": 已关闭"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"topic"}),": 会话主题"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 创建时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"访客发起在线咨询"}),`
`,n.jsx(e.li,{children:"指定技能组咨询"}),`
`,n.jsx(e.li,{children:"指定客服咨询"}),`
`,n.jsx(e.li,{children:"多渠道接入"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"如果指定了 agentUid，会尝试直接分配给该客服"}),`
`,n.jsx(e.li,{children:"如果只指定了 workgroupUid，会在该技能组的在线客服中分配"}),`
`,n.jsx(e.li,{children:"如果都不指定，会根据组织的路由规则分配"}),`
`,n.jsx(e.li,{children:"如果没有可用客服，会话会进入排队状态"}),`
`]}),`
`,n.jsxs(t,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "thread_123456",
    "visitorUid": "visitor_123456",
    "agentUid": "agent_123456",
    "workgroupUid": "wg_123456",
    "status": "ASSIGNED",
    "topic": "产品咨询",
    "extra": {
      "source": "web",
      "page": "/contact"
    },
    "createdAt": "2025-01-07T10:30:00Z"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "thread_123456",
    "visitorUid": "visitor_123456",
    "agentUid": null,
    "workgroupUid": "wg_123456",
    "status": "QUEUING",
    "topic": "产品咨询",
    "queuePosition": 1,
    "createdAt": "2025-01-07T10:30:00Z"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Workgroup not found",
  "data": null
}
`})})]}),`
`,n.jsxs(o,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`# 发起会话，指定技能组
curl -X POST "https://api.weiyuai.cn/api/v1/visitor/thread" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "workgroupUid": "wg_123456",
    "topic": "产品咨询",
    "extra": {
      "source": "web",
      "page": "/contact"
    }
  }'

# 发起会话，指定客服
curl -X POST "https://api.weiyuai.cn/api/v1/visitor/thread" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "agentUid": "agent_123456",
    "topic": "售后咨询"
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`// 访客发起会话
async function createThread(options) {
  const response = await fetch('https://api.weiyuai.cn/api/v1/visitor/thread', {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer your_access_token_here',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      workgroupUid: options.workgroupUid,
      agentUid: options.agentUid,
      topic: options.topic || '咨询',
      extra: {
        source: 'web',
        page: window.location.pathname
      }
    })
  });

  const result = await response.json();

  if (result.code === 200) {
    const thread = result.data;
    if (thread.status === 'QUEUING') {
      console.log(\`会话已创建，正在排队，当前位置: \${thread.queuePosition}\`);
    } else {
      console.log('会话已分配给客服');
    }
  }

  return result;
}

// 使用示例
createThread({ workgroupUid: 'wg_123456', topic: '产品咨询' });
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

def create_thread(workgroup_uid=None, agent_uid=None, topic="咨询"):
    url = "https://api.weiyuai.cn/api/v1/visitor/thread"
    headers = {
      "Authorization": "Bearer your_access_token_here",
      "Content-Type": "application/json"
    }

    data = {"topic": topic}
    if workgroup_uid:
        data["workgroupUid"] = workgroup_uid
    if agent_uid:
        data["agentUid"] = agent_uid

    response = requests.post(url, headers=headers, json=data)
    result = response.json()

    if result["code"] == 200:
        thread = result["data"]
        if thread["status"] == "QUEUING":
            print(f"会话已创建，正在排队，当前位置: {thread.get('queuePosition', 0)}")
        else:
            print("会话已分配给客服")

    return result

# 使用示例
create_thread(workgroup_uid="wg_123456", topic="产品咨询")
`})})]})]})}function h(i={}){const{wrapper:e}={...c(),...i.components};return e?n.jsx(e,{...i,children:n.jsx(d,{...i})}):d(i)}function s(i,e){throw new Error("Expected component `"+i+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,l as frontmatter};
