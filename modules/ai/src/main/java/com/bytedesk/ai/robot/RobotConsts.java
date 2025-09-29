/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-13 17:11:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-23 14:28:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.RedisConsts;

public class RobotConsts {
    private RobotConsts() {
    }

    // 定义 Redis 队列的 key
    public static final String ROBOT_FAQ_QUEUE_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "robot:faq:queue";
    // airline key
    public static final String ROBOT_INIT_DEMO_AIRLINE_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX
            + "robot:init:demo:airline";
    // bytedesk key
    public static final String ROBOT_INIT_DEMO_BYTEDESK_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX
            + "robot:init:demo:bytedesk";
    // shopping key
    public static final String ROBOT_INIT_DEMO_SHOPPING_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX
            + "robot:init:demo:shopping";
    //
    public static final String CATEGORY_JOB = I18Consts.I18N_PREFIX + "JOB";
    public static final String CATEGORY_LANGUAGE = I18Consts.I18N_PREFIX + "LANGUAGE";
    public static final String CATEGORY_TOOL = I18Consts.I18N_PREFIX + "TOOL";
    public static final String CATEGORY_WRITING = I18Consts.I18N_PREFIX + "WRITING";

    // robot name
    public static final String ROBOT_NAME_VOID_AGENT = "void_agent"; // 空白智能体
    public static final String ROBOT_NAME_CUSTOMER_SERVICE = "customer_service"; // 客服问答
    public static final String ROBOT_NAME_QUERY_EXPANSION = "query_expansion"; // 问题扩写
    public static final String ROBOT_NAME_INTENT_REWRITE = "intent_rewrite"; // 意图改写
    public static final String ROBOT_NAME_INTENT_CLASSIFICATION = "intent_classification"; // 意图识别
    public static final String ROBOT_NAME_EMOTION_ANALYSIS = "emotion_analysis"; // 情绪分析
    public static final String ROBOT_NAME_ROBOT_INSPECTION = "robot_inspection"; // 机器人质检
    public static final String ROBOT_NAME_AGENT_INSPECTION = "agent_inspection"; // 客服质检
    //
    public static final String ROBOT_NAME_TICKET_ASSISTANT = "ticket_assistant"; // 工单助手
    public static final String ROBOT_NAME_TICKET_SOLUTION_RECOMMENDATION = "ticket_solution_recommendation"; // 工单解决方案推荐
    public static final String ROBOT_NAME_TICKET_SUMMARY = "ticket_summary"; // 工单小结
    public static final String ROBOT_NAME_TICKET_GENERATE = "ticket_generate"; // 工单生成
    //
    public static final String ROBOT_NAME_VISITOR_PORTRAIT = "visitor_portrait"; // 访客画像
    public static final String ROBOT_NAME_VISITOR_INVITATION = "visitor_invitation"; // 接客助手
    public static final String ROBOT_NAME_VISITOR_RECOMMENDATION = "visitor_recommendation"; // 导购助手
    public static final String ROBOT_NAME_CUSTOMER_ASSISTANT = "customer_assistant"; // 客服助手
    public static final String ROBOT_NAME_PRE_SALE_CUSTOMER_ASSISTANT = "pre_sale_customer_assistant"; // 售前客服
    public static final String ROBOT_NAME_AFTER_SALE_CUSTOMER_ASSISTANT = "after_sale_customer_assistant"; // 售后客服
    public static final String ROBOT_NAME_LOGISTICS_CUSTOMER_ASSISTANT = "logistics_customer_assistant"; // 物流客服
    public static final String ROBOT_NAME_LANGUAGE_TRANSLATION = "language_translation"; // 语言翻译
    public static final String ROBOT_NAME_LANGUAGE_RECOGNITION = "language_recognition"; // 语言识别
    public static final String ROBOT_NAME_SEMANTIC_ANALYSIS = "semantic_analysis"; // 语义分析
    public static final String ROBOT_NAME_ENTITY_RECOGNITION = "entity_recognition"; // 实体识别
    public static final String ROBOT_NAME_SENTIMENT_ANALYSIS = "sentiment_analysis"; // 情感分析
    public static final String ROBOT_NAME_THREAD_CLASSIFICATION = "thread_classification"; // 会话分类
    public static final String ROBOT_NAME_CUSTOMER_SERVICE_EXPERT = "customer_service_expert"; // 客服专家
    public static final String ROBOT_NAME_FAQ_GENERATE = "faq_generate"; // 生成FAQ
    public static final String ROBOT_NAME_GENERATE_WECHAT_ARTICLE = "generate_wechat_article"; // 生成公众号文章
    public static final String ROBOT_NAME_GENERATE_XIAOHONGSHU_ARTICLE = "generate_xiaohongshu_article"; // 生成小红书文章
    public static final String ROBOT_NAME_AGENT_ASSISTANT = "agent_assistant"; // 客服助手
    public static final String ROBOT_NAME_THREAD_SUMMARY = "thread_summary"; // 会话小结
    public static final String ROBOT_NAME_THREAD_COMPLETION = "thread_completion"; // 输入补全
    public static final String ROBOT_NAME_CHINESE_WORD_SEGMENTATION = "chinese_word_segmentation"; // 中文分词
    public static final String ROBOT_NAME_PART_OF_SPEECH_TAGGING = "part_of_speech_tagging"; // 词性标注
    public static final String ROBOT_NAME_DEPENDENCY_PARSING = "dependency_parsing"; // 依存句法分析
    public static final String ROBOT_NAME_CONSTITUENCY_PARSING = "constituency_parsing"; // 成分句法分析
    public static final String ROBOT_NAME_SEMANTIC_DEPENDENCY_ANALYSIS = "semantic_dependency_analysis"; // 语义依存分析
    public static final String ROBOT_NAME_SEMANTIC_ROLE_LABELING = "semantic_role_labeling"; // 语义角色标注
    public static final String ROBOT_NAME_ABSTRACT_MEANING_REPRESENTATION = "abstract_meaning_representation"; // 抽象意义表示
    public static final String ROBOT_NAME_COREFERENCE_RESOLUTION = "coreference_resolution"; // 指代消解
    public static final String ROBOT_NAME_SEMANTIC_TEXT_SIMILARITY = "semantic_text_similarity"; // 语义文本相似度
    public static final String ROBOT_NAME_TEXT_STYLE_TRANSFER = "text_style_transfer"; // 文本风格转换
    public static final String ROBOT_NAME_KEYWORD_EXTRACTION = "keyword_extraction"; // 关键词短语提取
    public static final String ROBOT_NAME_TEXT_CORRECTION = "text_correction"; // 文本纠错
    public static final String ROBOT_NAME_TEXT_CLASSIFICATION = "text_classification"; // 文本分类
    public static final String ROBOT_NAME_FAQ_SIMILAR_QUESTIONS = "faq_similar_questions"; // FAQ相似问题生成
    //
    public static final String ROBOT_NAME_FALLBACK_RESPONSE = "fallback_response"; // 后备回复
    public static final String ROBOT_NAME_QUERY_REWRITE = "query_rewrite"; // 查询重写
    public static final String ROBOT_NAME_SUMMARY_GENERATION = "summary_generation"; // 摘要生成
    public static final String ROBOT_NAME_THREAD_TITLE_GENERATION = "thread_title_generation"; // 会话标题生成
    public static final String ROBOT_NAME_CONTEXT_TEMPLATE_SUMMARY = "context_template_summary"; // 上下文模板摘要
    public static final String ROBOT_NAME_ENTITY_EXTRACTION = "entity_extraction"; // 实体提取
    public static final String ROBOT_NAME_RELATIONSHIP_EXTRACTION = "relationship_extraction"; // 关系提取
    public static final String ROBOT_NAME_QUESTION_SUGGEST = "question_suggest"; // 问题建议
    public static final String ROBOT_NAME_OCR_EXTRACTION = "ocr_extraction"; // OCR文字提取

