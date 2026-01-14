import{u as o,j as n}from"./index-CJncBc6B.js";const a={title:"POST 创建关键词自动回复",openapi:"POST /api/v1/autoreply/keyword"};function c(s){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...o(),...s.components},{ApiPlayground:r,RequestExample:d,ResponseExample:l}=e;return r||i("ApiPlayground"),d||i("RequestExample"),l||i("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"创建关键词自动回复规则，当用户消息包含指定关键词时自动回复。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"POST",endpoint:"/api/v1/autoreply/keyword"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于创建基于关键词的自动回复规则。可以设置多个关键词，当用户消息中包含任一关键词时触发自动回复。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "name": "价格咨询自动回复",
  "keywords": ["价格", "多少钱", "费用"],
  "replyType": "text",
  "content": "我们的产品价格请参考官网价格页面，或咨询客服了解详情",
  "isEnabled": true,
  "priority": 1
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"请求字段",children:"请求字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"name"})," (必需): 规则名称"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"keywords"})," (必需): 关键词数组，支持多个关键词"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"replyType"})," (必需): 回复类型",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"text"}),": 文本回复"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"image"}),": 图片回复"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"article"}),": 文章回复"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"card"}),": 卡片回复"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"content"})," (必需): 回复内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"isEnabled"})," (可选): 是否启用，默认 true"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"priority"})," (可选): 优先级，数字越大优先级越高"]}),`
`]}),`
`,n.jsx(e.h3,{id:"自动回复对象字段",children:"自动回复对象字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 规则唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"name"}),": 规则名称"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"keywords"}),": 关键词数组"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"replyType"}),": 回复类型"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"content"}),": 回复内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"isEnabled"}),": 是否启用"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"priority"}),": 优先级"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"hitCount"}),": 命中次数"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 创建时间"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"updatedAt"}),": 更新时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"常见问题自动回复"}),`
`,n.jsx(e.li,{children:"关键词触发回复"}),`
`,n.jsx(e.li,{children:"减轻客服工作量"}),`
`,n.jsx(e.li,{children:"提升响应速度"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"关键词匹配支持模糊匹配"}),`
`,n.jsx(e.li,{children:"如果多条规则同时匹配，按优先级和创建时间排序"}),`
`,n.jsx(e.li,{children:"建议合理设置优先级避免冲突"}),`
`,n.jsx(e.li,{children:"支持正则表达式匹配"}),`
`]}),`
`,n.jsxs(l,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "autoreply_123456",
    "name": "价格咨询自动回复",
    "keywords": ["价格", "多少钱", "费用"],
    "replyType": "text",
    "content": "我们的产品价格请参考官网价格页面，或咨询客服了解详情",
    "isEnabled": true,
    "priority": 1,
    "hitCount": 0,
    "createdAt": "2025-01-07T10:30:00Z",
    "updatedAt": "2025-01-07T10:30:00Z"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Keywords cannot be empty",
  "data": null
}
`})})]}),`
`,n.jsxs(d,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl -X POST "https://api.weiyuai.cn/api/v1/autoreply/keyword" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "name": "价格咨询自动回复",
    "keywords": ["价格", "多少钱", "费用"],
    "replyType": "text",
    "content": "我们的产品价格请参考官网价格页面",
    "isEnabled": true,
    "priority": 1
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/autoreply/keyword', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: '价格咨询自动回复',
    keywords: ['价格', '多少钱', '费用'],
    replyType: 'text',
    content: '我们的产品价格请参考官网价格页面',
    isEnabled: true,
    priority: 1
  })
});

const result = await response.json();
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/autoreply/keyword"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Content-Type": "application/json"
}
data = {
  "name": "价格咨询自动回复",
  "keywords": ["价格", "多少钱", "费用"],
  "replyType": "text",
  "content": "我们的产品价格请参考官网价格页面",
  "isEnabled": True,
  "priority": 1
}

response = requests.post(url, headers=headers, json=data)
result = response.json()
`})})]})]})}function h(s={}){const{wrapper:e}={...o(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(c,{...s})}):c(s)}function i(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,a as frontmatter};
