import{u as p,j as e}from"./index-CJncBc6B.js";const j={title:"POST Chat Completions",description:"创建 AI 聊天完成 - OpenAI 兼容接口",openapi:"POST /api/v1/chat/completions"};function h(r){const n={a:"a",code:"code",h2:"h2",h3:"h3",li:"li",ol:"ol",p:"p",pre:"pre",ul:"ul",...p(),...r.components},{ApiPlayground:a,Expandable:t,ParamField:i,RequestExample:o,ResponseExample:l,ResponseField:s,Warning:d}=n;return a||c("ApiPlayground"),t||c("Expandable"),i||c("ParamField"),o||c("RequestExample"),l||c("ResponseExample"),s||c("ResponseField"),d||c("Warning"),e.jsxs(e.Fragment,{children:[e.jsx(n.h2,{id:"概述",children:"概述"}),`
`,e.jsx(n.p,{children:"此接口完全兼容 OpenAI Chat Completions API 格式，支持流式和非流式响应。可以使用 OpenAI SDK 或任何兼容 OpenAI 格式的客户端直接调用。"}),`
`,e.jsx("div",{style:{marginTop:24,marginBottom:24},children:e.jsx(a,{method:"POST",endpoint:"/api/v1/chat/completions"})}),`
`,e.jsx(n.h2,{id:"认证",children:"认证"}),`
`,e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`Authorization: Bearer YOUR_API_KEY
`})}),`
`,e.jsx(n.h2,{id:"请求体",children:"请求体"}),`
`,e.jsx(i,{body:"model",type:"string",required:!0,children:e.jsxs(n.p,{children:["要使用的模型 ID。可以使用 ",e.jsx(n.code,{children:"bytedesk-ai"})," 或您配置的其他模型名称。"]})}),`
`,e.jsxs(i,{body:"messages",type:"array",required:!0,children:[e.jsxs(n.p,{children:["对话消息列表，每条消息包含 ",e.jsx(n.code,{children:"role"})," 和 ",e.jsx(n.code,{children:"content"}),"。"]}),e.jsxs(t,{title:"消息对象结构",children:[e.jsx(i,{body:"role",type:"string",required:!0,children:e.jsxs(n.p,{children:["消息角色：",e.jsx(n.code,{children:"system"}),"、",e.jsx(n.code,{children:"user"})," 或 ",e.jsx(n.code,{children:"assistant"})]})}),e.jsx(i,{body:"content",type:"string",required:!0,children:e.jsx(n.p,{children:"消息内容"})}),e.jsx(i,{body:"name",type:"string",children:e.jsx(n.p,{children:"消息发送者的名称（可选）"})})]})]}),`
`,e.jsxs(i,{body:"temperature",type:"number",children:[e.jsx(n.p,{children:"控制输出的随机性，取值范围 0-2。较高的值（如 0.8）使输出更随机，较低的值（如 0.2）使输出更确定。"}),e.jsx(n.p,{children:"默认值：1"})]}),`
`,e.jsxs(i,{body:"top_p",type:"number",children:[e.jsx(n.p,{children:"使用核采样的替代方法，模型会考虑 top_p 概率质量的标记结果。"}),e.jsx(n.p,{children:"默认值：1"})]}),`
`,e.jsx(i,{body:"max_tokens",type:"integer",children:e.jsx(n.p,{children:"生成的最大 token 数量。"})}),`
`,e.jsxs(i,{body:"stream",type:"boolean",children:[e.jsxs(n.p,{children:["是否启用流式响应。设置为 ",e.jsx(n.code,{children:"true"})," 时，将通过 Server-Sent Events (SSE) 返回部分消息增量。"]}),e.jsx(n.p,{children:"默认值：false"})]}),`
`,e.jsxs(i,{body:"n",type:"integer",children:[e.jsx(n.p,{children:"为每个输入消息生成多少个聊天完成选项。"}),e.jsx(n.p,{children:"默认值：1"})]}),`
`,e.jsx(i,{body:"stop",type:"string | array",children:e.jsx(n.p,{children:"最多 4 个序列，API 将停止生成更多的 token。"})}),`
`,e.jsxs(i,{body:"presence_penalty",type:"number",children:[e.jsx(n.p,{children:"-2.0 到 2.0 之间的数字。正值会根据新 token 是否出现在文本中来惩罚它们，增加模型谈论新主题的可能性。"}),e.jsx(n.p,{children:"默认值：0"})]}),`
`,e.jsxs(i,{body:"frequency_penalty",type:"number",children:[e.jsx(n.p,{children:"-2.0 到 2.0 之间的数字。正值会根据新 token 在文本中的现有频率来惩罚它们，降低模型逐字重复相同行的可能性。"}),e.jsx(n.p,{children:"默认值：0"})]}),`
`,e.jsx(i,{body:"logit_bias",type:"object",children:e.jsx(n.p,{children:"修改指定 token 出现在完成中的可能性。"})}),`
`,e.jsx(i,{body:"user",type:"string",children:e.jsx(n.p,{children:"代表您的最终用户的唯一标识符，可以帮助 OpenAI 监控和检测滥用行为。"})}),`
`,e.jsx(n.h2,{id:"响应",children:"响应"}),`
`,e.jsx(n.h3,{id:"非流式响应",children:"非流式响应"}),`
`,e.jsx(s,{name:"id",type:"string",children:e.jsx(n.p,{children:"聊天完成的唯一标识符"})}),`
`,e.jsx(s,{name:"object",type:"string",children:e.jsxs(n.p,{children:["对象类型，固定为 ",e.jsx(n.code,{children:"chat.completion"})]})}),`
`,e.jsx(s,{name:"created",type:"integer",children:e.jsx(n.p,{children:"创建时间戳（Unix 时间）"})}),`
`,e.jsx(s,{name:"model",type:"string",children:e.jsx(n.p,{children:"使用的模型"})}),`
`,e.jsxs(s,{name:"choices",type:"array",children:[e.jsx(n.p,{children:"生成的聊天完成选项列表"}),e.jsxs(t,{title:"Choice 对象",children:[e.jsx(s,{name:"index",type:"integer",children:e.jsx(n.p,{children:"选项的索引"})}),e.jsxs(s,{name:"message",type:"object",children:[e.jsx(n.p,{children:"生成的消息"}),e.jsxs(t,{title:"Message 对象",children:[e.jsx(s,{name:"role",type:"string",children:e.jsxs(n.p,{children:["消息角色，通常为 ",e.jsx(n.code,{children:"assistant"})]})}),e.jsx(s,{name:"content",type:"string",children:e.jsx(n.p,{children:"消息内容"})})]})]}),e.jsx(s,{name:"finish_reason",type:"string",children:e.jsxs(n.p,{children:["完成原因：",e.jsx(n.code,{children:"stop"}),"、",e.jsx(n.code,{children:"length"}),"、",e.jsx(n.code,{children:"content_filter"})," 或 ",e.jsx(n.code,{children:"null"})]})})]})]}),`
`,e.jsxs(s,{name:"usage",type:"object",children:[e.jsx(n.p,{children:"Token 使用情况统计"}),e.jsxs(t,{title:"Usage 对象",children:[e.jsx(s,{name:"prompt_tokens",type:"integer",children:e.jsx(n.p,{children:"提示消息使用的 token 数"})}),e.jsx(s,{name:"completion_tokens",type:"integer",children:e.jsx(n.p,{children:"生成完成使用的 token 数"})}),e.jsx(s,{name:"total_tokens",type:"integer",children:e.jsx(n.p,{children:"总 token 数"})})]})]}),`
`,e.jsx(n.h3,{id:"流式响应",children:"流式响应"}),`
`,e.jsxs(n.p,{children:["当 ",e.jsx(n.code,{children:"stream=true"})," 时，响应将通过 Server-Sent Events (SSE) 格式返回。每个事件包含一个 JSON 对象："]}),`
`,e.jsx(s,{name:"id",type:"string",children:e.jsx(n.p,{children:"聊天完成块的唯一标识符"})}),`
`,e.jsx(s,{name:"object",type:"string",children:e.jsxs(n.p,{children:["对象类型，固定为 ",e.jsx(n.code,{children:"chat.completion.chunk"})]})}),`
`,e.jsx(s,{name:"created",type:"integer",children:e.jsx(n.p,{children:"创建时间戳（Unix 时间）"})}),`
`,e.jsx(s,{name:"model",type:"string",children:e.jsx(n.p,{children:"使用的模型"})}),`
`,e.jsxs(s,{name:"choices",type:"array",children:[e.jsx(n.p,{children:"流式选项列表"}),e.jsxs(t,{title:"Stream Choice 对象",children:[e.jsx(s,{name:"index",type:"integer",children:e.jsx(n.p,{children:"选项的索引"})}),e.jsxs(s,{name:"delta",type:"object",children:[e.jsx(n.p,{children:"消息增量"}),e.jsxs(t,{title:"Delta 对象",children:[e.jsx(s,{name:"role",type:"string",children:e.jsx(n.p,{children:"消息角色（仅在第一个块中出现）"})}),e.jsx(s,{name:"content",type:"string",children:e.jsx(n.p,{children:"内容增量"})})]})]}),e.jsx(s,{name:"finish_reason",type:"string",children:e.jsx(n.p,{children:"完成原因，在最后一个块中出现"})})]})]}),`
`,e.jsxs(n.p,{children:["流结束时将发送 ",e.jsx(n.code,{children:"data: [DONE]"})," 消息。"]}),`
`,e.jsx(n.h2,{id:"代码示例",children:"代码示例"}),`
`,e.jsxs(o,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl https://api.weiyuai.cn/api/v1/chat/completions \\
  -H "Content-Type: application/json" \\
  -H "Authorization: Bearer YOUR_API_KEY" \\
  -d '{
    "model": "bytedesk-ai",
    "messages": [
      {
        "role": "system",
        "content": "你是一个有帮助的助手。"
      },
      {
        "role": "user",
        "content": "解释一下什么是人工智能？"
      }
    ],
    "temperature": 0.7,
    "max_tokens": 500
  }'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import openai