    // 默认客服问答提示词
    public static final String ROBOT_LLM_DEFAULT_PROMPT = """
            角色：资深客服专家;
            背景：有专业客服经验;
            任务：根据上下文中提到的内容，对提出的问题给出有用、详细、礼貌的回答;
            要求：
            1. 根据搜索结果和历史聊天记录回答客户提出的问题，
            2. 安抚客户情绪，
            3. 提升客户满意度;
            4. 严禁回答政治、暴力、色情等违法违规问题;
            5. 仅根据上下文内容回答问题，不要添加其他内容;
            6. 如果上下文内容不完整，无法回答问题，直接回答“未查找到相关问题答案”，不要猜测;
            """;

    // 默认知识库对话提示词
    public static final String ROBOT_LLM_CHAT_PROMPT = """
            你是一个专业、友好的AI助手。现在用户提出的问题超出了你的知识库范围，你需要生成一个礼貌且有帮助的回复。

            ## 回复要求
            - 诚实承认你无法提供准确答案
            - 简洁友好，不要过度道歉
            - 可以提供相关的建议或替代方案
            - 回复控制在50字以内
            - 使用礼貌、专业的语气

            ## Few-shot示例

            用户问题: 今天杭州西湖的游客数量是多少？
            回复: 抱歉，我无法获取实时的杭州西湖游客数据。您可以通过杭州旅游官网或相关APP查询这一信息。

            用户问题: 张教授的新论文发表了吗？
            回复: 我没有张教授的最新论文信息。建议您查询学术数据库或直接联系张教授获取最新动态。

            用户问题: 我的银行卡号是多少？
            回复: 作为AI助手，我无法获取您的个人银行信息。请登录您的银行APP或联系银行客服获取相关信息。

            ## 用户当前的问题是:
            {{.Query}}
            """;

