'''
Author: jackning 270580156@qq.com
Date: 2024-08-29 14:49:54
LastEditors: jackning 270580156@qq.com
LastEditTime: 2024-09-03 09:14:22
Description: bytedesk.com https://github.com/Bytedesk/bytedesk
  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 contact: 270580156@qq.com 
 技术/商务联系：270580156@qq.com
Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
'''
#
import logging
from typing import List
from langchain_redis import RedisConfig, RedisVectorStore
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_community.vectorstores.redis.filters import RedisFilter
from langchain.docstore.document import Document
from redisvl.query.filter import Tag
from zhipuai import ZhipuAI

from app.config import get_settings
from app.redisPubsub import publishAnswerMessage, publishAnswerFinished

zhipuAi = ZhipuAI(api_key=get_settings().ZHIPU_API_KEY)  # 填写您自己的APIKey

redis_index_schema = {
    "tag": [{"name": "kb_uid"}, {"name": "file_uid"}],
}

# https://python.langchain.com/v0.2/docs/integrations/vectorstores/redis/

class MyRedisVector:
    #
    embeddings: HuggingFaceEmbeddings
    vector_store: RedisVectorStore
    #
    def __init__(self):
        self.embeddings = HuggingFaceEmbeddings(
            model_name=get_settings().EMBEDDINGS_PATH)
        #
        config = RedisConfig(
            index_name=get_settings().REDIS_INDEX_NAME,
            key_prefix=get_settings().REDIS_KEY_PREFIX,
            redis_url=get_settings().REDIS_URL,
            redis_index_schema=redis_index_schema,
            metadata_schema=[
                {
                    "name": "kb_uid",
                    "type": "tag",
                },
                {
                    "name": "file_uid",
                    "type": "tag",
                }
            ]
        )
        self.vector_store = RedisVectorStore(self.embeddings, config=config)
        return

    def add_docs(self, docs: List[Document]) -> List[str]:
        # 将docs中的文档存储到vector_store中
        results = self.vector_store.add_documents(docs)
        logging.info(f"add_texts result: {results}")
        return results
    
    def delete_docs(self, docIds: List[str]) -> int:
        # 将docs从vector_store中删除
        # 判断docIds是否为空，空则返回0
        if not docIds:
            return 0
        # 返回被删除的文档数量
        return self.vector_store.index.drop_keys(docIds)

    # FIXME: RuntimeError: Index has not been created. Must be created before calling search
    def search_docs(self, kbUid: str, query: str) -> List[Document]:
        kb_filter = Tag("kb_uid") == kbUid
        results = self.vector_store.similarity_search(
            query, k=3, filter=kb_filter)
        # logging.info(f'search_store results {results}, {len(results)}')
        return results

    # 4.2 使用Retriever在向量库中搜索问题
    # https://python.langchain.com/docs/integrations/vectorstores/redis#redis-as-retriever
    def search_as_retriever(self, kbUid: str, query: str) -> List[Document]:
        # query向量化？langchain文档demo中没有向量化
        # kb_filter = RedisFilter.tag("kb_uid") == kbUid
        kb_filter = Tag("kb_uid") == kbUid
        # 有三种搜索算法
        # 1 默认算法
        retriever = self.vector_store.as_retriever(
            search_type="similarity",
            search_kwargs={
                "k": 3,
                "filter": kb_filter
            }
        )
        # 2 similarity_distance_threshold retriever which allows the user to specify the vector distance
        # retriever = self.vector_store.as_retriever(
        #     search_type="similarity_distance_threshold",
        #     search_kwargs={"k": 3, "distance_threshold": 0.1,
        #                    "filter": kb_filter},
        # )
        # 3 the similarity_score_threshold allows the user to define the minimum score for similar documents
        # retriever = self.vector_store.as_retriever(
        #     search_type="similarity_score_threshold",
        #     search_kwargs={"score_threshold": 0.9,
        #                    "k": 3, "filter": kb_filter}, 
        # )
        #
        results = retriever.get_relevant_documents(query)
        print(f'search_as_retriever results {results}, {len(results)}')
        return results

    # 5.Generation 将问题和搜索结果传给大模型，返回答案
    def query_llm(self, messageUid: str, threadTopic: str, kbUid: str, question: str, search_results: List[Document]):
        # logging.info(f'query_llm search_results {search_results}, {len(search_results)}')
        # 拼接
        # context = "\n".join(search_results)
        context = "\n".join([doc.page_content for doc in search_results])
        # logging.info(f'query_llm context {context}')
        # 基于本地知识问答的提示词模
        # TODO: write an english version 
        # <问题 > {query} < /问题 >
        prompt_template = f'''
        ### Human: <指令>根据已知信息，简洁和专业的来回答问题。如果无法从中得到答案，请说 “根据已知信息无法回答该问题”，不允许在答案中添加编造成分，答案请使用跟问题中同样的语言。 </指令>
        <已知信息>{ context }</已知信息>###
        Assistant:'''
        print(f'prompt_template: { { prompt_template } }')

        # https://open.bigmodel.cn/dev/api#sdk_example
        response = zhipuAi.chat.completions.create(
            model="glm-4-flash", # 免费版
            # prompt=[{"role": "user", "content": prompt_template}],
            messages=[
                {"role": "system", "content": prompt_template},
                {"role": "user", "content": question},
            ],
            top_p=0.7,
            temperature=0.3,
            stream=True
        )
        #
        counter = 0
        for chunk in response:
            # ChatCompletionChunk(id='202408291708554ae5d8eac6e94630', choices=[Choice(delta=ChoiceDelta(content='凡', role='assistant', tool_calls=None), finish_reason=None, index=0)], created=1724922535, model='glm-4-air', usage=None, extra_json=None)
            # ChatCompletionChunk(id='202408291708554ae5d8eac6e94630', choices=[Choice(delta=ChoiceDelta(content='', role='assistant', tool_calls=None), finish_reason='stop', index=0)], created=1724922535, model='glm-4-air', usage=CompletionUsage(prompt_tokens=453, completion_tokens=34, total_tokens=487), extra_json=None)
            # print(f'chunk: { chunk }')
            # INFO: query_llm chunk: , stop, glm-4-air, CompletionUsage(prompt_tokens=453, completion_tokens=34, total_tokens=487)
            #
            counter += 1
            id_to_publish = counter
            # 
            answer = chunk.choices[0].delta.content
            model = chunk.model
            created = chunk.created
            finish_reason = chunk.choices[0].finish_reason
            logging.info(
                f'query_llm: {counter} {chunk.choices[0].delta.content}, {chunk.choices[0].finish_reason}, {chunk.model}, {chunk.created}, {chunk.usage}')
            if finish_reason == 'stop':
                promptTokens = chunk.usage.prompt_tokens
                completionTokens = chunk.usage.completion_tokens
                totalTokens = chunk.usage.total_tokens
                publishAnswerFinished(
                    id=id_to_publish,
                    uid=messageUid,
                    threadTopic=threadTopic, 
                    kbUid=kbUid, 
                    question=question, 
                    answer=answer,
                    model=model, 
                    created=created,
                    promptTokens=promptTokens, 
                    completionTokens=completionTokens, 
                    totalTokens=totalTokens
                )
            else:
                publishAnswerMessage(
                    id=id_to_publish,
                    uid=messageUid,
                    threadTopic=threadTopic, 
                    kbUid=kbUid, 
                    question=question, 
                    answer=answer,
                    model=model, 
                    created=created
                )


myredisVector = MyRedisVector()
#