client = openai.OpenAI(
    api_key="YOUR_API_KEY",
    base_url="https://api.weiyuai.cn/api/v1"
)

response = client.chat.completions.create(
    model="bytedesk-ai",
    messages=[
        {"role": "system", "content": "你是一个有帮助的助手。"},
        {"role": "user", "content": "解释一下什么是人工智能？"}
    ],
    temperature=0.7,
    max_tokens=500
)

print(response.choices[0].message.content)
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`import OpenAI from 'openai';

const openai = new OpenAI({
  apiKey: 'YOUR_API_KEY',
  baseURL: 'https://api.weiyuai.cn/api/v1'
});

const completion = await openai.chat.completions.create({
  model: 'bytedesk-ai',
  messages: [
    { role: 'system', content: '你是一个有帮助的助手。' },
    { role: 'user', content: '解释一下什么是人工智能？' }
  ],
  temperature: 0.7,
  max_tokens: 500
});

console.log(completion.choices[0].message.content);
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-java",children:`import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import java.util.*;

public class ChatExample {
    public static void main(String[] args) {
        OpenAiService service = new OpenAiService("YOUR_API_KEY");
        service.setBaseUrl("https://api.weiyuai.cn/api/v1");

        List<ChatMessage> messages = Arrays.asList(
            new ChatMessage("system", "你是一个有帮助的助手。"),
            new ChatMessage("user", "解释一下什么是人工智能？")
        );

        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model("bytedesk-ai")
            .messages(messages)
            .temperature(0.7)
            .maxTokens(500)
            .build();

        var response = service.createChatCompletion(request);
        System.out.println(response.getChoices().get(0).getMessage().getContent());
    }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-go",children:`package main

import (
    "context"
    "fmt"
    "github.com/sashabaranov/go-openai"
)

func main() {
    config := openai.DefaultConfig("YOUR_API_KEY")
    config.BaseURL = "https://api.weiyuai.cn/api/v1"
    client := openai.NewClientWithConfig(config)

    resp, err := client.CreateChatCompletion(
        context.Background(),
        openai.ChatCompletionRequest{
            Model: "bytedesk-ai",
            Messages: []openai.ChatCompletionMessage{
                {
                    Role:    openai.ChatMessageRoleSystem,
                    Content: "你是一个有帮助的助手。",
                },
                {
                    Role:    openai.ChatMessageRoleUser,
                    Content: "解释一下什么是人工智能？",
                },
            },
            Temperature: 0.7,
            MaxTokens:   500,
        },
    )

    if err != nil {
        fmt.Printf("ChatCompletion error: %v\\n", err)
        return
    }

    fmt.Println(resp.Choices[0].Message.Content)
}
`})})]}),`
`,e.jsxs(l,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "id": "chatcmpl-abc123",
  "object": "chat.completion",
  "created": 1677652288,
  "model": "bytedesk-ai",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "人工智能（Artificial Intelligence，简称 AI）是计算机科学的一个分支，致力于创建能够执行通常需要人类智能的任务的系统。这些任务包括学习、推理、问题解决、感知和语言理解。\\n\\nAI 可以分为两大类：\\n1. 弱人工智能（或狭义 AI）：专注于特定任务的 AI 系统\\n2. 强人工智能（或通用 AI）：理论上能够执行任何人类智能任务的系统\\n\\n当前的 AI 技术主要是弱人工智能，应用于图像识别、自然语言处理、推荐系统等领域。"
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 25,
    "completion_tokens": 156,
    "total_tokens": 181
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "error": {
    "message": "Invalid request: missing required field 'messages'",
    "type": "invalid_request_error",
    "code": "missing_required_field"
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "error": {
    "message": "Invalid API key",
    "type": "authentication_error",
    "code": "invalid_api_key"
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "error": {
    "message": "Rate limit exceeded",
    "type": "rate_limit_error",
    "code": "rate_limit_exceeded"
  }
}
`})})]}),`
`,e.jsx(n.h2,{id:"流式响应示例",children:"流式响应示例"}),`
`,e.jsxs(l,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`data: {"id":"chatcmpl-abc123","object":"chat.completion.chunk","created":1677652288,"model":"bytedesk-ai","choices":[{"index":0,"delta":{"role":"assistant","content":""},"finish_reason":null}]}

data: {"id":"chatcmpl-abc123","object":"chat.completion.chunk","created":1677652288,"model":"bytedesk-ai","choices":[{"index":0,"delta":{"content":"人工"},"finish_reason":null}]}

data: {"id":"chatcmpl-abc123","object":"chat.completion.chunk","created":1677652288,"model":"bytedesk-ai","choices":[{"index":0,"delta":{"content":"智能"},"finish_reason":null}]}

data: {"id":"chatcmpl-abc123","object":"chat.completion.chunk","created":1677652288,"model":"bytedesk-ai","choices":[{"index":0,"delta":{"content":"（"},"finish_reason":null}]}

data: [DONE]
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "error": {
    "message": "No chat model available",
    "type": "service_unavailable",
    "param": null,
    "code": null
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "error": {
    "message": "Internal server error",
    "type": "internal_error",
    "code": "internal_error"
  }
}
`})})]}),`
`,e.jsx(n.h2,{id:"错误响应说明",children:"错误响应说明"}),`
`,e.jsx(n.h3,{id:"常见错误类型",children:"常见错误类型"}),`
`,e.jsx(s,{name:"service_unavailable",type:"string",children:e.jsx(n.p,{children:"服务不可用，没有配置聊天模型"})}),`
`,e.jsx(s,{name:"internal_error",type:"string",children:e.jsx(n.p,{children:"内部服务器错误"})}),`
`,e.jsx(s,{name:"stream_error",type:"string",children:e.jsx(n.p,{children:"流式响应过程中发生错误"})}),`
`,e.jsx(s,{name:"invalid_request_error",type:"string",children:e.jsx(n.p,{children:"请求参数无效"})}),`
`,e.jsx(s,{name:"authentication_error",type:"string",children:e.jsx(n.p,{children:"认证失败，API 密钥无效"})}),`
`,e.jsx(n.h2,{id:"注意事项",children:"注意事项"}),`
`,e.jsx(d,{children:e.jsxs(n.ol,{children:[`
`,e.jsx(n.li,{children:"确保在请求头中包含有效的 Bearer Token 进行认证"}),`
`,e.jsx(n.li,{children:"流式响应需要客户端支持 Server-Sent Events (SSE)"}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"max_tokens"})," 参数应根据实际需求设置，过大可能导致响应超时"]}),`
`,e.jsx(n.li,{children:"建议在生产环境中实现错误重试机制"}),`
`]})}),`
`,e.jsx(n.h2,{id:"与-openai-的兼容性",children:"与 OpenAI 的兼容性"}),`
`,e.jsx(n.p,{children:"此接口完全兼容 OpenAI Chat Completions API v1 格式，您可以："}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"使用官方 OpenAI SDK（Python, Node.js, Go 等）"}),`
`,e.jsxs(n.li,{children:["将现有的 OpenAI 应用迁移到本服务，只需修改 ",e.jsx(n.code,{children:"base_url"})]}),`
`,e.jsx(n.li,{children:"使用任何支持 OpenAI 格式的第三方工具或库"}),`
`]}),`
`,e.jsx(n.h2,{id:"相关链接",children:"相关链接"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:e.jsx(n.a,{href:"https://platform.openai.com/docs/api-reference/chat",children:"OpenAI Chat Completions API 文档"})}),`
`,e.jsx(n.li,{children:e.jsx(n.a,{href:"/api-reference/auth/login",children:"认证方式"})}),`
`]})]})}function m(r={}){const{wrapper:n}={...p(),...r.components};return n?e.jsx(n,{...r,children:e.jsx(h,{...r})}):h(r)}function c(r,n){throw new Error("Expected component `"+r+"` to be defined: you likely forgot to import, pass, or provide it.")}export{m as default,j as frontmatter};