    // 默认重写提示词
    public static final String ROBOT_LLM_DEFAULT_REWRITE_PROMPT = """
            你是一个专注于指代消解和省略补全的智能助手，你的任务是根据历史对话上下文，清晰识别用户问题中的代词并替换为明确的主语，同时补全省略的关键信息。

            ## 改写目标
            请根据历史对话，对当前用户问题进行改写，目标是：
            - 进行指代消解，将"它"、"这个"、"那个"、"他"、"她"、"它们"、"他们"、"她们"等代词替换为明确的主语
            - 补全省略的关键信息，确保问题语义完整
            - 保持问题的原始含义和表达方式不变
            - 改写后必须也是一个问题
            - 改写后的问题字数控制在30字以内
            - 仅输出改写后的问题，不要输出任何解释，更不要尝试回答该问题，后面有其他助手回去解答此问题

            ## Few-shot示例

            示例1:
            历史对话:
            用户: 微信支付有哪些功能？
            助手: 微信支付的主要功能包括转账、付款码、收款、信用卡还款等多种支付服务。

            用户问题: 它的安全性
            改写后: 微信支付的安全性

            示例2:
            历史对话:
            用户: 苹果手机电池不耐用怎么办？
            助手: 您可以通过降低屏幕亮度、关闭后台应用和定期更新系统来延长电池寿命。

            用户问题: 这样会影响使用体验吗？
            改写后: 降低屏幕亮度和关闭后台应用是否影响使用体验

            示例3:
            历史对话:
            用户: 如何制作红烧肉？
            助手: 红烧肉的制作需要先将肉块焯水，然后加入酱油、糖等调料慢炖。

            用户问题: 需要炖多久？
            改写后: 红烧肉需要炖多久

            示例4:
            历史对话:
            用户: 北京到上海的高铁票价是多少？
            助手: 北京到上海的高铁票价根据车次和座位类型不同，二等座约为553元，一等座约为933元。

            用户问题: 时间呢？
            改写后: 北京到上海的高铁时长

            示例5:
            历史对话:
            用户: 如何注册微信账号？
            助手: 注册微信账号需要下载微信APP，输入手机号，接收验证码，然后设置昵称和密码。

            用户问题: 国外手机号可以吗？
            改写后: 国外手机号是否可以注册微信账号
            """;

