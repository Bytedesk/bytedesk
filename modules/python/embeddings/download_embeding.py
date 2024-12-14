# 说明： https://developer.aliyun.com/ask/530783
# 模型地址： https://modelscope.cn/models/ZhipuAI/chatglm2-6b
# 下载地址：/Users/ningjinpeng/.cache/modelscope/
# /Users/ningjinpeng/.cache/modelscope/hub/ZhipuAI/chatglm2-6b
# pip install modelscope
#
# from modelscope.models import Model
from modelscope import snapshot_download
# import modelscope

# print(modelscope.version.__release_datetime__)
# model = Model.from_pretrained('ZhipuAI/chatglm2-6b')
# model = Model.from_pretrained('Jerry0/M3E-large', cache_dir='./models')
# model_dir = snapshot_download("damo/nlp_gte_sentence-embedding_chinese-base", revision = 'master')
# /Users/ningjinpeng/.cache/modelscope/hub/damo/nlp_gte_sentence-embedding_chinese-base
# model_dir = snapshot_download("Xorbits/bge-small-zh-v1.5", revision ='master')
# /Users/ningjinpeng/.cache/modelscope/hub/Xorbits/bge-small-zh-v1.5
# model_dir = snapshot_download("Xorbits/bge-small-en-v1.5", revision='master')
# /Users/ningjinpeng/.cache/modelscope/hub/Xorbits/bge-small-en-v1.5
model_dir = snapshot_download("Xorbits/bge-large-zh-v1.5", revision='master')
# /Users/ningjinpeng/.cache/modelscope/hub/Xorbits/bge-large-zh-v1.5
print(model_dir)
