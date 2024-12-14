'''
Author: jackning 270580156@qq.com
Date: 2024-08-29 09:55:30
LastEditors: jackning 270580156@qq.com
LastEditTime: 2024-08-31 07:07:31
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
import uuid
from typing import List
from fastapi import APIRouter
from app.redisVector import myredisVector
from langchain_community.document_loaders import (
    PyPDFLoader,
    TextLoader,
    UnstructuredWordDocumentLoader,
    CSVLoader,
    UnstructuredMarkdownLoader,
    UnstructuredEPubLoader,
    UnstructuredHTMLLoader,
    UnstructuredImageLoader,
    UnstructuredExcelLoader,
    UnstructuredXMLLoader,
    UnstructuredRTFLoader
)
# from langchain_community.document_loaders.unstructured import UnstructuredFileLoader
from langchain_unstructured import UnstructuredLoader
# from langchain_text_splitters import RecursiveCharacterTextSplitter
from app.textsplitter.chinese_recursive_text_splitter import ChineseRecursiveTextSplitter
from langchain.docstore.document import Document

# https://python.langchain.com/v0.2/docs/integrations/document_loaders/pypdfloader/
# https://python.langchain.com/v0.2/docs/integrations/providers/unstructured/
def load_files(filePath: str):
    if (filePath.endswith(".pdf")):
        loader = PyPDFLoader(filePath)
    elif (filePath.endswith(".txt")):
        loader = TextLoader(filePath)
    elif (filePath.endswith(".doc") or filePath.endswith(".docx")):
        loader = UnstructuredWordDocumentLoader(filePath)
    elif (filePath.endswith(".md")):
        loader = UnstructuredMarkdownLoader(filePath)
    elif (filePath.endswith(".html")):
        loader = UnstructuredHTMLLoader(filePath)
    elif (filePath.endswith(".png") or filePath.endswith(".jpg")) or filePath.endswith(".jpeg"):
        loader = UnstructuredImageLoader(filePath)
    elif (filePath.endswith(".xlsx")):
        loader = UnstructuredExcelLoader(filePath)
    elif (filePath.endswith(".csv")):
        loader = CSVLoader(filePath)
    elif (filePath.endswith(".xml")):
        loader = UnstructuredXMLLoader(filePath)
    elif (filePath.endswith(".rtf")):
        loader = UnstructuredRTFLoader(filePath)
    elif (filePath.endswith(".epub")):
        loader = UnstructuredEPubLoader(filePath)
    else:
        loader = UnstructuredLoader(filePath)
    docs = loader.load()
    # print(docs[0].metadata)
    return docs

def split_docs(docs: List[Document]) -> List[Document]:
    # Load example document
    text_splitter = ChineseRecursiveTextSplitter(
        chunk_size=500,
        chunk_overlap=50,
        length_function=len,
        is_separator_regex=False,
    )
    texts = text_splitter.split_documents(docs)
    # print(texts[0])
    return texts

# 定义一个回调函数来处理接收到的消息


def load_and_parse(fileUid: str, filePath: str, kbUid: str) -> List[str]:
    logging.info(f"load_and_parse: {filePath}")
    # 解析
    docs = load_files(filePath)
    logging.info(f"Loaded {len(docs)} documents")
    # 分块
    splited_texts = split_docs(docs)
    for doc in splited_texts:
        doc.metadata["uid"] = str(uuid.uuid4().hex)
        doc.metadata["file_uid"] = fileUid
        doc.metadata["kb_uid"] = kbUid
    logging.info(f"Split into {len(splited_texts)} chunks")
    # 存储到redis
    docIds = myredisVector.add_docs(splited_texts)
    logging.info(f"Stored in redis")
    return docIds


router = APIRouter(
    prefix='/docs',
    tags=['docs v1 apis']
)

@router.get("/test")
def test():
    return {"docs": "test"}