    public static final String PROMPT_LLM_FAQ_GENERATE_TEMPLATE = """
            基于以下给定的文本，生成一组高质量的问答对。请遵循以下指南:

            1. 问题部分：
            - 为同一个主题创建多个不同表述的问题，确保问题的多样性
            - 每个问题应考虑用户可能的多种问法，例如：
              - 直接询问（如"什么是...？"）
              - 请求确认（如"是否可以说...？"）
              - 如何做（如"如何实现...？"）
              - 为什么（如"为什么需要...？"）
              - 比较类（如"...和...有什么区别？"）

            2. 答案部分：
            - 答案应该准确、完整且易于理解
            - 使用简洁清晰的语言
            - 适当添加示例说明
            - 可以包含关键步骤或要点
            - 必要时提供相关概念解释

            3. 格式要求：
            - 以JSON数组形式输出
            - 每个问答对包含question和answer字段
            - 可选添加type字段标识问题类型
            - 可选添加tags字段标识相关标签

            4. 质量控制：
            - 确保问答对之间不重复
            - 问题应该有实际意义和价值
            - 答案需要准确且有帮助
            - 覆盖文本中的重要信息点

            给定文本：
            {chunk}

            请基于这个文本生成问答对
            """;

    public static final String PROMPT_LLM_SIMPLE = """
            根据提供的文档信息回答问题，文档信息如下:
            {context}
            问题:
            {query}
            当用户提出的问题无法根据文档内容进行回复或者你也不清楚时，回复:未查找到相关问题答案.
            """;

    public static final String PROMPT_LLM_ANSWER_WITH_QA = """
              任务描述：根据用户的查询和文档信息回答问题，并结合历史聊天记录生成简要的回答。

              用户查询: {query}

              历史聊天记录: {history}

              搜索结果: {context}

              请根据以上信息生成一个简单明了的回答，确保信息准确且易于理解。
              当用户提出的问题无法根据文档内容进行回复或者你也不清楚时，回复:未查找到相关问题答案.
              另外，请提供更多相关的问答对。
              回答内容请以JSON格式输出，格式如下：
              {
                "answer": "回答内容",
                "additional_qa_pairs": [
                    {"question": "相关问题1", "answer": "相关答案1"},
                    {"question": "相关问题2", "answer": "相关答案2"}
                ]
              }
            """;

    public static final String PROMPT_LLM_SPLIT_WITH_QA_TEMPLATE = """
              需要将以下文本切段，并根据文本内容整理 FAQ。
              文本切段的要求是：
              1. 保证语义的完整性：不要将一个完整的句子切断，不要把表达同一个语义的不同句子分割开
              2. 保留足够多的上下文信息：如果切割后的文本段必须依赖上下文信息才能表达正确的语义，那就不能切割开
              3. 过滤无效信息：过滤格式化内容如大量填充的空格，过滤不完整的段落以及过滤
              4. 移除 markdown 内容的标记，层次关系按 1，2，3 以及 1.1，2.1 来标记
              5. 只保留文本内容，移除链接等信息
              6. 不要对标题单独切段：仅对正文进行切段，标题可以与正文合并一起，或者是作为上下文信息扩充进正文分段内
              7. 切段后的文本如果不是一个完整的句子，请修改为一个完整表述的句子

              FAQ 的要求是：
              1. FAQ 的问题范围尽量小，提出比较原子的问题
              2. 对主要内容整理 FAQ，FAQ 的数量不大于 20 个

              直接输出整理后的结果，结果格式为：
              {
                  "Chunks":["<chunk_0>", "<chunk_1>"],
                  "FAQs": [
                  {"Question": "<question_0>", "Answer": "<answer_0>"},
                  {"Question": "<question_0>", "Answer": "<answer_0>"}
                  ]
              }

              文本内容:
              {content}

            """;

    // 媒体消息标记前缀（用于在 Prompt 中传递媒体类型与 URL 给下游多模态服务）
    public static final String BD_MEDIA_PREFIX = "__BD_MEDIA__:";
    // 媒体 JSON 字段名
    public static final String BD_MEDIA_FIELD_TYPE = "type";
    public static final String BD_MEDIA_FIELD_URL = "url";

}
