import{u as c,j as n}from"./index-CJncBc6B.js";const o={title:"POST 创建LLM提供商",openapi:"POST /api/v1/provider"};function d(i){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...c(),...i.components},{ApiPlayground:r,RequestExample:l,ResponseExample:a}=e;return r||s("ApiPlayground"),l||s("RequestExample"),a||s("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"创建 LLM（大语言模型）提供商配置。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"POST",endpoint:"/api/v1/provider"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于创建 LLM 提供商配置。支持多种 LLM 提供商，如 OpenAI、Ollama、ZhipuAI、DeepSeek 等。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "name": "OpenAI",
  "type": "OPENAI",
  "apiKey": "sk-xxxxxxxxxxxx",
  "apiUrl": "https://api.openai.com/v1",
  "isEnabled": true,
  "isDefault": false
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"请求字段",children:"请求字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"name"})," (必需): 提供商名称"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"type"})," (必需): 提供商类型",`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"OPENAI"}),": OpenAI"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"OLLAMA"}),": Ollama"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"ZHIPU"}),": 智谱 AI"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"DEEPSEEK"}),": DeepSeek"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"GEMINI"}),": Google Gemini"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"BAIDU"}),": 百度文心"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"TENCENT"}),": 腾讯混元"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"MINIMAX"}),": MiniMax"]}),`
`]}),`
`]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"apiKey"})," (必需): API 密钥"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"apiUrl"})," (可选): API 地址"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"isEnabled"})," (可选): 是否启用，默认 true"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"isDefault"})," (可选): 是否为默认提供商，默认 false"]}),`
`]}),`
`,n.jsx(e.h3,{id:"提供商对象字段",children:"提供商对象字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 提供商唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"name"}),": 提供商名称"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"type"}),": 提供商类型"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"apiUrl"}),": API 地址"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"isEnabled"}),": 是否启用"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"isDefault"}),": 是否为默认提供商"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"modelCount"}),": 模型数量"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 创建时间"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"updatedAt"}),": 更新时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"配置 LLM 提供商"}),`
`,n.jsx(e.li,{children:"多提供商管理"}),`
`,n.jsx(e.li,{children:"切换默认提供商"}),`
`,n.jsx(e.li,{children:"API 密钥管理"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"API 密钥会加密存储"}),`
`,n.jsx(e.li,{children:"只能有一个默认提供商"}),`
`,n.jsx(e.li,{children:"删除提供商前需要先删除关联的模型"}),`
`,n.jsx(e.li,{children:"禁用提供商不会影响已配置的机器人"}),`
`]}),`
`,n.jsxs(a,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "provider_123456",
    "name": "OpenAI",
    "type": "OPENAI",
    "apiUrl": "https://api.openai.com/v1",
    "isEnabled": true,
    "isDefault": false,
    "modelCount": 0,
    "createdAt": "2025-01-07T10:30:00Z",
    "updatedAt": "2025-01-07T10:30:00Z"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Provider type and API key are required",
  "data": null
}
`})})]}),`
`,n.jsxs(l,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`# 创建 OpenAI 提供商
curl -X POST "https://api.weiyuai.cn/api/v1/provider" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "name": "OpenAI",
    "type": "OPENAI",
    "apiKey": "sk-xxxxxxxxxxxx",
    "apiUrl": "https://api.openai.com/v1",
    "isEnabled": true,
    "isDefault": true
  }'

# 创建 Ollama 提供商
curl -X POST "https://api.weiyuai.cn/api/v1/provider" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "name": "Ollama",
    "type": "OLLAMA",
    "apiKey": "",
    "apiUrl": "http://localhost:11434",
    "isEnabled": true
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`// 创建 LLM 提供商
async function createLLMProvider(config) {
  const response = await fetch('https://api.weiyuai.cn/api/v1/provider', {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer your_access_token_here',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(config)
  });

  const result = await response.json();
  return result.data;
}

// 使用示例 - OpenAI
const openai = await createLLMProvider({
  name: 'OpenAI',
  type: 'OPENAI',
  apiKey: 'sk-xxxxxxxxxxxx',
  apiUrl: 'https://api.openai.com/v1',
  isEnabled: true,
  isDefault: true
});

// 使用示例 - Ollama
const ollama = await createLLMProvider({
  name: 'Ollama',
  type: 'OLLAMA',
  apiKey: '',
  apiUrl: 'http://localhost:11434',
  isEnabled: true
});
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

def create_llm_provider(name, provider_type, api_key, api_url=None, is_default=False):
    url = "https://api.weiyuai.cn/api/v1/provider"
    headers = {
      "Authorization": "Bearer your_access_token_here",
      "Content-Type": "application/json"
    }

    data = {
      "name": name,
      "type": provider_type,
      "apiKey": api_key,
      "isEnabled": True,
      "isDefault": is_default
    }

    if api_url:
        data["apiUrl"] = api_url

    response = requests.post(url, headers=headers, json=data)
    result = response.json()
    return result["data"]

# 使用示例
openai = create_llm_provider(
    name="OpenAI",
    provider_type="OPENAI",
    api_key="sk-xxxxxxxxxxxx",
    api_url="https://api.openai.com/v1",
    is_default=True
)
`})})]})]})}function p(i={}){const{wrapper:e}={...c(),...i.components};return e?n.jsx(e,{...i,children:n.jsx(d,{...i})}):d(i)}function s(i,e){throw new Error("Expected component `"+i+"` to be defined: you likely forgot to import, pass, or provide it.")}export{p as default,o as frontmatter};
