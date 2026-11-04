# advisor

在 Spring AI 里，advisor 可以把它理解成挂在 ChatClient 调用链上的“拦截器/增强器”。它的主要用途不是直接生成回答，而是在请求发给模型之前、以及模型返回结果之后，统一插入一些通用能力，比如补上下文、做 RAG、管理记忆、记录日志、做安全拦截、控制工具调用流程。这个定位在文档里说得很明确：advisors.adoc。

从代码上看，advisor 的核心接口很简单：
Advisor 只定义名称和顺序，
CallAdvisor 处理普通调用，
StreamAdvisor 处理流式调用。
如果你实现的是常见“前处理 + 调下一个 + 后处理”模式，通常直接基于 BaseAdvisor 就行，它已经把 before/after 模板封装好了。

真正执行时，Spring AI 会把多个 advisor 串成一个链。这个链在 DefaultAroundAdvisorChain 里实现。它的行为是：
    1. 按 order 排序，数值越小越先处理请求。
    2. 请求沿着链往里走，每个 advisor 都可以修改 ChatClientRequest。
    3. 最后一个节点才真正调用模型。
    4. 响应再沿链往外返回，每个 advisor 都可以再处理 ChatClientResponse。
所以 advisor 最核心的价值，就是把“和模型交互前后要做的横切逻辑”从业务代码里抽出来，变成可复用、可组合、可排序的组件。

Spring AI 里常见的 advisor 用途大概有这几类：

1. 记忆管理
MessageChatMemoryAdvisor 会在请求前把历史消息读出来塞进 prompt，请求后再把新的用户消息和助手回复写回 memory。
VectorStoreChatMemoryAdvisor 则是把长期记忆从向量库中检索出来，拼到 system prompt 里。

2. RAG 检索增强
QuestionAnswerAdvisor 会先去 VectorStore 做相似度检索，再把检索到的文档上下文拼进用户问题里，让模型“带资料回答”。这一类本质上就是把检索步骤插到模型调用之前。对应说明见 retrieval-augmented-generation.adoc。

3. 工具调用编排
ToolCallingAdvisor 比较特殊，它不是简单前后增强，而是一个“递归 advisor”。它负责整个 tool calling loop：把工具定义发给模型，检查模型是否发起工具调用，执行工具，再把工具结果回填给模型，直到模型输出最终答案。相关设计说明在 tool-calling-advisor.adoc。

4. 日志和安全
SimpleLoggerAdvisor 用来统一打印请求和响应。
SafeGuardAdvisor 可以在请求进入模型前直接拦截敏感内容，必要时甚至不再继续调用后续链路。

你当前打开的 ToolSearchToolCallingAdvisor.java 就是一个很典型的“基于 ToolCallingAdvisor 做定制扩展”的例子。它的用途不是替代工具调用，而是在工具调用循环外面再包一层“工具搜索能力”：

1. 会话开始时，把当前可用工具建立索引。
2. 自动暴露一个 toolSearchTool，让模型先按自然语言搜索“我该用哪个工具”。
3. 从 toolSearchTool 的响应中抽取工具名。
4. 在下一轮工具调用里，只把搜索命中的那些工具注入给模型。

也就是说，这个 advisor 解决的是“工具太多，不能每次都全量暴露给模型”的问题。它把“先检索工具，再调用工具”这套流程封装进 advisor，而不是让业务代码自己拼。

如果用一句话概括：

advisor 的用途，就是把与大模型调用相关的通用编排逻辑，放到 ChatClient 调用链中统一处理，让业务层只关心提问和结果，不必手工管理 memory、RAG、日志、安全和 tool loop。
